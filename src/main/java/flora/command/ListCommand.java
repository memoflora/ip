package flora.command;

import flora.ui.Ui;
import flora.storage.Storage;
import flora.task.TaskList;

public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
