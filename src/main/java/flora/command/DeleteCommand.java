package flora.command;

import flora.exception.FloraException;
import flora.storage.Storage;
import flora.task.Task;
import flora.task.TaskList;

/**
 * Command to delete a task from the task list by its index.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;
    private Task task;
    private int size;

    /**
     * Constructs a DeleteCommand with the given 1-based task index.
     *
     * @param taskIndex The 1-based index of the task to delete.
     */
    public DeleteCommand(int taskIndex) {
        assert taskIndex > 0 : "Task index must be positive";
        this.taskIndex = taskIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Storage storage) throws FloraException {
        if (taskIndex < 1 || taskIndex > tasks.size()) {
            throw new FloraException("Bro's out of bounds");
        }

        task = tasks.remove(taskIndex);
        assert task != null : "Removed task must not be null";
        storage.save(tasks);
        size = tasks.size();
        assert size >= 0 : "Task list size must not be negative after deletion";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "Noted. I've removed this task:\n  " + task
                + "\nNow you have " + size + " task" + (size > 1 ? "s" : "") + " in the list.";
    }
}
