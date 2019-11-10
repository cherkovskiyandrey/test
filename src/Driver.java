import java.time.Duration;
import java.time.LocalDateTime;

public class Driver {
    private final String id;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String descr;


    public Driver(String id, LocalDateTime startTime, LocalDateTime endTime, String descr) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.descr = descr;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getDescr() {
        return descr;
    }

    @Override
    public String toString() {
        Duration duration = Duration.between(startTime, endTime);
        return String.format(descr + " | %02d:%02d.%03d", duration.toMinutes() % 60, duration.getSeconds() % 60, duration.toMillis() % 1000);
    }

    public long getTimeInMs() {
        return Duration.between(startTime, endTime).toMillis();
    }
}
