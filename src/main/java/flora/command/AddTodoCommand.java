package flora.command;

import flora.ui.Ui;
import flora.storage.Storage;
import flora.task.Todo;
import flora.task.TaskList;
import flora.exception.FloraException;

/**
 * Command to add a new todo task to the task list.
 */
public class AddTodoCommand extends Command {
    private final String taskDesc;

    /**
     * Constructs an AddTodoCommand with the given description.
     *
     * @param taskDesc Description of the todo task.
     */
    public AddTodoCommand(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws FloraException {
        Todo todo = new Todo(taskDesc);
        tasks.add(todo);
        storage.save(tasks);
        ui.showAddedTask(todo, tasks.size());
    }
}
