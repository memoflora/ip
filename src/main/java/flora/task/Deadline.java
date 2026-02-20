package flora.task;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a task with a deadline (due date/time).
 */
public class Deadline extends Task {
    private final LocalDateTime due;

    /**
     * Constructs a Deadline task with the given description and due date.
     *
     * @param description The description of the deadline task.
     * @param due         The due date and time.
     */
    public Deadline(String description, LocalDateTime due) {
        super(description);
        assert due != null : "Due date must not be null";
        this.due = due;
    }

    /**
     * Converts a date-time to the file storage format, omitting time if midnight.
     *
     * @param dateTime The date-time to convert.
     * @return The formatted date-time string for file storage.
     */
    private String convertDateTimeToFileString(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFileFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy[ HH:mm]");
        if (!dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(dateTimeFileFmt);
        } else {
            return dateTime.toLocalDate().format(dateTimeFileFmt);
        }
    }

    /**
     * Converts a date-time to a human-readable display format, omitting time if midnight.
     *
     * @param dateTime The date-time to convert.
     * @return The formatted date-time string for display.
     */
    private String convertDateTimeToString(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("d MMM yyyy[ 'at' HH:mm]");
        if (!dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(dateTimeFmt);
        } else {
            return dateTime.toLocalDate().format(dateTimeFmt);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getType() {
        return "D";
    }

    /**
     * {@inheritDoc}
     * Deadlines support /desc and /by. Any event-only fields (/from, /to) are
     * collected as invalid and ignored, but valid fields are still applied.
     */
    @Override
    public EditResult edit(String newDesc, LocalDateTime newDue,
            LocalDateTime newStart, LocalDateTime newEnd) {
        List<String> invalid = new ArrayList<>();
        if (newStart != null) {
            invalid.add("/from");
        }
        if (newEnd != null) {
            invalid.add("/to");
        }
        String desc = newDesc != null ? newDesc : description;
        LocalDateTime updatedDue = newDue != null ? newDue : this.due;
        Deadline updated = new Deadline(desc, updatedDue);
        if (done) {
            updated.mark();
        }
        return new EditResult(updated, invalid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDetailsKey() {
        return "D|" + description + "|" + due.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + convertDateTimeToFileString(due);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + convertDateTimeToString(due) + ")";
    }
}
