# Modern Hospital Appointment System (Maven & FlatLaf Edition)

An enterprise-grade Java desktop application for patient management, doctor scheduling, and appointment booking. Modernized from legacy Swing to a responsive, unified **FlatLaf** design with **Maven** dependency management.

---

## 🌟 Key Highlights & Modern Upgrades
1. **Automated Dependency Management (`pom.xml`):**
   - No more manual JAR linking or missing `jdbc:mysql://` driver errors.
   - Includes **MySQL Connector/J 8.4.0** and **FlatLaf 3.5.4**.
2. **Enterprise UI/UX Design:**
   - **Unified Dashboard:** Clean dark sidebar navigation (`#0F172A`) with dynamic `CardLayout` view switching.
   - **Real-Time Stat Badges:** Dynamic overview cards showing counts of Patients, Doctors, and Appointments.
   - **Modern Components:** Rounded input fields, placeholder hints, styled tables with clean padding, and color-coded status badges.
   - **Keyboard Friendly:** Press `ENTER` to quickly log in.
3. **Database Integration:**
   - Fully tested SQL schema with foreign keys, cascading rules, and pre-populated sample records.

---

## 🚀 Quick Setup & Run

### Prerequisites
- **JDK 17 or later** installed.
- **Apache Maven** installed (or use IntelliJ / Eclipse with embedded Maven).
- **MySQL Server** running on `localhost:3306`.

### 1. Database Setup
Open your MySQL terminal, DBeaver, or MySQL Workbench and run:
```sql
source database/hospital_db.sql;
```
*(Or execute the queries inside `database/hospital_db.sql` directly).*

### 2. Configure Credentials
Open `src/main/java/com/hospital/DBConnection.java` and verify your MySQL password:
```java
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";
```

### 3. Run Directly with Maven
In your project directory, execute:
```bash
mvn clean compile exec:java -Dexec.mainClass="com.hospital.Main"
```

### 4. Build Executable Fat JAR
```bash
mvn clean package
java -jar target/HospitalAppointmentSystem-2.0.0.jar
```

---

## 🔑 Demo Login Credentials
- **Username:** `admin`
- **Password:** `admin123`
