-- Hospital Appointment System Database Setup
CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

CREATE TABLE IF NOT EXISTS patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(20),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id) ON DELETE CASCADE
);

-- Initial Mock Data
INSERT INTO doctors (name, specialization, phone)
SELECT 'Dr. Sarah Jenkins', 'Cardiology', '+1 555-0192'
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE name='Dr. Sarah Jenkins');

INSERT INTO doctors (name, specialization, phone)
SELECT 'Dr. Marcus Vance', 'Neurology', '+1 555-0248'
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE name='Dr. Marcus Vance');

INSERT INTO doctors (name, specialization, phone)
SELECT 'Dr. Elena Rostova', 'Pediatrics', '+1 555-0371'
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE name='Dr. Elena Rostova');

INSERT INTO patients (name, age, gender, phone)
SELECT 'Alexander Wright', 34, 'Male', '+1 555-8821'
WHERE NOT EXISTS (SELECT 1 FROM patients WHERE name='Alexander Wright');

INSERT INTO patients (name, age, gender, phone)
SELECT 'Sophia Chen', 29, 'Female', '+1 555-4912'
WHERE NOT EXISTS (SELECT 1 FROM patients WHERE name='Sophia Chen');

INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, reason, status)
SELECT 1, 1, '2026-11-15', '09:30:00', 'Routine Cardiac Checkup', 'CONFIRMED'
WHERE NOT EXISTS (SELECT 1 FROM appointments WHERE appointment_id=1);
