package poebuildexporter.code;

import com.formdev.flatlaf.FlatDarkLaf;
import poebuildexporter.code.ItemData.Item;
import poebuildexporter.code.ItemData.Stat;
import poebuildexporter.code.export.BuildExporter;
import poebuildexporter.code.export.XlsxBuildExporter;
import poebuildexporter.code.gui.BuildParserGui;
import poebuildexporter.code.importer.BuildImporter;
import poebuildexporter.code.importer.CsvFileBuildImporter;
import poebuildexporter.code.importer.ManualBuildImporter;
import poebuildexporter.code.importer.TxtFileBuildImporter;
import poebuildexporter.code.importer.XlsxFileBuildImporter;

import javax.swing.SwingUtilities;
import java.util.List;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatDarkLaf.setup();
            List<BuildImporter> importers = List.of(new ManualBuildImporter(), new TxtFileBuildImporter(), new CsvFileBuildImporter(), new XlsxFileBuildImporter());
            List<BuildExporter> exporters = List.of(new XlsxBuildExporter());
            new BuildParserGui(importers, exporters).show();
        });
    }
}
