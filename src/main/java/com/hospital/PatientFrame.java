package com.hospital;

import javax.swing.*;

public class PatientFrame extends JFrame {
    public PatientFrame() {
        setTitle("Patient Management");
        setSize(850, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(new PatientPanel(new DashboardFrame()));
    }
}
