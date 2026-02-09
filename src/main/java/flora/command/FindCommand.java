package flora.command;

import flora.ui.Ui;
import flora.storage.Storage;
import flora.task.TaskList;

public class FindCommand extends Command {
    private final String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        TaskList matchingTasks = tasks.find(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
