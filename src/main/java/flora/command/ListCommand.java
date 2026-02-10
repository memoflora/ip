package flora.command;

import flora.ui.Ui;
import flora.storage.Storage;
import flora.task.TaskList;

/**
 * Command to display all tasks in the task list.
 */
public class ListCommand extends Command {
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
