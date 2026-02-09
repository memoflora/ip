package flora.task;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {
    private final LocalDateTime due;

    public Deadline(String description, LocalDateTime due) {
        super(description);
        this.due = due;
    }

    private String convertDateTimeToFileString(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFileFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy[ HH:mm]");
        if (!dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(dateTimeFileFmt);
        } else {
            return dateTime.toLocalDate().format(dateTimeFileFmt);
        }
    }

    private String convertDateTimeToString(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("d MMM yyyy[ 'at' HH:mm]");
        if (!dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(dateTimeFmt);
        } else {
            return dateTime.toLocalDate().format(dateTimeFmt);
        }
    }

    @Override
    protected String getType() {
        return "D";
    }

    @Override
    public String toFileString() {
        return super.toFileString() + " | " + convertDateTimeToFileString(due);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + convertDateTimeToString(due) + ")";
    }
}
