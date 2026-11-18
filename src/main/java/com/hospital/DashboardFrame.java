package com.hospital;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentArea = new JPanel(cardLayout);

    // Metric Badges on the Dashboard Overview
    private final JLabel patientCountLabel = new JLabel("0", SwingConstants.CENTER);
    private final JLabel doctorCountLabel = new JLabel("0", SwingConstants.CENTER);
    private final JLabel appointmentCountLabel = new JLabel("0", SwingConstants.CENTER);

    private PatientPanel patientPanel;
    private DoctorPanel doctorPanel;
    private AppointmentPanel appointmentPanel;

    private JButton navOverviewBtn;
    private JButton navPatientsBtn;
    private JButton navDoctorsBtn;
    private JButton navAppointmentsBtn;

    public DashboardFrame() {
        setTitle("Hospital Care System - Management Console");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.BACKGROUND);

        // Sidebar on Left
        JPanel sidebar = createSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // Main Content Area with CardLayout
        patientPanel = new PatientPanel(this);
        doctorPanel = new DoctorPanel(this);
        appointmentPanel = new AppointmentPanel(this);

        contentArea.add(createOverviewPanel(), "OVERVIEW");
        contentArea.add(patientPanel, "PATIENTS");
        contentArea.add(doctorPanel, "DOCTORS");
        contentArea.add(appointmentPanel, "APPOINTMENTS");

        root.add(contentArea, BorderLayout.CENTER);
        add(root);

        refreshMetrics();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(UIUtils.SIDEBAR_BG);
        sidebar.setBorder(new EmptyBorder(24, 16, 24, 16));

        // Header / Branding
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);

        JLabel brandTitle = new JLabel("MEDCARE HMS");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandTitle.setForeground(Color.WHITE);

        JLabel brandSub = new JLabel("Enterprise Portal");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brandSub.setForeground(new Color(148, 163, 184));

        brandPanel.add(brandTitle);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(brandSub);
        brandPanel.add(Box.createVerticalStrut(24));

        // Navigation Menu
        JPanel menuPanel = new JPanel(new GridLayout(4, 1, 0, 8));
        menuPanel.setOpaque(false);

        navOverviewBtn = createNavButton("Overview Dashboard", true);
        navPatientsBtn = createNavButton("Patients Management", false);
        navDoctorsBtn = createNavButton("Doctors Directory", false);
        navAppointmentsBtn = createNavButton("Appointments", false);

        navOverviewBtn.addActionListener(e -> switchView("OVERVIEW", navOverviewBtn));
        navPatientsBtn.addActionListener(e -> {
            switchView("PATIENTS", navPatientsBtn);
            patientPanel.loadPatients();
        });
        navDoctorsBtn.addActionListener(e -> {
            switchView("DOCTORS", navDoctorsBtn);
            doctorPanel.loadDoctors();
        });
        navAppointmentsBtn.addActionListener(e -> {
            switchView("APPOINTMENTS", navAppointmentsBtn);
            appointmentPanel.refreshData();
        });

        menuPanel.add(navOverviewBtn);
        menuPanel.add(navPatientsBtn);
        menuPanel.add(navDoctorsBtn);
        menuPanel.add(navAppointmentsBtn);

        // Bottom Logout
        JButton logoutBtn = new JButton("Logout & Exit");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoutBtn.setForeground(new Color(248, 113, 113));
        logoutBtn.setBackground(new Color(30, 41, 59));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(new EmptyBorder(10, 14, 10, 14));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        sidebar.add(brandPanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(logoutBtn, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        setButtonActive(btn, active);
        return btn;
    }

    private void setButtonActive(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(UIUtils.PRIMARY);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(UIUtils.SIDEBAR_BG);
            btn.setForeground(new Color(203, 213, 225));
        }
    }

    public void switchView(String cardName, JButton activeBtn) {
        cardLayout.show(contentArea, cardName);
        setButtonActive(navOverviewBtn, navOverviewBtn == activeBtn);
        setButtonActive(navPatientsBtn, navPatientsBtn == activeBtn);
        setButtonActive(navDoctorsBtn, navDoctorsBtn == activeBtn);
        setButtonActive(navAppointmentsBtn, navAppointmentsBtn == activeBtn);
        refreshMetrics();
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIUtils.BACKGROUND);
        panel.setBorder(new EmptyBorder(28, 32, 28, 32));

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("System Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(UIUtils.TEXT_MAIN);

        JLabel subtitle = new JLabel("Live summary of hospital operations, appointments, and registrations.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(UIUtils.TEXT_MUTED);

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);

        // Stats Cards Row
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(24, 0, 24, 0));

        statsGrid.add(createStatCard("Registered Patients", patientCountLabel, new Color(59, 130, 246)));
        statsGrid.add(createStatCard("Active Doctors", doctorCountLabel, new Color(16, 185, 129)));
        statsGrid.add(createStatCard("Total Appointments", appointmentCountLabel, new Color(139, 92, 246)));

        // Quick Actions Card
        JPanel quickActionsCard = UIUtils.createCard();
        quickActionsCard.setLayout(new BorderLayout());

        JLabel actionTitle = new JLabel("Quick Actions");
        actionTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        actionTitle.setForeground(UIUtils.TEXT_MAIN);
        quickActionsCard.add(actionTitle, BorderLayout.NORTH);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 14));
        actionButtons.setOpaque(false);

        JButton addPat = UIUtils.createPrimaryButton("+ Register New Patient");
        addPat.addActionListener(e -> switchView("PATIENTS", navPatientsBtn));

        JButton addDoc = UIUtils.createPrimaryButton("+ Add New Doctor");
        addDoc.addActionListener(e -> switchView("DOCTORS", navDoctorsBtn));

        JButton bookApp = UIUtils.createPrimaryButton("+ Book Appointment");
        bookApp.addActionListener(e -> switchView("APPOINTMENTS", navAppointmentsBtn));

        JButton syncBtn = UIUtils.createSecondaryButton("Refresh All Stats");
        syncBtn.addActionListener(e -> refreshMetrics());

        actionButtons.add(addPat);
        actionButtons.add(addDoc);
        actionButtons.add(bookApp);
        actionButtons.add(syncBtn);

        quickActionsCard.add(actionButtons, BorderLayout.CENTER);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(statsGrid, BorderLayout.NORTH);
        centerWrap.add(quickActionsCard, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(centerWrap, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = UIUtils.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(4, 0, 0, 0, accentColor),
            new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(UIUtils.TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(UIUtils.TEXT_MAIN);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshMetrics() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {

            ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM patients");
            if (rs1.next()) patientCountLabel.setText(String.valueOf(rs1.getInt(1)));

            ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM doctors");
            if (rs2.next()) doctorCountLabel.setText(String.valueOf(rs2.getInt(1)));

            ResultSet rs3 = st.executeQuery("SELECT COUNT(*) FROM appointments");
            if (rs3.next()) appointmentCountLabel.setText(String.valueOf(rs3.getInt(1)));

        } catch (Exception ex) {
            // Keep existing numbers if database is connecting
        }
    }
}
