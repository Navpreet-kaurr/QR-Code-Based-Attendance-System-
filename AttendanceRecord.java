import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AttendanceRecord {
    private int studentId;
    private LocalDate date;
    private String qrToken;
    private LocalDateTime timestamp;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public AttendanceRecord(int studentId, LocalDate date, String qrToken, LocalDateTime timestamp) {
        this.studentId = studentId;
        this.date = date;
        this.qrToken = qrToken;
        this.timestamp = timestamp;
    }

    public int getStudentId() { return studentId; }
    public LocalDate getDate() { return date; }
    public String getQrToken() { return qrToken; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        // CSV: studentId,date,qrToken,timestamp
        return studentId + "," + date.format(DATE_FMT) + "," + qrToken + "," + timestamp.format(TS_FMT);
    }

    public static AttendanceRecord fromCsv(String line) {
        String[] p = line.split(",", 4);
        if (p.length < 4) return null;
        try {
            int sid = Integer.parseInt(p[0].trim());
            LocalDate d = LocalDate.parse(p[1].trim(), DATE_FMT);
            String token = p[2].trim();
            LocalDateTime ts = LocalDateTime.parse(p[3].trim(), TS_FMT);
            return new AttendanceRecord(sid, d, token, ts);
        } catch (Exception e) {
            return null;
        }
    }
}