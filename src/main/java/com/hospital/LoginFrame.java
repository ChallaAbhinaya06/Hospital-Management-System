package com.hospital;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);

    public LoginFrame() {
        setTitle("Hospital Care Portal - Secure Staff Login");
        setSize(440, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(UIUtils.BACKGROUND);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UIUtils.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Logo / Badge Area
        JLabel badge = new JLabel("+", SwingConstants.CENTER);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 28));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(UIUtils.PRIMARY);
        badge.setPreferredSize(new Dimension(54, 54));
        badge.setMaximumSize(new Dimension(54, 54));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);
        badge.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JLabel title = new JLabel("MedCare Portal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UIUtils.TEXT_MAIN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to manage appointments & staff");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(UIUtils.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Card Container for Inputs
        JPanel card = UIUtils.createCard();
        card.setLayout(new GridLayout(5, 1, 6, 8));
        card.setMaximumSize(new Dimension(360, 240));

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(UIUtils.TEXT_MUTED);

        usernameField.putClientProperty("JTextField.placeholderText", "e.g. admin");
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(UIUtils.TEXT_MUTED);

        passwordField.putClientProperty("JTextField.placeholderText", "••••••••");
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        card.add(userLabel);
        card.add(usernameField);
        card.add(passLabel);
        card.add(passwordField);

        JButton loginButton = UIUtils.createPrimaryButton("Sign In to Portal");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(360, 44));
        loginButton.addActionListener(e -> authenticate());

        // Support Enter key submission
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    authenticate();
                }
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        JLabel hint = new JLabel("Demo Access: admin / admin123");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(UIUtils.TEXT_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(badge);
        mainPanel.add(Box.createVerticalStrut(14));
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(4));
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(24));
        mainPanel.add(card);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(14));
        mainPanel.add(hint);

        add(mainPanel);
    }

    private void authenticate() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if ("admin".equals(user) && "admin123".equals(pass)) {
            dispose();
            new DashboardFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid credentials. Please enter valid admin details.",
                "Authentication Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
