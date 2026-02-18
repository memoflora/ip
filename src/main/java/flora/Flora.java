package flora;

import flora.command.Command;
import flora.exception.FloraException;
import flora.parser.Parser;
import flora.storage.Storage;
import flora.task.TaskList;

public class Flora {
    private final Storage storage;
    private TaskList tasks;

    public Flora() {
        String filePath = "data/tasks.txt";
        storage = new Storage(filePath);

        try {
            tasks = new TaskList(storage.load());
        } catch (FloraException e) {
            tasks = new TaskList();
        }
    }

    public String getResponse(String input) {
        try {
            Command c = Parser.parse(input);
            c.execute(tasks, storage);
            return c.getString();
        } catch (FloraException e) {
            return "Error: " + e.getMessage();
        }
    }
}
