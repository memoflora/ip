public abstract class Task {
    protected String description;
    protected boolean done;

    public Task(String description) {
        this.description = description;
        this.done = false;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return done;
    }

    public void mark() {
        done = true;
    }

    public void unmark() {
        done = false;
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
