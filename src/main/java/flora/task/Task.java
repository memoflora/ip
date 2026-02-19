package flora.task;

import java.time.LocalDateTime;

import flora.exception.FloraException;

/**
 * Represents an abstract task with a description and completion status.
 */
public abstract class Task {
    protected String description;
    protected boolean done = false;

    /**
     * Constructs a Task with the given description, initially not done.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        assert description != null && !description.isBlank() : "Task description must not be null or blank";
        this.description = description;
    }

    /**
     * Returns the description of this task.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is marked as done.
     *
     * @return {@code true} if the task is done, {@code false} otherwise.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        done = true;
        assert done : "Task should be marked as done after mark()";
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        done = false;
        assert !done : "Task should be unmarked after unmark()";
    }

    /**
     * Returns the single-character type identifier for this task.
     *
     * @return The type identifier (e.g., "T", "D", "E").
     */
    protected abstract String getType();

    /**
     * Returns a new task of the same type with the specified fields updated.
     * Pass {@code null} for any field to keep its current value.
     * Fields that don't apply to this task type are collected in the result's
     * invalid fields list rather than throwing an error, so valid fields are
     * always applied.
     *
     * @param newDesc  New description, or {@code null} to keep current.
     * @param newDue   New due date (Deadline only), or {@code null} to keep current.
     * @param newStart New start time (Event only), or {@code null} to keep current.
     * @param newEnd   New end time (Event only), or {@code null} to keep current.
     * @return An {@code EditResult} with the updated task and any invalid field names.
     * @throws FloraException If a valid field value itself is invalid (e.g., start after end).
     */
    public abstract EditResult edit(String newDesc, LocalDateTime newDue,
            LocalDateTime newStart, LocalDateTime newEnd) throws FloraException;

    /**
     * Returns the pipe-delimited string representation of this task for file storage.
     *
     * @return The file-formatted string for this task.
     */
    public String toFileString() {
        return getType() + " | " + (isDone() ? "1" : "0") + " | " + description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[" + getType() + "][" + (isDone() ? "X" : " ") + "] " + description;
    }
}
