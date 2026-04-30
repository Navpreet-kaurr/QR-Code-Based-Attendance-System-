# QR Code Attendance System

A dual-interface attendance tracking system that uses QR codes for student attendance. Features both a console application and a web-based frontend.

Live Demo - https://navpreet-kaurr.github.io/QR-Code-Based-Attendance-System-/

## Features

- **Student Management**: Add and list students with ID and name
- **QR Code Generation**: Generate time-limited QR tokens for attendance sessions
- **Attendance Marking**: Students scan QR code to mark their attendance
- **Attendance Tracking**: View attendance records by date
- **Dual Interfaces**: Use either the console or web interface

## Project Structure

```
qratt/
├── Main.java              # Console application entry point
├── Student.java           # Student model class
├── AttendanceRecord.java  # Attendance record model
├── QRGenerator.java       # QR token generation utility
├── Storage.java           # Data persistence (CSV files)
├── index.html             # Web frontend interface
├── students.csv           # Student data storage
├── attendance.csv         # Attendance records storage
├── current_qr.txt         # Current active QR token
└── README.md              # This file
```

## Requirements

- Java 8 or higher (for console application)
- Modern web browser (for web interface)
- Internet connection (for web interface - Google Charts API for QR codes)

## Usage

### Option 1: Console Application

1. Compile the Java files:
   ```bash
   javac *.java
   ```

2. Run the application:
   ```bash
   java Main
   ```

3. Follow the menu prompts:
   - **Add Student**: Enter student ID and name
   - **List Students**: View all registered students
   - **Generate QR**: Create a new QR token for attendance
   - **Mark Attendance**: Enter student ID and token to mark attendance
   - **Show Attendance**: View attendance records

### Option 2: Web Interface

1. Open `index.html` in a web browser
2. Use the web interface to:
   - Add students using the form
   - Generate QR codes (valid for 5 minutes)
   - Enter student ID and token to mark attendance
   - View attendance by date

## How It Works

### QR Code Generation
- A unique token is generated using `QRGenerator.generateToken()`
- The token is valid for a default time period (5 minutes in web interface)
- QR code is displayed as an image using Google Charts API

### Attendance Process
1. Admin generates a QR code/token for a session
2. Students enter their ID and the token to mark attendance
3. Each student can only mark attendance once per day
4. Attendance is recorded with date and timestamp

## Data Storage

- **students.csv**: Stores student records (ID, Name)
- **attendance.csv**: Stores attendance records (StudentID, Date, Token, Timestamp)
- **current_qr.txt**: Stores the current active QR token and expiry time

## Notes

- The console and web interfaces use separate data storage (CSV files vs LocalStorage)
- QR codes are time-limited for security
