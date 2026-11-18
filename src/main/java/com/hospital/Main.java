package com.hospital;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Modern UI Configuration with FlatLaf
        try {
            // Setup modern FlatLaf Light theme
            FlatLightLaf.setup();

            // Set global UI properties for an industry-grade finish
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.thumbArc", 10);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
            UIManager.put("TableHeader.separatorColor", new Color(226, 232, 240));
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Component.innerFocusWidth", 1);
            
            // Set default application font
            Font appFont = new Font("Segoe UI", Font.PLAIN, 13);
            UIManager.put("defaultFont", appFont);

        } catch (Exception e) {
            System.err.println("FlatLaf initialization failed: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
