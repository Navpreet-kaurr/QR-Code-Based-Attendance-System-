import java.util.*;
import java.time.*;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static Map<Integer, Student> students;
    private static List<AttendanceRecord> attendance;

    public static void main(String[] args) {
        System.out.println("=== QR Code Attendance System (Console) ===");
        students = Storage.loadStudents();
        attendance = Storage.loadAttendance();

        mainLoop();
    }

    private static void mainLoop() {
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Student");
            System.out.println("2. List Students");
            System.out.println("3. Generate QR (for class/session)");
            System.out.println("4. Mark Attendance (scan QR)");
            System.out.println("5. Show Attendance");
            System.out.println("6. Exit");
            System.out.print("Choice: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1": addStudent(); break;
                case "2": listStudents(); break;
                case "3": generateQR(); break;
                case "4": markAttendance(); break;
                case "5": showAttendance(); break;
                case "6": System.out.println("Goodbye!"); return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void addStudent() {
        try {
            System.out.print("Enter integer ID: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            if (students.containsKey(id)) {
                System.out.println("Student with this ID already exists: " + students.get(id).getName());
                return;
            }
            System.out.print("Enter name: ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
                return;
            }
            Student s = new Student(id, name);
            if (Storage.saveStudent(s)) {
                students.put(id, s);
                System.out.println("Student saved.");
            } else {
                System.out.println("Failed to save student.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter an integer.");
        }
    }

    private static void listStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("Students:");
        students.values().forEach(s -> System.out.println("ID: " + s.getId() + " | " + s.getName()));
    }

    private static void generateQR() {
        String token = QRGenerator.generateToken();
        Instant expiry = Instant.now().plus(QRGenerator.defaultValidity());
        Storage.saveCurrentQR(token, expiry);
        System.out.println("\n=== QR Generated ===");
        System.out.println("Token (simulate QR): " + token);
        System.out.println("Valid until: " + expiry.atZone(ZoneId.systemDefault()).toLocalDateTime());
        System.out.println("(Share this token with students or show it as a QR in real life)");
    }

    private static void markAttendance() {
        try {
            Storage.CurrentQR cur = Storage.loadCurrentQR();
            if (cur == null) {
                System.out.println("No active QR token. Ask admin to generate a QR first.");
                return;
            }
            Instant now = Instant.now();
            if (now.isAfter(cur.expiry)) {
                System.out.println("The QR token has expired. Generate a new one.");
                return;
            }
            System.out.print("Enter student ID: ");
            int sid = Integer.parseInt(sc.nextLine().trim());
            if (!students.containsKey(sid)) {
                System.out.println("Unknown student ID. Ask to register first.");
                return;
            }
            System.out.print("Enter token (from QR): ");
            String provided = sc.nextLine().trim();
            if (!provided.equals(cur.token)) {
                System.out.println("Token does not match. Attendance not recorded.");
                return;
            }

            // check if already marked today
            LocalDate today = LocalDate.now();
            boolean already = attendance.stream()
                .anyMatch(r -> r.getStudentId() == sid && r.getDate().equals(today));
            if (already) {
                System.out.println("Attendance already marked for today for this student.");
                return;
            }

            AttendanceRecord rec = new AttendanceRecord(sid, today, provided, LocalDateTime.now());
            if (Storage.appendAttendance(rec)) {
                attendance.add(rec);
                System.out.println("Attendance marked for " + students.get(sid).getName() + " on " + today);
            } else {
                System.out.println("Failed to save attendance.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private static void showAttendance() {
        if (attendance.isEmpty()) {
            System.out.println("No attendance records yet.");
            return;
        }
        System.out.println("1. Show all");
        System.out.println("2. Show for date (YYYY-MM-DD)");
        System.out.print("Choice: ");
        String ch = sc.nextLine().trim();
        if ("2".equals(ch)) {
            System.out.print("Enter date (YYYY-MM-DD): ");
            String ds = sc.nextLine().trim();
            try {
                LocalDate d = LocalDate.parse(ds);
                attendance.stream()
                    .filter(r -> r.getDate().equals(d))
                    .forEach(r -> printRecord(r));
            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
        } else {
            attendance.forEach(r -> printRecord(r));
        }
    }

    private static void printRecord(AttendanceRecord r) {
        String name = students.containsKey(r.getStudentId()) ? students.get(r.getStudentId()).getName() : "Unknown";
        System.out.println("ID: " + r.getStudentId() + " | Name: " + name + " | Date: " + r.getDate() + " | Time: " + r.getTimestamp());
    }
}