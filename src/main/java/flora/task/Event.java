package flora.task;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import flora.exception.FloraException;

/**
 * Represents a task that spans a time period with a start and end date/time.
 */
public class Event extends Task {
    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Constructs an Event task with the given description, start time, and end time.
     *
     * @param description The description of the event task.
     * @param start       The start date and time.
     * @param end         The end date and time.
     */
    public Event(String description, LocalDateTime start, LocalDateTime end) {
        super(description);
        assert start != null : "Event start time must not be null";
        assert end != null : "Event end time must not be null";
        assert !start.isAfter(end) : "Event start time must not be after end time";
        this.start = start;
        this.end = end;
    }

    /**
     * Converts a date-time to the file storage format.
     * Includes time if either the start or end time is not midnight.
     *
     * @param dateTime The date-time to convert.
     * @return The formatted date-time string for file storage.
     */
    private String convertDateTimeToFileString(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFileFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy[ HH:mm]");
        if (!start.toLocalTime().equals(LocalTime.MIDNIGHT) || !end.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(dateTimeFileFmt);
        } else {
            return dateTime.toLocalDate().format(dateTimeFileFmt);
        }
    }

    /**
     * Converts a date-time to a human-readable display format.
     * Includes time if either the start or end time is not midnight.
     *
     * @param dateTime The date-time to convert.
     * @return The formatted date-time string for display.
     */
    private String convertDateTimeToString(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("d MMM yyyy[ 'at' HH:mm]");
        if (!start.toLocalTime().equals(LocalTime.MIDNIGHT) || !end.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(dateTimeFmt);
        } else {
            return dateTime.toLocalDate().format(dateTimeFmt);
        }
    }

    /**
     * Returns the start date/time of this event.
     *
     * @return The start date/time.
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Returns the end date/time of this event.
     *
     * @return The end date/time.
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getType() {
        return "E";
    }

    /**
     * {@inheritDoc}
     * Events support /desc, /from, and /to. The deadline-only field /by is
     * collected as invalid and ignored, but valid fields are still applied.
     *
     * @throws FloraException If the resulting start time is after the end time.
     */
    @Override
    public EditResult edit(String newDesc, LocalDateTime newDue,
            LocalDateTime newStart, LocalDateTime newEnd) throws FloraException {
        List<String> invalid = new ArrayList<>();
        if (newDue != null) {
            invalid.add("/by");
        }
        String desc = newDesc != null ? newDesc : description;
        LocalDateTime updatedStart = newStart != null ? newStart : this.start;
        LocalDateTime updatedEnd = newEnd != null ? newEnd : this.end;
        if (updatedStart.isAfter(updatedEnd)) {
            throw new FloraException("Start time cannot be after end time.");
        }
        Event updated = new Event(desc, updatedStart, updatedEnd);
        if (done) {
            updated.mark();
        }
        return new EditResult(updated, invalid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + convertDateTimeToFileString(start)
                + " | " + convertDateTimeToFileString(end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + convertDateTimeToString(start)
                + " to: " + convertDateTimeToString(end) + ")";
    }
}
