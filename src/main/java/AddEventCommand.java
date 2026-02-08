import java.time.LocalDateTime;

public class AddEventCommand extends Command {
    private final String taskDesc;
    private final LocalDateTime taskStart;
    private final LocalDateTime taskEnd;

    public AddEventCommand(String taskDesc, LocalDateTime taskStart, LocalDateTime taskEnd) {
        this.taskDesc = taskDesc;
        this.taskStart = taskStart;
        this.taskEnd = taskEnd;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws FloraException {
        Event event = new Event(taskDesc, taskStart, taskEnd);
        tasks.add(event);
        storage.save(tasks);
        ui.showAddedTask(event, tasks.size());
    }
}
