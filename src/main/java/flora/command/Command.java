package flora.command;

import flora.exception.FloraException;
import flora.storage.Storage;
import flora.task.TaskList;
import flora.ui.Ui;

public abstract class Command {
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws FloraException;

    public boolean isExit() {
        return false;
    }
}
