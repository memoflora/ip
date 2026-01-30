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
            System.out.println(indent + (i + 1) + ". " + tasks[i].toString());
        }
    }

    public void addTask(String input) {
        tasks[taskCount++] = new Task(input);
        System.out.println(indent + "added: " + input);
    }

    public void markTask(int index) {
        Task task = tasks[index - 1];
        task.markAsDone();

        System.out.println(indent + "Nice! I've marked this task as done:");
        System.out.println(indent + "  " + task.toString());
    }

    public void unmarkTask(int index) {
        Task task = tasks[index - 1];
        task.markAsNotDone();

        System.out.println(indent + "OK, I've marked this task as not done yet:");
        System.out.println(indent + "  " + task.toString());
    }

    private static class Task {
        protected String description;
        protected boolean isDone;

        public Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        public String getStatusIcon() {
            return (isDone ? "X" : " ");
        }

        public void markAsDone() {
            isDone = true;
        }

        public void markAsNotDone() {
            isDone = false;
        }

        @Override
        public String toString() {
            return "[" + getStatusIcon() + "] " + description;
        }
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
            String[] parts = input.split(" ");
            String command = parts[0];

            System.out.println(line);

            switch (command.toLowerCase()) {
                case "mark": {
                    int taskIndex = Integer.parseInt(parts[1]);
                    flora.markTask(taskIndex);
                    break;
                }

                case "unmark": {
                    int taskIndex = Integer.parseInt(parts[1]);
                    flora.unmarkTask(taskIndex);
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
                    flora.addTask(command);
            }
        }

        System.out.println(line);
    }
}
