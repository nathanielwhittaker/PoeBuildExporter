package poebuildexporter.code.gui;

import poebuildexporter.code.ItemData.Item;
import poebuildexporter.code.PropertiesManagerCore;
import poebuildexporter.code.build.Build;
import poebuildexporter.code.build.BuildType;
import poebuildexporter.code.export.BuildExporter;
import poebuildexporter.code.export.ExportType;
import poebuildexporter.code.export.FileBuildExporter;
import poebuildexporter.code.importer.BuildImporter;
import poebuildexporter.code.poeapi.PathOfExileTradeApi;
import poebuildexporter.code.poeapi.PathOfExileTradeApiResponse;
import poebuildexporter.code.util.BuildUtils;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BuildParserGui {

    private final JFrame frame;
    private final JPanel centerWrapper;
    private final JComboBox<BuildImporter> importerCombo;
    private final JComboBox<BuildExporter> exporterCombo;
    private final JPanel fileRow;
    private final JTextField filePathField;
    private final JPasswordField sessIdField;
    private final JTextField leagueField;
    private final JTextField leaguePoe2Field;
    private final JButton runButton;
    private final JLabel statusLabel;
    private final JProgressBar progressBar;

    public BuildParserGui(List<BuildImporter> importers, List<BuildExporter> exporters) {
        frame = new JFrame("PoeBuildExporter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);

        importerCombo = new JComboBox<>(importers.toArray(new BuildImporter[0]));
        importerCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof BuildImporter importer) {
                    setText(importer.getDisplayName());
                }
                return this;
            }
        });

        centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBorder(BorderFactory.createTitledBorder("Build Input"));

        exporterCombo = new JComboBox<>(exporters.toArray(new BuildExporter[0]));
        exporterCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof BuildExporter exporter) {
                    setText(exporter.getDisplayName());
                }
                return this;
            }
        });

        filePathField = new JTextField(30);
        filePathField.setEditable(false);
        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> onBrowse());

        fileRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileRow.add(new JLabel("Save to:"));
        fileRow.add(filePathField);
        fileRow.add(browseButton);

        sessIdField = new JPasswordField(36);
        String currentSessId = PropertiesManagerCore.getPoeSessId();
        if (currentSessId != null) {
            sessIdField.setText(currentSessId);
        }
        JButton saveSessIdButton = new JButton("Save");
        saveSessIdButton.addActionListener(e -> onSaveSessId());

        leagueField = new JTextField(16);
        String currentLeague = PropertiesManagerCore.getLeague();
        if (currentLeague != null) {
            leagueField.setText(currentLeague);
        }

        leaguePoe2Field = new JTextField(16);
        String currentLeaguePoe2 = PropertiesManagerCore.getLeaguePoe2();
        if (currentLeaguePoe2 != null) {
            leaguePoe2Field.setText(currentLeaguePoe2);
        }

        JButton saveLeagueButton = new JButton("Save");
        saveLeagueButton.addActionListener(e -> onSaveLeagues());

        runButton = new JButton("Run");
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.GRAY);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("");
        progressBar.setVisible(false);

        JPanel importRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        importRow.add(new JLabel("Import via:"));
        importRow.add(importerCombo);

        JPanel sessIdRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sessIdRow.add(new JLabel("POESESSID:"));
        sessIdRow.add(sessIdField);
        sessIdRow.add(saveSessIdButton);

        JPanel leagueRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leagueRow.add(new JLabel("League (PoE1):"));
        leagueRow.add(leagueField);
        leagueRow.add(new JLabel("League (PoE2):"));
        leagueRow.add(leaguePoe2Field);
        leagueRow.add(saveLeagueButton);

        JPanel exportRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exportRow.add(new JLabel("Export format:"));
        exportRow.add(exporterCombo);

        JPanel runRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        runRow.add(runButton);

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusRow.add(statusLabel);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.PAGE_AXIS));
        southPanel.add(sessIdRow);
        southPanel.add(leagueRow);
        southPanel.add(exportRow);
        southPanel.add(fileRow);
        southPanel.add(runRow);
        southPanel.add(progressBar);
        southPanel.add(statusRow);

        frame.add(importRow, BorderLayout.NORTH);
        frame.add(centerWrapper, BorderLayout.CENTER);
        frame.add(southPanel, BorderLayout.SOUTH);

        importerCombo.addActionListener(e -> updateImporterPanel());
        exporterCombo.addActionListener(e -> updateFileRowVisibility());
        runButton.addActionListener(e -> onRun());

        updateImporterPanel();
        updateFileRowVisibility();
    }

    public void show() {
        frame.setVisible(true);
    }

    private void updateImporterPanel() {
        BuildImporter importer = (BuildImporter) importerCombo.getSelectedItem();
        centerWrapper.removeAll();
        centerWrapper.add(importer.getInputPanel(), BorderLayout.CENTER);
        centerWrapper.revalidate();
        centerWrapper.repaint();
    }

    private void updateFileRowVisibility() {
        BuildExporter selected = (BuildExporter) exporterCombo.getSelectedItem();
        fileRow.setVisible(selected.getExportType() == ExportType.FILE);
        frame.revalidate();
    }

    private void onBrowse() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save As");
        BuildExporter selected = (BuildExporter) exporterCombo.getSelectedItem();
        if (selected.getExportType() == ExportType.FILE) {
            chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
            chooser.setSelectedFile(new File("build_results.xlsx"));
        }
        if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void onRun() {
        BuildImporter importer = (BuildImporter) importerCombo.getSelectedItem();
        List<String> importerResults = importer.getResults();
        if (importerResults.isEmpty()) {
            setStatus("No build importerResults provided.", Color.ORANGE);
            return;
        }

        BuildExporter exporter = (BuildExporter) exporterCombo.getSelectedItem();

        if (exporter.getExportType() == ExportType.FILE) {
            String path = filePathField.getText().trim();
            if (path.isEmpty()) {
                setStatus("No output file selected.", Color.ORANGE);
                return;
            }
            ((FileBuildExporter) exporter).setOutputFile(new File(path));
        }

        runButton.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setString("");
        progressBar.setVisible(true);
        setStatus("Loading builds...", Color.GRAY);

        SwingWorker<Map<Build, Map<Item, PathOfExileTradeApiResponse>>, String> worker =
                new SwingWorker<>() {
                    @Override
                    protected Map<Build, Map<Item, PathOfExileTradeApiResponse>> doInBackground() {
                        List<Build> builds = BuildUtils.extractBuildsFromRawImportData(importerResults, this::publish);

                        int totalItems = builds.stream()
                                .mapToInt(b -> b.getItems().size())
                                .sum();
                        AtomicInteger completed = new AtomicInteger(0);

                        publish("Running trade queries... (0/" + totalItems + ")");
                        return PathOfExileTradeApi.batchProcessBuilds(builds, () -> {
                            int done = completed.incrementAndGet();
                            setProgress(totalItems == 0 ? 100 : (int) (done * 100.0 / totalItems));
                            publish("Running trade queries... (" + done + "/" + totalItems + ")");
                        });
                    }

                    @Override
                    protected void process(List<String> chunks) {
                        setStatus(chunks.get(chunks.size() - 1), Color.GRAY);
                    }

                    @Override
                    protected void done() {
                        runButton.setEnabled(true);
                        progressBar.setVisible(false);
                        try {
                            Map<Build, Map<Item, PathOfExileTradeApiResponse>> results = get();
                            setStatus("Exporting...", Color.GRAY);
                            exporter.export(results);
                            setStatus("Done.", new Color(80, 200, 80));
                        } catch (Exception ex) {
                            setStatus("Error: " + ex.getMessage(), Color.RED);
                            JOptionPane.showMessageDialog(frame, ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };

        worker.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if ("progress".equals(evt.getPropertyName())) {
                int value = (Integer) evt.getNewValue();
                progressBar.setValue(value);
                progressBar.setString(value + "%");
            }
        });

        worker.execute();
    }

    private void onSaveLeagues() {
        String poe1 = leagueField.getText().trim();
        String poe2 = leaguePoe2Field.getText().trim();
        saveConfig("League names cannot be empty.", "Leagues saved.", () -> {
            PropertiesManagerCore.setLeague(poe1);
            PropertiesManagerCore.setLeaguePoe2(poe2);
        }, poe1, poe2);
    }

    private void onSaveSessId() {
        String value = new String(sessIdField.getPassword()).trim();
        saveConfig("POESESSID cannot be empty.", "POESESSID saved.",
                () -> PropertiesManagerCore.setPoeSessId(value), value);
    }

    private void saveConfig(String emptyError, String successMessage, Runnable save, String... values) {
        for (String value : values) {
            if (value.isEmpty()) {
                setStatus(emptyError, Color.ORANGE);
                return;
            }
        }
        save.run();
        setStatus(successMessage, new Color(80, 200, 80));
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
}
