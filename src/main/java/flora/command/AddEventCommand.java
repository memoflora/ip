package flora.command;

import java.time.LocalDateTime;

import flora.exception.FloraException;
import flora.storage.Storage;
import flora.task.Event;
import flora.task.TaskList;

/**
 * Command to add a new event task to the task list.
 */
public class AddEventCommand extends Command {
    private final String taskDesc;
    private final LocalDateTime taskStart;
    private final LocalDateTime taskEnd;
    private Event event;
    private int size;

    /**
     * Constructs an AddEventCommand with the given description, start time, and end time.
     *
     * @param taskDesc  Description of the event task.
     * @param taskStart Start date and time of the event.
     * @param taskEnd   End date and time of the event.
     */
    public AddEventCommand(String taskDesc, LocalDateTime taskStart, LocalDateTime taskEnd) {
        assert taskDesc != null && !taskDesc.isBlank() : "Event description must not be null or blank";
        assert taskStart != null : "Event start time must not be null";
        assert taskEnd != null : "Event end time must not be null";
        this.taskDesc = taskDesc;
        this.taskStart = taskStart;
        this.taskEnd = taskEnd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Storage storage) throws FloraException {
        event = new Event(taskDesc, taskStart, taskEnd);
        tasks.add(event);
        storage.save(tasks);
        size = tasks.size();
        assert size > 0 : "Task list must be non-empty after adding a task";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "Got it. I've added this task:\n  " + event
                + "\nNow you have " + size + " task" + (size > 1 ? "s" : "") + " in the list.";
    }
}
