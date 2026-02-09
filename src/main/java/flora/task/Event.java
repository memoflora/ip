package flora.task;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Event extends Task {
    private final LocalDateTime start;
    private final LocalDateTime end;

    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    private String convertDateTimeToFileString(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFileFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy[ HH:mm]");
        if (!start.toLocalTime().equals(LocalTime.MIDNIGHT) || !end.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(dateTimeFileFmt);
        } else {
            return dateTime.toLocalDate().format(dateTimeFileFmt);
        }
    }

    private String convertDateTimeToString(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("d MMM yyyy[ 'at' HH:mm]");
        if (!start.toLocalTime().equals(LocalTime.MIDNIGHT) || !end.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(dateTimeFmt);
        } else {
            return dateTime.toLocalDate().format(dateTimeFmt);
        }
    }

    @Override
    protected String getType() {
        return "E";
    }

    @Override
    public String toFileString() {
        return super.toFileString() + " | " + convertDateTimeToFileString(start) + " | " + convertDateTimeToFileString(end);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + convertDateTimeToString(start) + " to: " + convertDateTimeToString(end) + ")";
    }
}
