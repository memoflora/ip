package flora.command;

import flora.exception.FloraException;
import flora.storage.Storage;
import flora.task.TaskList;
import flora.task.Todo;

/**
 * Command to add a new todo task to the task list.
 */
public class AddTodoCommand extends Command {
    private final String taskDesc;
    private Todo todo;
    private int size;

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
    public void execute(TaskList tasks, Storage storage) throws FloraException {
        todo = new Todo(taskDesc);
        tasks.add(todo);
        storage.save(tasks);
        size = tasks.size();
        message = getMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "Got it. I've added this task:\n  " + todo
                + "\nNow you have " + size + " task" + (size > 1 ? "s" : "") + " in the list.";
    }
}
