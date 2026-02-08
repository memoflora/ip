import java.time.LocalDateTime;

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
