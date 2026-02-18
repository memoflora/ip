package flora;

import flora.command.Command;
import flora.exception.FloraException;
import flora.parser.Parser;
import flora.storage.Storage;
import flora.task.TaskList;

/**
 * The main class for the Flora chatbot application.
 */
public class Flora {
    private final Storage storage;
    private TaskList tasks;
    private boolean exit = false;

    /**
     * Constructs a Flora instance, loading tasks from the default storage file.
     */
    public Flora() {
        String filePath = "data/tasks.txt";
        storage = new Storage(filePath);

        try {
            tasks = new TaskList(storage.load());
        } catch (FloraException e) {
            tasks = new TaskList();
        }
    }

    /**
     * Parses and executes the given user input, returning Flora's response message.
     *
     * @param input The raw user input string.
     * @return The response message to display.
     */
    public String getResponse(String input) {
        try {
            Command c = Parser.parse(input);
            c.execute(tasks, storage);
            exit = c.isExit();
            return c.getMessage();
        } catch (FloraException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Returns the welcome message shown when the application starts.
     *
     * @return The welcome message string.
     */
    public String getWelcomeMessage() {
        return "Hi there! Flora here.\nAsk me anything!";
    }

    /**
     * Returns whether the application should exit after the last command.
     *
     * @return {@code true} if an exit command was executed, {@code false} otherwise.
     */
    public boolean isExit() {
        return exit;
    }

    public static void main(String[] args) {
        System.out.println("Hi!");
    }
}
