package poebuildexporter.code.importer;

import java.util.Arrays;
import java.util.Optional;

public enum FileImportType {
    TEXT("txt"),
    CSV("csv"),
    XLSX("xlsx");

    private final String extension;

    FileImportType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static Optional<FileImportType> fromExtension(String extension) {
        return Arrays.stream(values())
                .filter(t -> t.extension.equalsIgnoreCase(extension))
                .findFirst();
    }
}
