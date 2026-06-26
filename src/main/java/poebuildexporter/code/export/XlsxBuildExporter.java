package poebuildexporter.code.export;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import poebuildexporter.code.ItemData.Item;
import poebuildexporter.code.build.Build;
import poebuildexporter.code.poeapi.PathOfExileTradeApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class XlsxBuildExporter implements FileBuildExporter {

    private File outputFile;

    private static final Logger log = LoggerFactory.getLogger(XlsxBuildExporter.class);

    private static final List<String> HEADERS = List.of(
            "Item Name", "Base Type", "Rarity", "iLvl", "Links",
            "PDPS", "EDPS", "ES", "Evasion", "Armour",
            "Total Listings", "Trade URL"
    );

    @Override
    public String getDisplayName() {
        return "Excel Spreadsheet (.xlsx)";
    }

    @Override
    public void setOutputFile(File file) {
        this.outputFile = file;
    }

    @Override
    public void export(Map<Build, Map<Item, PathOfExileTradeApiResponse>> results) {
        if (outputFile == null) {
            JOptionPane.showMessageDialog(null, "No output file selected.",
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = outputFile;
        if (!file.getName().endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = buildHeaderStyle(workbook);
            CellStyle linkStyle = buildLinkStyle(workbook);

            for (Map.Entry<Build, Map<Item, PathOfExileTradeApiResponse>> buildEntry : results.entrySet()) {
                writeSheet(workbook, buildEntry.getKey(), buildEntry.getValue(), headerStyle, linkStyle);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            log.info("Exported results to {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to write xlsx file", e);
            JOptionPane.showMessageDialog(null, "Failed to write file: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void writeSheet(XSSFWorkbook workbook, Build build,
            Map<Item, PathOfExileTradeApiResponse> itemResults,
            CellStyle headerStyle, CellStyle linkStyle) {
        String sheetName = build.getName() != null ? build.getName() : "Build";
        if (sheetName.length() > 31) {
            sheetName = sheetName.substring(0, 31);
        }
        XSSFSheet sheet = workbook.createSheet(sheetName);

        writeHeaderRow(sheet, headerStyle);

        int rowIdx = 1;
        for (Map.Entry<Item, PathOfExileTradeApiResponse> entry : itemResults.entrySet()) {
            writeItemRow(sheet, rowIdx++, entry.getKey(), entry.getValue(), build, linkStyle);
        }

        for (int col = 0; col < HEADERS.size(); col++) {
            sheet.autoSizeColumn(col);
        }
    }

    private void writeHeaderRow(XSSFSheet sheet, CellStyle style) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < HEADERS.size(); i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(HEADERS.get(i));
            cell.setCellStyle(style);
        }
    }

    private void writeItemRow(XSSFSheet sheet, int rowIdx, Item item,
            PathOfExileTradeApiResponse response, Build build, CellStyle linkStyle) {
        Row row = sheet.createRow(rowIdx);
        String tradeUrl = build.getTradeApi().getTradeWebUrl()
                + build.getTradeApi().getLeague() + "/"
                + (response != null && response.getId() != null ? response.getId() : "");

        row.createCell(0).setCellValue(nullSafe(item.getName()));
        row.createCell(1).setCellValue(nullSafe(item.getBaseType()));
        row.createCell(2).setCellValue(nullSafe(item.getRarity()));
        row.createCell(3).setCellValue(item.getIlvl());
        row.createCell(4).setCellValue(item.getLinks());
        row.createCell(5).setCellValue(item.getPDPS());
        row.createCell(6).setCellValue(item.getEDPS());
        row.createCell(7).setCellValue(item.getEs());
        row.createCell(8).setCellValue(item.getEvasion());
        row.createCell(9).setCellValue(item.getArmour());
        row.createCell(10).setCellValue(response != null ? response.getTotal() : 0);

        Cell urlCell = row.createCell(11);
        urlCell.setCellValue(tradeUrl);
        if (response != null && response.getId() != null) {
            CreationHelper helper = sheet.getWorkbook().getCreationHelper();
            Hyperlink hyperlink = helper.createHyperlink(HyperlinkType.URL);
            hyperlink.setAddress(tradeUrl);
            urlCell.setHyperlink(hyperlink);
            urlCell.setCellStyle(linkStyle);
        }
    }

    private CellStyle buildHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle buildLinkStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setUnderline(Font.U_SINGLE);
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        return style;
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}
