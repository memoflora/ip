import java.util.List;
import java.util.Random;

public class Ui {
    private static final String indent = "     ";
    private static final String line = "    ____________________________________________________________";
    private static final String greetingMsg = indent + "Hi there! Flora here.\n" + indent + "Ask me anything!";
    private static final String farewellMsg = indent + "Talk to you later—bye!";

    public void showNewLine() {
        System.out.println();
    }

    public void showLine() {
        System.out.println(line);
    }

    public void showGreeting() {
        showLine();
        System.out.println(greetingMsg);
        showLine();
        showNewLine();
    }

    public void showFarewell() {
        System.out.println(farewellMsg);
    }

    public void showError(String errorMsg) {
        System.out.println(indent + errorMsg);
    }

    public void showLoadingError(String errorMsg) {
        System.out.println(indent + "Failed loading storage: " + errorMsg);
    }

    public void showSavingError(String errorMsg) {
        System.out.println(indent + "Failed saving to storage: " + errorMsg);
    }

    public void showListTasks(List<Task> tasks) {
        System.out.println(indent + "Here are the tasks in your list: ");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(indent + (i + 1) + "." + tasks.get(i));
        }
    }

    public void showAddTodo(Todo todo, int tasksSize) {
        System.out.println(indent + "Got it. I've added this task:");
        System.out.println(indent + "  " + todo);
        System.out.println(indent + "Now you have " + tasksSize + " task" + (tasksSize > 1 ? "s" : "") + " in the list.");
    }

    public void showAddDeadline(Deadline deadline, int tasksSize) {
        System.out.println(indent + "Got it. I've added this task:");
        System.out.println(indent + "  " + deadline);
        System.out.println(indent + "Now you have " + tasksSize + " task" + (tasksSize > 1 ? "s" : "") + " in the list.");
    }

    public void showAddEvent(Event event, int tasksSize) {
        System.out.println(indent + "Got it. I've added this task:");
        System.out.println(indent + "  " + event);
        System.out.println(indent + "Now you have " + tasksSize + " task" + (tasksSize > 1 ? "s" : "") + " in the list.");
    }

    public void showDeleteTask(Task task, int tasksSize) {
        System.out.println(indent + "Noted. I've removed this task:");
        System.out.println(indent + "  " + task);
        System.out.println(indent + "Now you have " + tasksSize + " task" + (tasksSize > 1 ? "s" : "") + " in the list.");
    }

    public void showMarkTask(Task task) {
        System.out.println(indent + "Nice! I've marked this task as done:");
        System.out.println(indent + "  " + task);
    }

    public void showUnmarkTask(Task task) {
        System.out.println(indent + "OK, I've marked this task as not done yet:");
        System.out.println(indent + "  " + task);
    }

    public String getInvalidCmdErrorMsg() {
        String[] strings = {"I guess bro", "Whatever that means"};
        Random rand = new Random(System.currentTimeMillis());
        int randomIndex = rand.nextInt(strings.length);
        return strings[randomIndex];
    }
}
