package flora.command;

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
        assert keyword != null && !keyword.isBlank() : "Find keyword must not be null or blank";
        this.keyword = keyword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Storage storage) {
        matchingTasks = tasks.find(keyword);
        assert matchingTasks != null : "Find result must not be null";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        if (matchingTasks.size() == 0) {
            return "No matching tasks.";
        }
        StringBuilder sb = new StringBuilder("Here are the matching tasks in your list: ");
        for (int i = 1; i <= matchingTasks.size(); i++) {
            sb.append("\n").append(i).append(".").append(matchingTasks.get(i));
        }
        return sb.toString();
    }
}
