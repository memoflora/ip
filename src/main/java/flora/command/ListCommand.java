package flora.command;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        String items = IntStream.rangeClosed(1, taskList.size())
                .mapToObj(i -> "\n" + i + "." + taskList.get(i))
                .collect(Collectors.joining());
        return "Here are the tasks in your list: " + items;
    }
}
