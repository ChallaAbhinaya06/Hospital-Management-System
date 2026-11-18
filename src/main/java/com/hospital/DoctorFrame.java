package com.hospital;

import javax.swing.*;

public class DoctorFrame extends JFrame {
    public DoctorFrame() {
        setTitle("Doctor Management");
        setSize(850, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(new DoctorPanel(new DashboardFrame()));
    }
}
