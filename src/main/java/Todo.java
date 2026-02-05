public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getType() {
        return "T";
    }

    @Override
    public String toFileString() {
        return super.toFileString();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
