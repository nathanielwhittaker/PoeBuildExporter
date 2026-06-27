package poebuildexporter.code.importer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;
import java.util.List;

public class XlsxFileBuildImporter extends FileBuildImporter {

    private static final Logger log = LoggerFactory.getLogger(XlsxFileBuildImporter.class);

    @Override
    protected FileNameExtensionFilter getFileNameExtensionFilter() {
        return new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx");
    }

    @Override
    public String getDisplayName() {
        return "Excel File (.xlsx)";
    }

    @Override
    public List<BuildImporterResult> getResults() {
        if (selectedFile == null) {
            return List.of();
        }
        try (XSSFWorkbook workbook = new XSSFWorkbook(selectedFile)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getLastRowNum() < 0) {
                return List.of();
            }

            Row headerRow = sheet.getRow(0);
            int urlCol = headerRow != null ? findColumn(headerRow, "url") : -1;
            int nameCol = headerRow != null ? findColumn(headerRow, "name") : -1;
            int startRow;
            if (urlCol >= 0) {
                startRow = 1;
            } else {
                startRow = 0;
                urlCol = headerRow != null && headerRow.getLastCellNum() > 1
                        ? headerRow.getLastCellNum() - 1
                        : 0;
                nameCol = -1;
            }

            List<BuildImporterResult> results = new ArrayList<>();
            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Cell urlCell = row.getCell(urlCol);
                if (urlCell == null) {
                    continue;
                }
                String url = urlCell.getStringCellValue().trim();
                if (url.isEmpty()) {
                    continue;
                }
                String name = null;
                if (nameCol >= 0) {
                    Cell nameCell = row.getCell(nameCol);
                    if (nameCell != null) {
                        String nameValue = nameCell.getStringCellValue().trim();
                        if (!nameValue.isEmpty()) {
                            name = nameValue;
                        }
                    }
                }
                results.add(new BuildImporterResult(name, url));
            }
            return results;
        } catch (Exception e) {
            log.error("Failed to read xlsx from {}", selectedFile.getAbsolutePath(), e);
            return List.of();
        }
    }

    private static int findColumn(Row headerRow, String name) {
        for (Cell cell : headerRow) {
            if (name.equalsIgnoreCase(cell.getStringCellValue().trim())) {
                return cell.getColumnIndex();
            }
        }
        return -1;
    }
}
