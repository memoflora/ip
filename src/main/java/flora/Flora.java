package flora;

import flora.command.Command;
import flora.exception.FloraException;
import flora.parser.Parser;
import flora.storage.Storage;
import flora.task.TaskList;
import flora.ui.Ui;

/**
 * Main class for the Flora chatbot application.
 * Manages the interaction between the UI, storage, and task list.
 */
public class Flora {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Constructs a Flora instance with the specified file path for storage.
     *
     * @param filePath Path to the file used for persisting tasks.
     */
    public Flora(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        try {
            tasks = new TaskList(storage.load());
        } catch (FloraException e) {
            ui.showError(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main loop of the chatbot, reading and executing user commands
     * until the exit command is issued.
     */
    public void run() {
        ui.showGreeting();
        boolean isExit = false;
        while (!isExit) {
            try {
                String input = ui.readCommand();
                ui.showLine();
                Command cmd = Parser.parse(input);
                cmd.execute(tasks, ui, storage);
                isExit = cmd.isExit();
            } catch (FloraException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
                ui.showNewLine();
            }
        }
    }

    /**
     * Entry point of the Flora application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Flora("data/tasks.txt").run();
    }
}
