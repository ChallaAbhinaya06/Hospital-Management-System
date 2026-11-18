package com.hospital;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PatientPanel extends JPanel {
    private final JTextField nameField = new JTextField(12);
    private final JTextField ageField = new JTextField(4);
    private final JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
    private final JTextField phoneField = new JTextField(12);
    private final JTextField searchField = new JTextField(12);

    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{"ID", "Patient Name", "Age", "Gender", "Phone Contact"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final DashboardFrame parentDashboard;

    public PatientPanel(DashboardFrame parent) {
        this.parentDashboard = parent;
        setLayout(new BorderLayout(0, 16));
        setBackground(UIUtils.BACKGROUND);
        setBorder(new EmptyBorder(24, 30, 24, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Patient Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UIUtils.TEXT_MAIN);
        JLabel sub = new JLabel("Register and search patients admitted or consulting in the facility.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(UIUtils.TEXT_MUTED);
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(sub, BorderLayout.SOUTH);

        // Input Registration Card
        JPanel card = UIUtils.createCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField.putClientProperty("JTextField.placeholderText", "Full Name");
        ageField.putClientProperty("JTextField.placeholderText", "Age");
        phoneField.putClientProperty("JTextField.placeholderText", "Phone / Mobile");

        gbc.gridx = 0; gbc.gridy = 0; card.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; card.add(nameField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; card.add(new JLabel("Age:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; card.add(ageField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; card.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; card.add(genderBox, gbc);

        gbc.gridx = 2; gbc.gridy = 1; card.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; card.add(phoneField, gbc);

        JButton addBtn = UIUtils.createPrimaryButton("+ Add Patient");
        addBtn.addActionListener(e -> addPatient());
        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 2;
        card.add(addBtn, gbc);

        // Table & Search Area
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setOpaque(false);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchBar.setOpaque(false);
        searchField.putClientProperty("JTextField.placeholderText", "Filter by patient name...");
        searchField.setPreferredSize(new Dimension(220, 32));
        JButton filterBtn = UIUtils.createSecondaryButton("Filter");
        filterBtn.addActionListener(e -> filterPatients(searchField.getText().trim()));
        JButton refreshBtn = UIUtils.createSecondaryButton("Reload Table");
        refreshBtn.addActionListener(e -> loadPatients());

        searchBar.add(new JLabel("Search:"));
        searchBar.add(searchField);
        searchBar.add(filterBtn);
        searchBar.add(refreshBtn);

        UIUtils.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER, 1));

        tableContainer.add(searchBar, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // Assembly
        JPanel topWrap = new JPanel(new BorderLayout(0, 14));
        topWrap.setOpaque(false);
        topWrap.add(headerPanel, BorderLayout.NORTH);
        topWrap.add(card, BorderLayout.CENTER);

        add(topWrap, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);

        loadPatients();
    }

    private void addPatient() {
        try {
            String pName = nameField.getText().trim();
            if (pName.isEmpty()) throw new IllegalArgumentException("Patient name cannot be empty.");
            int pAge = Integer.parseInt(ageField.getText().trim());
            if (pAge < 0 || pAge > 130) throw new IllegalArgumentException("Please enter a valid age between 0 and 130.");
            String pGender = (String) genderBox.getSelectedItem();
            String pPhone = phoneField.getText().trim();

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO patients(name, age, gender, phone) VALUES(?,?,?,?)")) {
                ps.setString(1, pName);
                ps.setInt(2, pAge);
                ps.setString(3, pGender);
                ps.setString(4, pPhone);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Patient registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            nameField.setText("");
            ageField.setText("");
            phoneField.setText("");
            loadPatients();
            parentDashboard.refreshMetrics();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid integer number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadPatients() {
        filterPatients("");
    }

    private void filterPatients(String keyword) {
        model.setRowCount(0);
        String sql = keyword.isEmpty()
            ? "SELECT * FROM patients ORDER BY patient_id DESC"
            : "SELECT * FROM patients WHERE LOWER(name) LIKE ? ORDER BY patient_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (!keyword.isEmpty()) {
                ps.setString(1, "%" + keyword.toLowerCase() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("patient_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("phone")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load patients: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
