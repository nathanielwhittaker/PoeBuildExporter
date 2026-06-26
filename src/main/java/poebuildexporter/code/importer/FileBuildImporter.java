package poebuildexporter.code.importer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileBuildImporter implements BuildImporter {

    private static final Logger log = LoggerFactory.getLogger(FileBuildImporter.class);

    private final JPanel inputPanel;
    private final JTextField pathField;
    private final JLabel errorLabel;
    private File selectedFile = null;

    public FileBuildImporter() {
        pathField = new JTextField(35);
        pathField.setEditable(false);

        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> onBrowse());

        int numSupportTypes = FileImportType.values().length;
        String fileExtentionsListString = Arrays.toString(FileImportType.values()).replace("[", "").replace("]", "").toLowerCase();
        errorLabel = new JLabel(String.format("Unsupported file type. Only %s %s supported.", fileExtentionsListString, numSupportTypes < 2 ? "is" : "are"));
        errorLabel.setForeground(Color.RED);
        errorLabel.setVisible(false);

        JPanel fileRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileRow.add(new JLabel("File:"));
        fileRow.add(pathField);
        fileRow.add(browseButton);

        JPanel errorRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        errorRow.add(errorLabel);

        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));
        inputPanel.add(fileRow);
        inputPanel.add(errorRow);
    }

    @Override
    public String getDisplayName() {
        return "File";
    }

    @Override
    public JPanel getInputPanel() {
        return inputPanel;
    }

    @Override
    public List<String> getResults() {
        if (selectedFile == null) {
            return List.of();
        }
        Optional<FileImportType> type = FileImportType.fromExtension(extension(selectedFile));
        if (type.isEmpty()) {
            return List.of();
        }
        return switch (type.get()) {
            case TEXT -> readTextFile();
            case CSV  -> readCsvFile();
            case XLSX -> readXlsxFile();
        };
    }

    private void onBrowse() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open Build List");
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            pathField.setText(selectedFile.getAbsolutePath());
            boolean supported = FileImportType.fromExtension(extension(selectedFile)).isPresent();
            errorLabel.setVisible(!supported);
            inputPanel.revalidate();
        }
    }

    private List<String> readTextFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to read build list from {}", selectedFile.getAbsolutePath(), e);
            return List.of();
        }
    }

    private List<String> readCsvFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            List<String> lines = reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .collect(Collectors.toList());

            if (lines.isEmpty()) {
                return List.of();
            }

            String[] firstRow = lines.get(0).split(",", -1);
            int urlCol = findUrlColumn(firstRow);
            int startRow;
            if (urlCol >= 0) {
                startRow = 1;
            } else {
                startRow = 0;
                urlCol = firstRow.length > 1 ? firstRow.length - 1 : 0;
            }

            final int col = urlCol;
            return lines.subList(startRow, lines.size()).stream()
                    .map(line -> line.split(",", -1))
                    .filter(parts -> parts.length > col)
                    .map(parts -> parts[col].trim())
                    .filter(url -> !url.isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to read CSV from {}", selectedFile.getAbsolutePath(), e);
            return List.of();
        }
    }

    private List<String> readXlsxFile() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(selectedFile)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getLastRowNum() < 0) {
                return List.of();
            }

            Row headerRow = sheet.getRow(0);
            int urlCol = headerRow != null ? findUrlColumn(headerRow) : -1;
            int startRow;
            if (urlCol >= 0) {
                startRow = 1;
            } else {
                startRow = 0;
                urlCol = headerRow != null && headerRow.getLastCellNum() > 1
                        ? headerRow.getLastCellNum() - 1
                        : 0;
            }

            List<String> results = new ArrayList<>();
            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Cell cell = row.getCell(urlCol);
                if (cell == null) {
                    continue;
                }
                String value = cell.getStringCellValue().trim();
                if (!value.isEmpty()) {
                    results.add(value);
                }
            }
            return results;
        } catch (Exception e) {
            log.error("Failed to read xlsx from {}", selectedFile.getAbsolutePath(), e);
            return List.of();
        }
    }

    private static int findUrlColumn(String[] headers) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase("url")) {
                return i;
            }
        }
        return -1;
    }

    private static int findUrlColumn(Row headerRow) {
        for (Cell cell : headerRow) {
            if ("url".equalsIgnoreCase(cell.getStringCellValue().trim())) {
                return cell.getColumnIndex();
            }
        }
        return -1;
    }

    private static String extension(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot + 1) : "";
    }
}
