package com.hospital;

import javax.swing.*;

public class AppointmentFrame extends JFrame {
    public AppointmentFrame() {
        setTitle("Appointment Booking");
        setSize(950, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(new AppointmentPanel(new DashboardFrame()));
    }
}
