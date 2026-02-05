public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public boolean getIsDone() {
        return isDone;
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    protected abstract String getType();

    public String toFileString() {
        return getType() + " | " + (getIsDone() ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return "[" + getType() + "][" + (getIsDone() ? "X" : " ") + "] " + description;
    }
}
