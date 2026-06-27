package poebuildexporter.code.importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CsvFileBuildImporter extends FileBuildImporter {

    private static final Logger log = LoggerFactory.getLogger(CsvFileBuildImporter.class);

    @Override
    protected FileNameExtensionFilter getFileNameExtensionFilter() {
        return new FileNameExtensionFilter("CSV Files (*.csv)", "csv");
    }

    @Override
    public String getDisplayName() {
        return "CSV File (.csv)";
    }

    @Override
    public List<BuildImporterResult> getResults() {
        if (selectedFile == null) {
            return List.of();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            List<String> lines = reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .collect(Collectors.toList());

            if (lines.isEmpty()) {
                return List.of();
            }

            String[] firstRow = lines.get(0).split(",", -1);
            int urlCol = findColumn(firstRow, "url");
            int nameCol = findColumn(firstRow, "name");
            int startRow;
            if (urlCol >= 0) {
                startRow = 1;
            } else {
                startRow = 0;
                urlCol = firstRow.length > 1 ? firstRow.length - 1 : 0;
                nameCol = -1;
            }

            final int finalUrlCol = urlCol;
            final int finalNameCol = nameCol;
            List<BuildImporterResult> results = new ArrayList<>();
            for (String line : lines.subList(startRow, lines.size())) {
                String[] parts = line.split(",", -1);
                if (parts.length <= finalUrlCol) {
                    continue;
                }
                String url = parts[finalUrlCol].trim();
                if (url.isEmpty()) {
                    continue;
                }
                String name = null;
                if (finalNameCol >= 0 && parts.length > finalNameCol) {
                    String nameValue = parts[finalNameCol].trim();
                    if (!nameValue.isEmpty()) {
                        name = nameValue;
                    }
                }
                results.add(new BuildImporterResult(name, url));
            }
            return results;
        } catch (IOException e) {
            log.error("Failed to read CSV from {}", selectedFile.getAbsolutePath(), e);
            return List.of();
        }
    }

    private static int findColumn(String[] headers, String name) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }
}
