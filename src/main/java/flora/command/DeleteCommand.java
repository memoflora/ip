package flora.command;

import flora.ui.Ui;
import flora.storage.Storage;
import flora.task.Task;
import flora.task.TaskList;
import flora.exception.FloraException;

/**
 * Command to delete a task from the task list by its index.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Constructs a DeleteCommand with the given 1-based task index.
     *
     * @param taskIndex The 1-based index of the task to delete.
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws FloraException {
        if (taskIndex < 1 || taskIndex > tasks.size()) {
            throw new FloraException("Bro's out of bounds");
        }

        Task task = tasks.remove(taskIndex);
        storage.save(tasks);
        ui.showDeletedTask(task, tasks.size());
    }
}
