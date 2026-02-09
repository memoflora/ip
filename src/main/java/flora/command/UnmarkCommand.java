package flora.command;

import flora.ui.Ui;
import flora.storage.Storage;
import flora.task.Task;
import flora.task.TaskList;
import flora.exception.FloraException;

public class UnmarkCommand extends Command {
    private final int taskIndex;

    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

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
