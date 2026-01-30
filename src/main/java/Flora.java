import java.util.Scanner;

public class Flora {
    private static final String line = "    ____________________________________________________________";
    private static final String indent = "     ";
    private static final String greeting = indent + "Hi there! Flora here.\n" + indent + "Ask me anything!";
    private static final String farewell = indent + "Talk to you later—bye!";

    private final String[] tasks;
    private int taskCount;

    public Flora() {
        this.tasks = new String[100];
        this.taskCount = 0;
    }

    public void listTasks() {
        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + ". " + tasks[i]);
        }
    }

    public void addTask(String task) {
        tasks[taskCount++] = task;
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
            System.out.println(line);

            switch (input.toLowerCase()) {
                case "list":
                    flora.listTasks();
                    break;
                case "bye":
                    System.out.println(farewell);
                    exit = true;
                    break;
                default:
                    flora.addTask(input);
                    System.out.println("added: " + input);
            }
        }

        System.out.println(line);
    }
}
