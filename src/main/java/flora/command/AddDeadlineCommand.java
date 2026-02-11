package flora.command;

import java.time.LocalDateTime;

import flora.exception.FloraException;
import flora.storage.Storage;
import flora.task.Deadline;
import flora.task.TaskList;
import flora.ui.Ui;

/**
 * Command to add a new deadline task to the task list.
 */
public class AddDeadlineCommand extends Command {
    private final String taskDesc;
    private final LocalDateTime taskDue;

    /**
     * Constructs an AddDeadlineCommand with the given description and due date.
     *
     * @param taskDesc Description of the deadline task.
     * @param taskDue  Due date and time of the deadline.
     */
    public AddDeadlineCommand(String taskDesc, LocalDateTime taskDue) {
        this.taskDesc = taskDesc;
        this.taskDue = taskDue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws FloraException {
        Deadline deadline = new Deadline(taskDesc, taskDue);
        tasks.add(deadline);
        storage.save(tasks);
        ui.showAddedTask(deadline, tasks.size());
    }
}
