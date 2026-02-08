public class AddTodoCommand extends Command {
    private final String taskDesc;

    public AddTodoCommand(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws FloraException {
        Todo todo = new Todo(taskDesc);
        tasks.add(todo);
        storage.save(tasks);
        ui.showAddedTask(todo, tasks.size());
    }
}
