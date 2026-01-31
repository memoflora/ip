import java.util.Scanner;

public class Flora {
    private static final String line = "    ____________________________________________________________";
    private static final String indent = "     ";
    private static final String greeting = indent + "Hi there! Flora here.\n" + indent + "Ask me anything!";
    private static final String farewell = indent + "Talk to you later—bye!";

    private final Task[] tasks;
    private int taskCount;

    public Flora() {
        this.tasks = new Task[100];
        this.taskCount = 0;
    }

    public void listTasks() {
        System.out.println(indent + "Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println(indent + (i + 1) + "." + tasks[i]);
        }
    }

    public void addTodo(String taskDesc) {
        System.out.println(indent + "Got it. I've added this task:");

        Todo todo = new Todo(taskDesc);
        tasks[taskCount++] = todo;

        System.out.println(indent + "  " + todo);
        System.out.println(indent + "Now you have " + taskCount + " task" + (taskCount > 1 ? "s" : "") + " in the list.");
    }

    public void addDeadline(String taskDesc, String dueDate) {
        System.out.println(indent + "Got it. I've added this task:");

        Deadline deadline = new Deadline(taskDesc, dueDate);
        tasks[taskCount++] = deadline;

        System.out.println(indent + "  " + deadline);
        System.out.println(indent + "Now you have " + taskCount + " task" + (taskCount > 1 ? "s" : "") + " in the list.");

    }

    public void addEvent(String taskDesc, String startTime, String endTime) {
        System.out.println(indent + "Got it. I've added this task:");

        Event event = new Event(taskDesc, startTime, endTime);
        tasks[taskCount++] = event;

        System.out.println(indent + "  " + event);
        System.out.println(indent + "Now you have " + taskCount + " task" + (taskCount > 1 ? "s" : "") + " in the list.");
    }

    public void markTask(int index) {
        Task task = tasks[index - 1];
        task.markAsDone();

        System.out.println(indent + "Nice! I've marked this task as done:");
        System.out.println(indent + "  " + task);
    }

    public void unmarkTask(int index) {
        Task task = tasks[index - 1];
        task.markAsNotDone();

        System.out.println(indent + "OK, I've marked this task as not done yet:");
        System.out.println(indent + "  " + task);
    }

    public static void main(String[] args) {
        Flora flora = new Flora();

        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println(line);
        System.out.println(greeting);

        boolean exit = false;
        while (!exit) {
            System.out.println(line);
            System.out.println();

            input = sc.nextLine();

            int firstSpaceIndex = input.indexOf(" ");
            String command;

            if (firstSpaceIndex != -1) {
                command = input.substring(0, firstSpaceIndex);
            } else {
                command = input;
            }

            System.out.println(line);

            switch (command.toLowerCase()) {
                case "todo": {
                    String taskDesc = input.substring(firstSpaceIndex + 1);
                    flora.addTodo(taskDesc);
                    break;
                }

                case "deadline" : {
                    int byIndex = input.indexOf("/by");

                    String taskDesc = input.substring(firstSpaceIndex + 1, byIndex - 1);
                    String dueDate = input.substring(byIndex + 4);

                    flora.addDeadline(taskDesc, dueDate);
                    break;
                }

                case "event" : {
                    int fromIndex = input.indexOf("/from");
                    int toIndex = input.indexOf("/to");

                    String taskDesc = input.substring(firstSpaceIndex + 1, fromIndex - 1);
                    String startTime = input.substring(fromIndex + 6, toIndex - 1);
                    String endTime = input.substring(toIndex + 4);

                    flora.addEvent(taskDesc, startTime, endTime);
                    break;
                }

                case "mark": {
                    String taskIndex = input.substring(firstSpaceIndex + 1);
                    flora.markTask(Integer.parseInt(taskIndex));
                    break;
                }

                case "unmark": {
                    String taskIndex = input.substring(firstSpaceIndex + 1);
                    flora.unmarkTask(Integer.parseInt(taskIndex));
                    break;
                }

                case "list":
                    flora.listTasks();
                    break;
                case "bye":
                    System.out.println(farewell);
                    exit = true;
                    break;
                default:
                    System.out.println(input);
            }
        }

        System.out.println(line);
    }
}
