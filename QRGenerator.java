import java.util.UUID;
import java.time.Instant;
import java.time.Duration;

public class QRGenerator {
    /**
     * Generate a random token (UUID).
     */
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Default expiry duration for generated tokens in seconds.
     * Change here if you want longer/shorter validity.
     */
    public static Duration defaultValidity() {
        return Duration.ofMinutes(5); // 5 minutes
    }
}