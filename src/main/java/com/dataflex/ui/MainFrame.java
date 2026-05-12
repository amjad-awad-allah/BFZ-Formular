package com.dataflex.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.dataflex.logic.JsonHandler;
import com.dataflex.logic.ValidationEngine;
import com.dataflex.model.FormConfig;
import com.dataflex.model.FormData;

import net.miginfocom.swing.MigLayout;

public class MainFrame extends JFrame {
    // UI-Hauptkomponenten
    private DynamicFormPanel formPanel;
    private JLabel versionLabel;
    private FormConfig currentConfig;

    public MainFrame() {
        setTitle("DataFlex Solutions GmbH - Dynamic Form Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initComponents();
    }

    // Initialisierung der Benutzeroberfläche
    private void initComponents() {
        setLayout(new BorderLayout());

        // Oberer Bereich (Header)
        JPanel headerPanel = new JPanel(new MigLayout("insets 15", "[]push[]"));
        headerPanel.setBackground(new Color(0, 128, 128)); // Teal
        JLabel logoLabel = new JLabel("DataFlex Solutions");
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(logoLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Seitliche Menüleiste (Sidebar)
        JPanel sidebar = new JPanel(new MigLayout("wrap 1, insets 10, fillx", "[fill]"));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        
        JButton btnLoadConfig = new JButton("Load Configuration (JSON)");
        JButton btnSaveData = new JButton("Save Data (JSON)");
        JButton btnLoadData = new JButton("Load Data (JSON)");

        btnLoadConfig.addActionListener(e -> loadConfig());
        btnSaveData.addActionListener(e -> saveData());
        btnLoadData.addActionListener(e -> loadData());

        sidebar.add(new JLabel("Actions"), "gapbottom 10");
        sidebar.add(btnLoadConfig);
        sidebar.add(btnLoadData);
        sidebar.add(btnSaveData, "gaptop 20");
        
        add(sidebar, BorderLayout.WEST);

        // Main Content
        formPanel = new DynamicFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Footer / Status Bar
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        versionLabel = new JLabel("Application Version: " + com.dataflex.Main.APP_VERSION);
        versionLabel.setFont(versionLabel.getFont().deriveFont(Font.ITALIC, 11f));
        versionLabel.setForeground(Color.GRAY);
        footer.add(versionLabel);
        add(footer, BorderLayout.SOUTH);
    }

    private void loadConfig() {
        JFileChooser chooser = new JFileChooser(".");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                FormConfig config = JsonHandler.loadConfig(chooser.getSelectedFile().getAbsolutePath());
                if (config != null) {
                    currentConfig = config;
                    formPanel.buildForm(currentConfig);
                }
            } catch (Exception ex) {
                // Zeigt dem Benutzer genau an, was schiefgelaufen ist (z.B. Syntax-Fehler)
                JOptionPane.showMessageDialog(this, 
                    "Fehler beim Laden der Konfiguration:\n" + ex.getMessage(), 
                    "Konfigurationsfehler", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveData() {
        if (currentConfig == null) {
            JOptionPane.showMessageDialog(this, "Please load a configuration first.");
            return;
        }

        // Validate
        Map<String, String> errors = ValidationEngine.validate(formPanel.getFieldMap());
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder("Please fix the following errors:\n");
            errors.forEach((k, v) -> sb.append("- ").append(k).append(": ").append(v).append("\n"));
            JOptionPane.showMessageDialog(this, sb.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser(".");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                FormData data = new FormData();
                data.setFormTitle(currentConfig.getFormTitle());
                data.setSubmittedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                data.setValues(formPanel.getValues());

                String path = chooser.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".json")) path += ".json";
                
                JsonHandler.saveData(path, data);
                JOptionPane.showMessageDialog(this, "Daten erfolgreich gespeichert!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Fehler beim Speichern:\n" + ex.getMessage(), 
                    "Speicherfehler", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadData() {
        if (currentConfig == null) {
            JOptionPane.showMessageDialog(this, "Please load a configuration first.");
            return;
        }

        JFileChooser chooser = new JFileChooser(".");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                FormData data = JsonHandler.loadData(chooser.getSelectedFile().getAbsolutePath());
                if (data == null) {
                    JOptionPane.showMessageDialog(this, "The selected file is not a valid data file.");
                    return;
                }
                if (!data.getFormTitle().equals(currentConfig.getFormTitle())) {
                    int res = JOptionPane.showConfirmDialog(this, 
                        "The data belongs to a different form ('" + data.getFormTitle() + "'). Load anyway?", 
                        "Mismatch", JOptionPane.YES_NO_OPTION);
                    if (res != JOptionPane.YES_OPTION) return;
                }
                formPanel.setValues(data.getValues());
                JOptionPane.showMessageDialog(this, "Daten erfolgreich geladen!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Fehler beim Laden der Daten:\n" + ex.getMessage(), 
                    "Ladefehler", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
