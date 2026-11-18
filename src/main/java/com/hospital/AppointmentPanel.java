package com.hospital;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class AppointmentPanel extends JPanel {
    private final JComboBox<Choice> patientBox = new JComboBox<>();
    private final JComboBox<Choice> doctorBox = new JComboBox<>();
    private final JTextField dateField = new JTextField(LocalDate.now().plusDays(1).toString(), 10);
    private final JTextField timeField = new JTextField("10:00", 6);
    private final JTextField reasonField = new JTextField(14);
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"CONFIRMED", "COMPLETED", "CANCELLED"});

    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{"ID", "Patient", "Doctor", "Date", "Time", "Reason", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final DashboardFrame parentDashboard;

    public static class Choice {
        public final int id;
        public final String label;
        public Choice(int id, String label) { this.id = id; this.label = label; }
        @Override
        public String toString() { return label; }
    }

    public AppointmentPanel(DashboardFrame parent) {
        this.parentDashboard = parent;
        setLayout(new BorderLayout(0, 16));
        setBackground(UIUtils.BACKGROUND);
        setBorder(new EmptyBorder(24, 30, 24, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Appointment Scheduling & Records");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UIUtils.TEXT_MAIN);
        JLabel sub = new JLabel("Schedule consultations and track status across medical departments.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(UIUtils.TEXT_MUTED);
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(sub, BorderLayout.SOUTH);

        // Booking Card
        JPanel card = UIUtils.createCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        reasonField.putClientProperty("JTextField.placeholderText", "e.g. Routine checkup");

        gbc.gridx = 0; gbc.gridy = 0; card.add(new JLabel("Select Patient:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; card.add(patientBox, gbc);

        gbc.gridx = 2; gbc.gridy = 0; card.add(new JLabel("Assign Doctor:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; card.add(doctorBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; card.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; card.add(dateField, gbc);

        gbc.gridx = 2; gbc.gridy = 1; card.add(new JLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; card.add(timeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; card.add(new JLabel("Consultation Reason:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; card.add(reasonField, gbc);

        gbc.gridx = 2; gbc.gridy = 2; card.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; card.add(statusBox, gbc);

        JButton bookBtn = UIUtils.createPrimaryButton("+ Confirm Booking");
        bookBtn.addActionListener(e -> bookAppointment());
        gbc.gridx = 4; gbc.gridy = 0; gbc.gridheight = 3;
        card.add(bookBtn, gbc);

        // Table & Actions
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setOpaque(false);

        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tableActions.setOpaque(false);

        JButton cancelBtn = UIUtils.createSecondaryButton("Cancel Selected");
        cancelBtn.setForeground(UIUtils.DANGER);
        cancelBtn.addActionListener(e -> updateSelectedStatus("CANCELLED"));

        JButton completeBtn = UIUtils.createSecondaryButton("Mark Completed");
        completeBtn.setForeground(UIUtils.SUCCESS);
        completeBtn.addActionListener(e -> updateSelectedStatus("COMPLETED"));

        JButton reloadBtn = UIUtils.createSecondaryButton("Refresh");
        reloadBtn.addActionListener(e -> refreshData());

        tableActions.add(cancelBtn);
        tableActions.add(completeBtn);
        tableActions.add(reloadBtn);

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

        refreshData();
    }

    public void refreshData() {
        loadChoices();
        loadAppointments();
    }

    private void loadChoices() {
        patientBox.removeAllItems();
        doctorBox.removeAllItems();

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {

            ResultSet rsPatients = st.executeQuery("SELECT patient_id, name FROM patients ORDER BY name");
            while (rsPatients.next()) {
                patientBox.addItem(new Choice(rsPatients.getInt("patient_id"), rsPatients.getString("name")));
            }

            ResultSet rsDoctors = st.executeQuery("SELECT doctor_id, name, specialization FROM doctors ORDER BY name");
            while (rsDoctors.next()) {
                doctorBox.addItem(new Choice(rsDoctors.getInt("doctor_id"),
                    rsDoctors.getString("name") + " (" + rsDoctors.getString("specialization") + ")"));
            }
        } catch (SQLException ex) {
            System.err.println("Could not load dropdown items: " + ex.getMessage());
        }
    }

    private void bookAppointment() {
        Choice selectedPatient = (Choice) patientBox.getSelectedItem();
        Choice selectedDoctor = (Choice) doctorBox.getSelectedItem();

        if (selectedPatient == null || selectedDoctor == null) {
            JOptionPane.showMessageDialog(this,
                "Please make sure at least one patient and one doctor exist in the database.",
                "Missing Record", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date appDate = Date.valueOf(dateField.getText().trim());
            String timeStr = timeField.getText().trim();
            Time appTime = Time.valueOf(timeStr.length() == 5 ? timeStr + ":00" : timeStr);
            String reason = reasonField.getText().trim();
            String status = (String) statusBox.getSelectedItem();

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO appointments(patient_id, doctor_id, appointment_date, appointment_time, reason, status) VALUES(?,?,?,?,?,?)")) {
                ps.setInt(1, selectedPatient.id);
                ps.setInt(2, selectedDoctor.id);
                ps.setDate(3, appDate);
                ps.setTime(4, appTime);
                ps.setString(5, reason);
                ps.setString(6, status);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Appointment successfully booked!", "Success", JOptionPane.INFORMATION_MESSAGE);
            reasonField.setText("");
            loadAppointments();
            parentDashboard.refreshMetrics();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                "Invalid date/time format. Use YYYY-MM-DD for date and HH:MM for time (24h format).",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelectedStatus(String newStatus) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table first.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int appointmentId = (int) table.getValueAt(selectedRow, 0);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE appointments SET status = ? WHERE appointment_id = ?")) {
            ps.setString(1, newStatus);
            ps.setInt(2, appointmentId);
            ps.executeUpdate();
            loadAppointments();
            parentDashboard.refreshMetrics();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Update Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadAppointments() {
        model.setRowCount(0);
        String sql = "SELECT a.appointment_id, p.name AS patient_name, d.name AS doctor_name, " +
                     "a.appointment_date, a.appointment_time, a.reason, a.status " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("appointment_id"),
                    rs.getString("patient_name"),
                    rs.getString("doctor_name"),
                    rs.getDate("appointment_date"),
                    rs.getTime("appointment_time"),
                    rs.getString("reason"),
                    rs.getString("status")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load appointments: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
