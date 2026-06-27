package poebuildexporter.code.importer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.FlowLayout;
import java.io.File;
import java.util.List;

public abstract class FileBuildImporter implements BuildImporter {

    private final JPanel inputPanel;
    private final JTextField pathField;
    protected File selectedFile = null;

    protected FileBuildImporter() {
        pathField = new JTextField(40);
        pathField.setEditable(false);

        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> onBrowse());

        inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("File:"));
        inputPanel.add(pathField);
        inputPanel.add(browseButton);
    }

    protected abstract FileNameExtensionFilter getFileNameExtensionFilter();

    @Override
    public abstract String getDisplayName();

    @Override
    public abstract List<BuildImporterResult> getResults();

    @Override
    public JPanel getInputPanel() {
        return inputPanel;
    }

    private void onBrowse() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open Build List");
        chooser.setFileFilter(getFileNameExtensionFilter());
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            pathField.setText(selectedFile.getAbsolutePath());
        }
    }
}
