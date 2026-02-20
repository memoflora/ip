package flora.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a simple todo task with no date/time attached.
 */
public class Todo extends Task {
    /**
     * Constructs a Todo task with the given description.
     *
     * @param description The description of the todo task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getType() {
        return "T";
    }

    /**
     * {@inheritDoc}
     * Todos only support /desc. Any date fields are collected as invalid and ignored.
     */
    @Override
    public EditResult edit(String newDesc, LocalDateTime newDue,
            LocalDateTime newStart, LocalDateTime newEnd) {
        List<String> invalid = new ArrayList<>();
        if (newDue != null) {
            invalid.add("/by");
        }
        if (newStart != null) {
            invalid.add("/from");
        }
        if (newEnd != null) {
            invalid.add("/to");
        }
        String desc = newDesc != null ? newDesc : description;
        Todo updated = new Todo(desc);
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
        return "T|" + description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toFileString() {
        return super.toFileString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
