package com.dataflex.ui;

import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.dataflex.model.FormConfig;
import com.dataflex.model.FormField;

import net.miginfocom.swing.MigLayout;

public class DynamicFormPanel extends JPanel {
    // Speichert die Zuordnung von Config-Feld zu Swing-Komponente
    private final Map<FormField, JComponent> fieldMap = new LinkedHashMap<>();
    private final JLabel titleLabel;

    public DynamicFormPanel() {
        setLayout(new MigLayout("wrap 2, fillx, insets 20", "[right][fill, grow]"));
        titleLabel = new JLabel("Please load a form...");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        add(titleLabel, "span, center, gapbottom 20");
    }

    // Baut das Formular basierend auf der JSON-Config komplett neu auf
    public void buildForm(FormConfig config) {
        removeAll();
        fieldMap.clear();

        String titleText = config.getFormTitle();
        if (config.getVersion() != null) {
            titleText += " (v" + config.getVersion() + ")";
        }
        titleLabel.setText(titleText);
        add(titleLabel, "span, center, gapbottom 20");

        for (FormField field : config.getFields()) {
            JLabel label = new JLabel(field.getLabel() + (field.isRequired() ? "*" : ":"));
            JComponent component = createComponent(field);
            
            add(label);
            add(component, "growx");
            fieldMap.put(field, component);
        }

        revalidate();
        repaint();
    }

    private JComponent createComponent(FormField field) {
        String type = field.getControlType().toLowerCase();
        return switch (type) {
            case "textfield" -> new JTextField();
            case "spinner" -> new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
            case "dropdown" -> {
                JComboBox<String> combo = new JComboBox<>();
                if (field.getOptions() != null) {
                    for (String opt : field.getOptions()) combo.addItem(opt);
                }
                yield combo;
            }
            case "checkbox" -> new JCheckBox();
            case "textarea" -> {
                JTextArea area = new JTextArea(3, 20);
                area.setLineWrap(true);
                yield new JScrollPane(area);
            }
            default -> new JTextField();
        };
    }

    public Map<String, Object> getValues() {
        Map<String, Object> values = new LinkedHashMap<>();
        for (Map.Entry<FormField, JComponent> entry : fieldMap.entrySet()) {
            values.put(entry.getKey().getLabel(), getValue(entry.getValue()));
        }
        return values;
    }

    public void setValues(Map<String, Object> values) {
        if (values == null) return;
        for (Map.Entry<FormField, JComponent> entry : fieldMap.entrySet()) {
            Object val = values.get(entry.getKey().getLabel());
            if (val != null) {
                setValue(entry.getValue(), val);
            }
        }
    }

    // Liest die aktuellen Werte aus den GUI-Komponenten aus
    private Object getValue(JComponent comp) {
        return switch (comp) {
            case JTextField textField -> textField.getText();
            case JSpinner spinner -> spinner.getValue();
            case JComboBox<?> comboBox -> comboBox.getSelectedItem();
            case JCheckBox checkBox -> checkBox.isSelected();
            case JScrollPane scrollPane -> {
                if (scrollPane.getViewport().getView() instanceof JTextArea textArea) {
                    yield textArea.getText();
                }
                yield null;
            }
            default -> null;
        };
    }

    // Schreibt die Werte in die entsprechenden GUI-Komponenten
    private void setValue(JComponent comp, Object val) {
        switch (comp) {
            case JTextField textField -> textField.setText(val.toString());
            case JSpinner spinner -> {
                // Konvertierung von Double (aus JSON) zu Integer für den Spinner
                if (val instanceof Double d) spinner.setValue(d.intValue());
                else spinner.setValue(val);
            }
            case JComboBox<?> comboBox -> comboBox.setSelectedItem(val);
            case JCheckBox checkBox -> checkBox.setSelected(val instanceof Boolean b && b);
            case JScrollPane scrollPane -> {
                if (scrollPane.getViewport().getView() instanceof JTextArea textArea) {
                    textArea.setText(val.toString());
                }
            }
            default -> { /* Unknown component */ }
        }
    }

    public Map<FormField, JComponent> getFieldMap() {
        return fieldMap;
    }
}
