package flora;

import flora.ui.Ui;
import flora.storage.Storage;
import flora.task.TaskList;
import flora.parser.Parser;
import flora.command.Command;
import flora.exception.FloraException;

public class Flora {
    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

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

    public static void main(String[] args) {
        new Flora("data/tasks.txt").run();
    }
}
