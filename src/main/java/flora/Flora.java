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
    private String loadError = null;
    private boolean shouldExit = false;

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
            loadError = "Error loading tasks: " + e.getMessage();
        }
    }

    /**
     * Parses and executes the given user input, returning Flora's response message.
     *
     * @param input The raw user input string.
     * @return The response message to display.
     */
    public String getResponse(String input) {
        assert input != null : "User input must not be null";
        try {
            Command command = Parser.parse(input);
            assert command != null : "Parser must return a non-null command";
            command.execute(tasks, storage);
            shouldExit = command.isExit();
            return command.getMessage();
        } catch (FloraException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Returns the welcome message shown when the application starts.
     * If there was an error loading tasks from storage, the error message is prepended.
     *
     * @return The welcome message string, optionally preceded by a load error message.
     */
    public String getWelcomeMessage() {
        String welcome = "Hi there! Flora here.\nAsk me anything!";
        if (loadError != null) {
            return loadError + "\n" + welcome;
        }
        return welcome;
    }

    /**
     * Returns whether the application should exit after the last command.
     *
     * @return {@code true} if an exit command was executed, {@code false} otherwise.
     */
    public boolean isExit() {
        return shouldExit;
    }

    public static void main(String[] args) {
        System.out.println("Hi!");
    }
}
