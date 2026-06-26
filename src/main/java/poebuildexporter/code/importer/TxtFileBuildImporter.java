package poebuildexporter.code.importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TxtFileBuildImporter implements BuildImporter {

    private static final Logger log = LoggerFactory.getLogger(TxtFileBuildImporter.class);

    private final JPanel inputPanel;
    private final JTextField pathField;
    private File selectedFile = null;

    public TxtFileBuildImporter() {
        pathField = new JTextField(40);
        pathField.setEditable(false);

        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> onBrowse());

        inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("File:"));
        inputPanel.add(pathField);
        inputPanel.add(browseButton);
    }

    @Override
    public String getDisplayName() {
        return "Text File (.txt)";
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

    private void onBrowse() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open Build List");
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            pathField.setText(selectedFile.getAbsolutePath());
        }
    }
}
