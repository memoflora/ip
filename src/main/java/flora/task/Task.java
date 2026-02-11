package flora.task;

public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        isDone = false;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    public void mark() {
        isDone = true;
    }

    public void unmark() {
        isDone = false;
    }

    protected abstract String getType();

    public String toFileString() {
        return getType() + " | " + (isDone() ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return "[" + getType() + "][" + (isDone() ? "X" : " ") + "] " + description;
    }
}
