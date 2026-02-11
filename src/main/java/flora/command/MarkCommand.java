package flora.command;

import flora.exception.FloraException;
import flora.storage.Storage;
import flora.task.Task;
import flora.task.TaskList;
import flora.ui.Ui;

/**
 * Command to mark a task as done.
 */
public class MarkCommand extends Command {
    private final int taskIndex;

    /**
     * Constructs a MarkCommand with the given 1-based task index.
     *
     * @param taskIndex The 1-based index of the task to mark as done.
     */
    public MarkCommand(int taskIndex) {
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

        Task task = tasks.get(taskIndex);
        if (task.isDone()) {
            ui.showAlreadyMarked();
            return;
        }

        task.mark();
        storage.save(tasks);
        ui.showMarkedTask(task);
    }
}
