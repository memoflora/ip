package flora.command;

import flora.storage.Storage;
import flora.task.TaskList;

/**
 * Command to display all tasks in the task list.
 */
public class ListCommand extends Command {
    private TaskList taskList;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Storage storage) {
        taskList = tasks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        if (taskList.size() == 0) {
            return "Your list is empty.";
        }
        StringBuilder sb = new StringBuilder("Here are the tasks in your list: ");
        for (int i = 1; i <= taskList.size(); i++) {
            sb.append("\n").append(i).append(".").append(taskList.get(i));
        }
        return sb.toString();
    }
}
