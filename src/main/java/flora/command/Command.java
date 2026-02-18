package flora.command;

import flora.exception.FloraException;
import flora.storage.Storage;
import flora.task.TaskList;

/**
 * Represents an abstract command that can be executed by the chatbot.
 */
public abstract class Command {
    protected String message;

    public abstract void execute(TaskList tasks, Storage storage) throws FloraException;

    /**
     * Returns the message string to display in the GUI for this command.
     *
     * @return The message string.
     */
    public abstract String getMessage();

    /**
     * Returns the message to display in the GUI after executing this command.
     *
     * @return The message string.
     */
    public String getString() {
        return message;
    }

    /**
     * Returns whether this command causes the application to exit.
     *
     * @return {@code true} if this is an exit command, {@code false} otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
