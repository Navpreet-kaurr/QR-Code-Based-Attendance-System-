import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.*;

public class Storage {
    private static final Path STUDENTS_FILE = Paths.get("students.csv");
    private static final Path ATTENDANCE_FILE = Paths.get("attendance.csv");
    private static final Path CURRENT_QR_FILE = Paths.get("current_qr.txt");

    // Ensure files exist
    static {
        try {
            if (!Files.exists(STUDENTS_FILE)) Files.createFile(STUDENTS_FILE);
            if (!Files.exists(ATTENDANCE_FILE)) Files.createFile(ATTENDANCE_FILE);
        } catch (IOException e) {
            System.err.println("Error initializing storage files: " + e.getMessage());
        }
    }

    public static Map<Integer, Student> loadStudents() {
        Map<Integer, Student> map = new TreeMap<>();
        try (BufferedReader br = Files.newBufferedReader(STUDENTS_FILE)) {
            String line;
            while ((line = br.readLine()) != null) {
                Student s = Student.fromCsv(line);
                if (s != null) map.put(s.getId(), s);
            }
        } catch (IOException e) {
            System.err.println("Failed to load students: " + e.getMessage());
        }
        return map;
    }

    public static boolean saveStudent(Student s) {
        // append to file
        try (BufferedWriter bw = Files.newBufferedWriter(STUDENTS_FILE, StandardOpenOption.APPEND)) {
            bw.write(s.toString());
            bw.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save student: " + e.getMessage());
            return false;
        }
    }

    public static List<AttendanceRecord> loadAttendance() {
        List<AttendanceRecord> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(ATTENDANCE_FILE)) {
            String line;
            while ((line = br.readLine()) != null) {
                AttendanceRecord r = AttendanceRecord.fromCsv(line);
                if (r != null) list.add(r);
            }
        } catch (IOException e) {
            System.err.println("Failed to load attendance: " + e.getMessage());
        }
        return list;
    }

    public static boolean appendAttendance(AttendanceRecord r) {
        try (BufferedWriter bw = Files.newBufferedWriter(ATTENDANCE_FILE, StandardOpenOption.APPEND)) {
            bw.write(r.toString());
            bw.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save attendance: " + e.getMessage());
            return false;
        }
    }

    // Current QR file format: token|expiryEpochMillis
    public static void saveCurrentQR(String token, Instant expiry) {
        String line = token + "|" + expiry.toEpochMilli();
        try {
            Files.writeString(CURRENT_QR_FILE, line, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to write current QR: " + e.getMessage());
        }
    }

    public static CurrentQR loadCurrentQR() {
        try {
            if (!Files.exists(CURRENT_QR_FILE)) return null;
            String content = Files.readString(CURRENT_QR_FILE).trim();
            if (content.isEmpty()) return null;
            String[] p = content.split("\\|", 2);
            if (p.length < 2) return null;
            String token = p[0];
            long epoch = Long.parseLong(p[1]);
            Instant expiry = Instant.ofEpochMilli(epoch);
            return new CurrentQR(token, expiry);
        } catch (Exception e) {
            System.err.println("Failed to read current QR: " + e.getMessage());
            return null;
        }
    }

    // small helper class
    public static class CurrentQR {
        public final String token;
        public final Instant expiry;
        public CurrentQR(String token, Instant expiry) {
            this.token = token;
            this.expiry = expiry;
        }
    }
}