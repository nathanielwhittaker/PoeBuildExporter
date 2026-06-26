package poebuildexporter.code.export;

import java.io.File;

public interface FileBuildExporter extends BuildExporter {

    @Override
    default ExportType getExportType() {
        return ExportType.FILE;
    }

    void setOutputFile(File file);
}
