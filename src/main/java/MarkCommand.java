public class MarkCommand extends Command {
    private final int taskIndex;

    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws FloraException {
        if (taskIndex < 1 || taskIndex > tasks.size()) {
            throw new FloraException("Bro's out of bounds");
        }

        Task task = tasks.get(taskIndex);
        if (task.isDone()) {
            ui.showAlreadyMarked();
            return;
        }

        task.mark();
        storage.save(tasks);
        ui.showMarkedTask(task);
    }
}
