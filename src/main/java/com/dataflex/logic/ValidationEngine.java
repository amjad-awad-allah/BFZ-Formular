package com.dataflex.logic;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.dataflex.model.FormField;

public class ValidationEngine {

    public static Map<String, String> validate(Map<FormField, JComponent> fieldMap) {
        Map<String, String> errors = new HashMap<>();

        for (Map.Entry<FormField, JComponent> entry : fieldMap.entrySet()) {
            FormField field = entry.getKey();
            JComponent component = entry.getValue();
            String value = getValueAsString(component);

            // Required check
            if (field.isRequired() && (value == null || value.trim().isEmpty())) {
                errors.put(field.getLabel(), "This field is required.");
                continue;
            }

            // Data type check
            if (value != null && !value.trim().isEmpty()) {
                if ("int".equalsIgnoreCase(field.getDataType())) {
                    try {
                        Integer.valueOf(value);
                    } catch (NumberFormatException e) {
                        errors.put(field.getLabel(), "Must be a valid integer.");
                    }
                }
                // Add more type checks if needed
            }
        }

        return errors;
    }

    /**
     * Extracts value from Swing components using Pattern Matching for switch (Java 21).
     * This modern approach provides a clean, declarative way to handle multiple types
     * and binds the typed variables directly within each case label.
     */
    private static String getValueAsString(JComponent component) {
        return switch (component) {
            case JTextField textField -> textField.getText();
            case JSpinner spinner -> spinner.getValue().toString();
            case JComboBox<?> comboBox -> {
                Object selected = comboBox.getSelectedItem();
                yield selected != null ? selected.toString() : "";
            }
            case JCheckBox checkBox -> String.valueOf(checkBox.isSelected());
            case JTextArea textArea -> textArea.getText();
            case JScrollPane scrollPane -> {
                // Handling nested components within the scroll pane's viewport
                if (scrollPane.getViewport().getView() instanceof JTextArea textArea) {
                    yield textArea.getText();
                }
                yield "";
            }
            default -> "";
        };
    }
}
