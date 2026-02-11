package flora.command;

import flora.exception.FloraException;
import flora.storage.Storage;
import flora.task.TaskList;
import flora.ui.Ui;

/**
 * Represents an abstract command that can be executed by the chatbot.
 */
public abstract class Command {
    /**
     * Executes this command with the given task list, UI, and storage.
     *
     * @param tasks   The task list to operate on.
     * @param ui      The UI to display output.
     * @param storage The storage to persist changes.
     * @throws FloraException If an error occurs during execution.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws FloraException;

    /**
     * Returns whether this command causes the application to exit.
     *
     * @return {@code true} if this is an exit command, {@code false} otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
