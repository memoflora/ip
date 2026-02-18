package flora.command;

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
    public void execute(TaskList tasks, Storage storage) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "Talk to you later—bye!";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
