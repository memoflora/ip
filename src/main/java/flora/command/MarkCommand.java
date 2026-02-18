package flora.command;

import flora.exception.FloraException;
import flora.storage.Storage;
import flora.task.Task;
import flora.task.TaskList;

/**
 * Command to mark a task as done.
 */
public class MarkCommand extends Command {
    private final int taskIndex;
    private Task task;
    private boolean isMarked;

    /**
     * Constructs a MarkCommand with the given 1-based task index.
     *
     * @param taskIndex The 1-based index of the task to mark as done.
     */
    public MarkCommand(int taskIndex) {
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
        isMarked = task.isDone();
        if (isMarked) {
            return;
        }

        task.mark();
        storage.save(tasks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        if (isMarked) {
            return "That task is already marked bro";
        }
        return "Nice! I've marked this task as done:\n  " + task;
    }
}
