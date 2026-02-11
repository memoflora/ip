package flora.ui;

import java.util.Scanner;

import flora.task.Task;
import flora.task.TaskList;

public class Ui {
    private static final String INDENT_STR = "     ";
    private static final String LINE = "____________________________________________________________";
    private static final String[] GREETINGS = {"Hi there! Flora here.", "Ask me anything!"};
    private static final String FAREWELL = "Talk to you later—bye!";
    private final Scanner sc = new Scanner(System.in);

    public String readCommand() {
        return sc.nextLine();
    }

    public void indent(String str) {
        System.out.println(INDENT_STR + str);
    }

    public void showLine() {
        System.out.println(INDENT_STR.substring(0, INDENT_STR.length() - 1) + LINE);
    }

    public void showNewLine() {
        System.out.println();
    }

    public void showGreeting() {
        showLine();
        for (String greeting : GREETINGS) {
            indent(greeting);
        }
        showLine();
        showNewLine();
    }

    public void showFarewell() {
        indent(FAREWELL);
    }

    public void showError(String errorMsg) {
        indent(errorMsg);
    }

    public void showTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            indent("Your list is empty.");
            return;
        }

        indent("Here are the tasks in your list: ");
        for (int i = 1; i <= tasks.size(); i++) {
            indent(i + "." + tasks.get(i));
        }
    }

    public void showMatchingTasks(TaskList matchingTasks) {
        if (matchingTasks.size() == 0) {
            indent("No matching tasks.");
            return;
        }

        indent("Here are the matching tasks in your list: ");
        for (int i = 1; i <= matchingTasks.size(); i++) {
            indent(i + "." + matchingTasks.get(i));
        }
    }

    public void showAddedTask(Task task, int tasksSize) {
        indent("Got it. I've added this task:");
        indent("  " + task);
        indent("Now you have " + tasksSize + " task" + (tasksSize > 1 ? "s" : "") + " in the list.");
    }

    public void showDeletedTask(Task task, int tasksSize) {
        indent("Noted. I've removed this task:");
        indent("  " + task);
        indent("Now you have " + tasksSize + " task" + (tasksSize > 1 ? "s" : "") + " in the list.");
    }

    public void showMarkedTask(Task task) {
        indent("Nice! I've marked this task as done:");
        indent("  " + task);
    }

    public void showUnmarkedTask(Task task) {
        indent("Ok, I've marked this task as not done yet:");
        indent("  " + task);
    }

    public void showAlreadyMarked() {
        indent("That task is already marked bro");
    }

    public void showAlreadyUnmarked() {
        indent("That task is already unmarked bro");
    }
}
