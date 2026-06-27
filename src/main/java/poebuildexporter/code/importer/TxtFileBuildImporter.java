package poebuildexporter.code.importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TxtFileBuildImporter extends FileBuildImporter {

    private static final Logger log = LoggerFactory.getLogger(TxtFileBuildImporter.class);

    @Override
    protected FileNameExtensionFilter getFileNameExtensionFilter() {
        return new FileNameExtensionFilter("Text Files (*.txt)", "txt");
    }

    @Override
    public String getDisplayName() {
        return "Text File (.txt)";
    }

    @Override
    public List<BuildImporterResult> getResults() {
        if (selectedFile == null) {
            return List.of();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .map(line -> new BuildImporterResult(null, line))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to read build list from {}", selectedFile.getAbsolutePath(), e);
            return List.of();
        }
    }
}
