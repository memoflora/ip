package flora.command;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import flora.storage.Storage;
import flora.task.TaskList;

/**
 * Command to find tasks whose descriptions contain a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;
    private TaskList matchingTasks;

    /**
     * Constructs a FindCommand with the given search keyword.
     *
     * @param keyword The keyword to search for in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Storage storage) {
        matchingTasks = tasks.find(keyword);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        if (matchingTasks.size() == 0) {
            return "No matching tasks.";
        }
        String items = IntStream.rangeClosed(1, matchingTasks.size())
                .mapToObj(i -> "\n" + i + "." + matchingTasks.get(i))
                .collect(Collectors.joining());
        return "Here are the matching tasks in your list: " + items;
    }
}
