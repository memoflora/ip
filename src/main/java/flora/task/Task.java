package flora.task;

/**
 * Represents an abstract task with a description and completion status.
 */
public abstract class Task {
    protected String description;
    protected boolean done;

    /**
     * Constructs a Task with the given description, initially not done.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.done = false;
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
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        done = false;
    }

    /**
     * Returns the single-character type identifier for this task.
     *
     * @return The type identifier (e.g., "T", "D", "E").
     */
    protected abstract String getType();

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
