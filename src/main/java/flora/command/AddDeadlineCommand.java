package flora.command;

import java.time.LocalDateTime;

import flora.exception.FloraException;
import flora.storage.Storage;
import flora.task.Deadline;
import flora.task.TaskList;

/**
 * Command to add a new deadline task to the task list.
 */
public class AddDeadlineCommand extends Command {
    private final String taskDesc;
    private final LocalDateTime taskDue;
    private Deadline deadline;
    private int size;

    /**
     * Constructs an AddDeadlineCommand with the given description and due date.
     *
     * @param taskDesc Description of the deadline task.
     * @param taskDue  Due date and time of the deadline.
     */
    public AddDeadlineCommand(String taskDesc, LocalDateTime taskDue) {
        assert taskDesc != null && !taskDesc.isBlank() : "Deadline description must not be null or blank";
        assert taskDue != null : "Deadline due date must not be null";
        this.taskDesc = taskDesc;
        this.taskDue = taskDue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Storage storage) throws FloraException {
        deadline = new Deadline(taskDesc, taskDue);
        if (tasks.containsTaskWithDetails(deadline)) {
            throw new FloraException("This task already exists: " + deadline);
        }
        tasks.add(deadline);
        storage.save(tasks);
        size = tasks.size();
        assert size > 0 : "Task list must be non-empty after adding a task";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "Got it. I've added this task:\n  " + deadline
                + "\nNow you have " + size + " task" + (size > 1 ? "s" : "") + " in the list.";
    }
}
