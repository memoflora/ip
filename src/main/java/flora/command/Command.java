package flora.command;

import flora.ui.Ui;
import flora.storage.Storage;
import flora.task.TaskList;
import flora.exception.FloraException;

public abstract class Command {
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws FloraException;

    public boolean isExit() {
        return false;
    }
}
