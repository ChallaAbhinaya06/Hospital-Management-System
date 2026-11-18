package com.hospital;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DoctorPanel extends JPanel {
    private final JTextField nameField = new JTextField(14);
    private final JComboBox<String> specBox = new JComboBox<>(new String[]{
        "General Physician", "Cardiology", "Neurology", "Pediatrics", "Orthopedics", "Dermatology", "Oncology"
    });
    private final JTextField phoneField = new JTextField(12);

    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{"ID", "Doctor Name", "Specialization", "Contact Phone"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final DashboardFrame parentDashboard;

    public DoctorPanel(DashboardFrame parent) {
        this.parentDashboard = parent;
        setLayout(new BorderLayout(0, 16));
        setBackground(UIUtils.BACKGROUND);
        setBorder(new EmptyBorder(24, 30, 24, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Doctors Directory");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UIUtils.TEXT_MAIN);
        JLabel sub = new JLabel("Manage medical staff profiles, departments, and consultation channels.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(UIUtils.TEXT_MUTED);
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(sub, BorderLayout.SOUTH);

        // Input Card
        JPanel card = UIUtils.createCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField.putClientProperty("JTextField.placeholderText", "e.g. Dr. Jane Foster");
        phoneField.putClientProperty("JTextField.placeholderText", "Direct Phone");
        specBox.setEditable(true);

        gbc.gridx = 0; gbc.gridy = 0; card.add(new JLabel("Doctor Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; card.add(nameField, gbc);

        gbc.gridx = 2; gbc.gridy = 0; card.add(new JLabel("Specialization:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; card.add(specBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; card.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; card.add(phoneField, gbc);

        JButton addBtn = UIUtils.createPrimaryButton("+ Add Doctor");
        addBtn.addActionListener(e -> addDoctor());
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2;
        card.add(addBtn, gbc);

        // Table
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setOpaque(false);

        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableActions.setOpaque(false);
        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh Doctors List");
        refreshBtn.addActionListener(e -> loadDoctors());
        tableActions.add(refreshBtn);

        UIUtils.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER, 1));

        tableContainer.add(tableActions, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel topWrap = new JPanel(new BorderLayout(0, 14));
        topWrap.setOpaque(false);
        topWrap.add(headerPanel, BorderLayout.NORTH);
        topWrap.add(card, BorderLayout.CENTER);

        add(topWrap, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);

        loadDoctors();
    }

    private void addDoctor() {
        String docName = nameField.getText().trim();
        String spec = specBox.getSelectedItem() != null ? specBox.getSelectedItem().toString().trim() : "";
        String phone = phoneField.getText().trim();

        if (docName.isEmpty() || spec.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Doctor name and specialization are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "INSERT INTO doctors(name, specialization, phone) VALUES(?,?,?)")) {
            ps.setString(1, docName);
            ps.setString(2, spec);
            ps.setString(3, phone);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Doctor registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            nameField.setText("");
            phoneField.setText("");
            loadDoctors();
            parentDashboard.refreshMetrics();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadDoctors() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM doctors ORDER BY doctor_id DESC")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("doctor_id"),
                    rs.getString("name"),
                    rs.getString("specialization"),
                    rs.getString("phone")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load doctors: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
