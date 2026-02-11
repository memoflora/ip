package flora.ui;

import java.util.Scanner;

import flora.task.Task;
import flora.task.TaskList;

/**
 * Handles all user interface interactions, including reading input and displaying output.
 */
public class Ui {
    private static final String INDENT_STR = "     ";
    private static final String LINE = "____________________________________________________________";
    private static final String[] GREETINGS = {"Hi there! Flora here.", "Ask me anything!"};
    private static final String FAREWELL = "Talk to you later—bye!";
    private final Scanner sc = new Scanner(System.in);

    /**
     * Reads a line of input from the user.
     *
     * @return The user's input as a string.
     */
    public String readCommand() {
        return sc.nextLine();
    }

    public void indent(String str) {
        System.out.println(INDENT_STR + str);
    }

    /**
     * Displays a horizontal divider line.
     */
    public void showLine() {
        System.out.println(INDENT_STR.substring(0, INDENT_STR.length() - 1) + LINE);
    }

    /**
     * Displays a blank line.
     */
    public void showNewLine() {
        System.out.println();
    }

    /**
     * Displays the greeting message when the application starts.
     */
    public void showGreeting() {
        showLine();
        for (String greeting : GREETINGS) {
            indent(greeting);
        }
        showLine();
        showNewLine();
    }

    /**
     * Displays the farewell message when the application exits.
     */
    public void showFarewell() {
        indent(FAREWELL);
    }

    /**
     * Displays an error message.
     *
     * @param errorMsg The error message to display.
     */
    public void showError(String errorMsg) {
        indent(errorMsg);
    }

    /**
     * Displays all tasks in the given task list.
     *
     * @param tasks The task list to display.
     */
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

    /**
     * Displays all tasks that match a search query.
     *
     * @param matchingTasks The task list containing matching tasks.
     */
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

    /**
     * Displays a confirmation message after a task is added.
     *
     * @param task      The task that was added.
     * @param tasksSize The total number of tasks after adding.
     */
    public void showAddedTask(Task task, int tasksSize) {
        indent("Got it. I've added this task:");
        indent("  " + task);
        indent("Now you have " + tasksSize + " task" + (tasksSize > 1 ? "s" : "") + " in the list.");
    }

    /**
     * Displays a confirmation message after a task is deleted.
     *
     * @param task      The task that was deleted.
     * @param tasksSize The total number of tasks after deleting.
     */
    public void showDeletedTask(Task task, int tasksSize) {
        indent("Noted. I've removed this task:");
        indent("  " + task);
        indent("Now you have " + tasksSize + " task" + (tasksSize > 1 ? "s" : "") + " in the list.");
    }

    /**
     * Displays a confirmation message after a task is marked as done.
     *
     * @param task The task that was marked.
     */
    public void showMarkedTask(Task task) {
        indent("Nice! I've marked this task as done:");
        indent("  " + task);
    }

    /**
     * Displays a confirmation message after a task is marked as not done.
     *
     * @param task The task that was unmarked.
     */
    public void showUnmarkedTask(Task task) {
        indent("Ok, I've marked this task as not done yet:");
        indent("  " + task);
    }

    /**
     * Displays a message indicating the task is already marked as done.
     */
    public void showAlreadyMarked() {
        indent("That task is already marked bro");
    }

    /**
     * Displays a message indicating the task is already marked as not done.
     */
    public void showAlreadyUnmarked() {
        indent("That task is already unmarked bro");
    }
}
