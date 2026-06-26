package poebuildexporter.code.importer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class ManualBuildImporter implements BuildImporter {

    private final JPanel inputPanel;
    private final JPanel fieldsPanel;
    private final List<JTextField> fields = new ArrayList<>();

    public ManualBuildImporter() {
        fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.PAGE_AXIS));
        addField();

        inputPanel = new JPanel(new java.awt.BorderLayout());
        inputPanel.add(new JScrollPane(fieldsPanel), java.awt.BorderLayout.CENTER);
    }

    @Override
    public String getDisplayName() {
        return "Manual Entry";
    }

    @Override
    public JPanel getInputPanel() {
        return inputPanel;
    }

    @Override
    public List<String> getResults() {
        List<String> results = new ArrayList<>();
        for (JTextField field : fields) {
            String trimmed = field.getText().trim();
            if (!trimmed.isEmpty()) {
                results.add(trimmed);
            }
        }
        return results;
    }

    private void addField() {
        JTextField field = new JTextField();
        field.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onFieldChanged(field);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onFieldChanged(field);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        fields.add(field);
        fieldsPanel.add(field);
        fieldsPanel.revalidate();
        fieldsPanel.repaint();
    }

    private void onFieldChanged(JTextField field) {
        int idx = fields.indexOf(field);
        int last = fields.size() - 1;

        if (idx == last && !field.getText().isEmpty()) {
            addField();
        } else if (idx == last - 1 && field.getText().isEmpty()) {
            JTextField lastField = fields.get(last);
            if (lastField.getText().isEmpty() && fields.size() > 1) {
                fields.remove(last);
                fieldsPanel.remove(lastField);
                fieldsPanel.revalidate();
                fieldsPanel.repaint();
            }
        }
    }
}
