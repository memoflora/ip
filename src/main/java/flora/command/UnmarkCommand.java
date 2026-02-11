package flora.command;

import flora.exception.FloraException;
import flora.storage.Storage;
import flora.task.Task;
import flora.task.TaskList;
import flora.ui.Ui;

/**
 * Command to mark a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Constructs an UnmarkCommand with the given 1-based task index.
     *
     * @param taskIndex The 1-based index of the task to mark as not done.
     */
    public UnmarkCommand(int taskIndex) {
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
        if (!task.isDone()) {
            ui.showAlreadyUnmarked();
            return;
        }

        task.unmark();
        storage.save(tasks);
        ui.showUnmarkedTask(task);
    }
}
