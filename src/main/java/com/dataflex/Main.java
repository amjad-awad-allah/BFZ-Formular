package com.dataflex;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.dataflex.ui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

public class Main {
    public static final String APP_VERSION = "1.0.0";

    public static void main(String[] args) {
        // Modernes Design (FlatLaf) laden
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Design konnte nicht geladen werden.");
        }

        // GUI im richtigen Thread starten
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}