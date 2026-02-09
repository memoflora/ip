package flora.command;

import java.time.LocalDateTime;

import flora.ui.Ui;
import flora.storage.Storage;
import flora.task.Deadline;
import flora.task.TaskList;
import flora.exception.FloraException;

public class AddDeadlineCommand extends Command {
    private final String taskDesc;
    private final LocalDateTime taskDue;

    public AddDeadlineCommand(String taskDesc, LocalDateTime taskDue) {
        this.taskDesc = taskDesc;
        this.taskDue = taskDue;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws FloraException {
        Deadline deadline = new Deadline(taskDesc, taskDue);
        tasks.add(deadline);
        storage.save(tasks);
        ui.showAddedTask(deadline, tasks.size());
    }
}
