package flora.command;

import flora.exception.FloraException;
import flora.storage.Storage;
import flora.task.Task;
import flora.task.TaskList;

/**
 * Command to mark a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;
    private Task task;
    private boolean isUnmarked;

    /**
     * Constructs an UnmarkCommand with the given 1-based task index.
     *
     * @param taskIndex The 1-based index of the task to mark as not done.
     */
    public UnmarkCommand(int taskIndex) {
        assert taskIndex > 0 : "Task index must be positive";
        this.taskIndex = taskIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Storage storage) throws FloraException {
        if (taskIndex < 1 || taskIndex > tasks.size()) {
            throw new FloraException("Bro's out of bounds");
        }

        task = tasks.get(taskIndex);
        isUnmarked = !task.isDone();
        if (isUnmarked) {
            return;
        }

        task.unmark();
        assert !task.isDone() : "Task must be unmarked after unmark()";
        storage.save(tasks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        if (isUnmarked) {
            return "That task is already unmarked bro";
        }
        return "Ok, I've marked this task as not done yet:\n  " + task;
    }
}
