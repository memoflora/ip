package flora.command;

import flora.ui.Ui;
import flora.storage.Storage;
import flora.task.Todo;
import flora.task.TaskList;
import flora.exception.FloraException;

public class AddTodoCommand extends Command {
    private final String taskDesc;

    public AddTodoCommand(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws FloraException {
        Todo todo = new Todo(taskDesc);
        tasks.add(todo);
        storage.save(tasks);
        ui.showAddedTask(todo, tasks.size());
    }
}
