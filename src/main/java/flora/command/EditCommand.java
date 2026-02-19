package flora.command;

import java.time.LocalDateTime;

import flora.exception.FloraException;
import flora.storage.Storage;
import flora.task.EditResult;
import flora.task.Task;
import flora.task.TaskList;

/**
 * Edits an existing task in the task list without deleting and re-adding it.
 * The specific fields that can be updated depend on the task type, determined
 * at runtime via polymorphism.
 */
public class EditCommand extends Command {
    private final int taskIndex;
    private final String newDesc;
    private final LocalDateTime newDue;
    private final LocalDateTime newStart;
    private final LocalDateTime newEnd;
    private Task updatedTask;
    private EditResult editResult;

    /**
     * Constructs an EditCommand. Pass {@code null} for any field to leave it unchanged.
     *
     * @param taskIndex The 1-based index of the task to edit.
     * @param newDesc   New description, or {@code null} to keep current.
     * @param newDue    New due date (Deadline only), or {@code null} to keep current.
     * @param newStart  New start time (Event only), or {@code null} to keep current.
     * @param newEnd    New end time (Event only), or {@code null} to keep current.
     */
    public EditCommand(int taskIndex, String newDesc, LocalDateTime newDue,
            LocalDateTime newStart, LocalDateTime newEnd) {
        assert taskIndex > 0 : "Task index must be positive";
        this.taskIndex = taskIndex;
        this.newDesc = newDesc;
        this.newDue = newDue;
        this.newStart = newStart;
        this.newEnd = newEnd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Storage storage) throws FloraException {
        if (taskIndex > tasks.size()) {
            throw new FloraException("Invalid task index: " + taskIndex);
        }
        Task old = tasks.get(taskIndex);
        editResult = old.edit(newDesc, newDue, newStart, newEnd);
        updatedTask = editResult.task();
        tasks.set(taskIndex, updatedTask);
        storage.save(tasks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        StringBuilder msg = new StringBuilder("Got it, I've updated the task:\n  ").append(updatedTask);
        if (editResult.hasInvalidFields()) {
            msg.append("\nIgnored (not applicable to this task type): ")
               .append(String.join(", ", editResult.invalidFields()));
        }
        return msg.toString();
    }
}
