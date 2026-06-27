package poebuildexporter.code.importer;

import javax.swing.JPanel;
import java.util.List;

public interface BuildImporter {
    String getDisplayName();
    JPanel getInputPanel();
    List<BuildImporterResult> getResults();
}
