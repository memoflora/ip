public class DeleteCommand extends Command {
    private final int taskIndex;

    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws FloraException {
        if (taskIndex < 1 || taskIndex > tasks.size()) {
            throw new FloraException("Bro's out of bounds");
        }

        Task task = tasks.remove(taskIndex);
        storage.save(tasks);
        ui.showDeletedTask(task, tasks.size());
    }
}
