package poebuildexporter.code;

import com.formdev.flatlaf.FlatDarkLaf;
import poebuildexporter.code.ItemData.Item;
import poebuildexporter.code.ItemData.Stat;
import poebuildexporter.code.export.BuildExporter;
import poebuildexporter.code.export.XlsxBuildExporter;
import poebuildexporter.code.gui.BuildParserGui;
import poebuildexporter.code.importer.BuildImporter;
import poebuildexporter.code.importer.FileBuildImporter;
import poebuildexporter.code.importer.ManualBuildImporter;

import javax.swing.SwingUtilities;
import java.util.List;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatDarkLaf.setup();
            List<BuildImporter> importers = List.of(new ManualBuildImporter(), new FileBuildImporter());
            List<BuildExporter> exporters = List.of(new XlsxBuildExporter());
            new BuildParserGui(importers, exporters).show();
        });
    }

    private static void printList(List<?> list) {
        for (Object o : list) {
            System.out.println(o);
            System.out.println();
        }
    }

    private static void printItemStatIdsFromList(List<Item> items) {
        for (Item i : items) {
            System.out.println(i.getName() + "\n" + i.getBaseType());
            for (Stat s : i.getStats()) {
                System.out.println("        " + s.getRoll() + " " + s.getText() + "\n        " + s.getId());
                System.out.println();
            }
            System.out.println("\n");
        }
    }
}
