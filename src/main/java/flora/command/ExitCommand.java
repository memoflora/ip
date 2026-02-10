package flora.command;

import flora.ui.Ui;
import flora.storage.Storage;
import flora.task.TaskList;

/**
 * Command to exit the Flora application.
 */
public class ExitCommand extends Command {
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showFarewell();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
