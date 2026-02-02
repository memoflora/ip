import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;

public class Flora {
    private static final String line = "    ____________________________________________________________";
    private static final String indent = "     ";
    private static final String greeting = indent + "Hi there! Flora here.\n" + indent + "Ask me anything!";
    private static final String farewell = indent + "Talk to you later—bye!";

    private final ArrayList<Task> tasks;
    private final Storage storage;

    public Flora() {
        storage = new Storage("./data/flora.txt");
        ArrayList<Task> loadedTasks;

        try {
            loadedTasks = storage.load();
        } catch (IOException e) {
            loadedTasks = new ArrayList<>();
        }

        this.tasks = loadedTasks;
    }

    public void saveTasks() {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            System.out.println(indent + "Error saving tasks: " + e.getMessage());
        }
    }

    public void listTasks() {
        System.out.println(indent + "Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(indent + (i + 1) + "." + tasks.get(i));
        }
    }

    public void addTodo(String taskDesc) throws FloraException {
        if (taskDesc == null) {
            throw new FloraException("At least put something bro");
        }

        Todo todo = new Todo(taskDesc);
        tasks.add(todo);
        saveTasks();

        System.out.println(indent + "Got it. I've added this task:");
        System.out.println(indent + "  " + todo);
        System.out.println(indent + "Now you have " + tasks.size() + " task" + (tasks.size() > 1 ? "s" : "") + " in the list.");
    }

    public void addDeadline(String taskDesc, String dueDate) throws FloraException {
        if (taskDesc == null) {
            throw new FloraException("At least put something bro");
        }

        if (dueDate == null) {
            throw new FloraException("At least set a due date bro");
        }

        Deadline deadline = new Deadline(taskDesc, dueDate);
        tasks.add(deadline);
        saveTasks();

        System.out.println(indent + "Got it. I've added this task:");
        System.out.println(indent + "  " + deadline);
        System.out.println(indent + "Now you have " + tasks.size() + " task" + (tasks.size() > 1 ? "s" : "") + " in the list.");
    }

    public void addEvent(String taskDesc, String startTime, String endTime) throws FloraException {
        if (taskDesc == null) {
            throw new FloraException("At least put something bro");
        }

        if (startTime == null) {
            throw new FloraException("At least set a start time bro");
        }

        if (endTime == null) {
            throw new FloraException("At least set an end time bro");
        }

        Event event = new Event(taskDesc, startTime, endTime);
        tasks.add(event);
        saveTasks();

        System.out.println(indent + "Got it. I've added this task:");
        System.out.println(indent + "  " + event);
        System.out.println(indent + "Now you have " + tasks.size() + " task" + (tasks.size() > 1 ? "s" : "") + " in the list.");
    }

    public void deleteTask(Integer index) throws FloraException {
        if (index == null) {
            throw new FloraException("At least put the index bro");
        }

        if (index < 1 || index > tasks.size()) {
            throw new FloraException("Bro's out of bounds");
        }

        System.out.println(indent + "Noted. I've removed this task:");
        System.out.println(indent + "  " + tasks.remove(index - 1));
        saveTasks();

        System.out.println(indent + "Now you have " + tasks.size() + " task" + (tasks.size() > 1 ? "s" : "") + " in the list.");
    }

    public void markTask(Integer index) throws FloraException {
        if (index == null) {
            throw new FloraException("At least put the index bro");
        }

        if (index < 1 || index > tasks.size()) {
            throw new FloraException("Bro's out of bounds");
        }

        Task task = tasks.get(index - 1);
        task.markAsDone();
        saveTasks();

        System.out.println(indent + "Nice! I've marked this task as done:");
        System.out.println(indent + "  " + task);
    }

    public void unmarkTask(Integer index) throws FloraException {
        if (index == null) {
            throw new FloraException("At least put the index bro");
        }

        if (index < 1 || index > tasks.size()) {
            throw new FloraException("Bro's out of bounds");
        }

        Task task = tasks.get(index - 1);
        task.markAsNotDone();
        saveTasks();

        System.out.println(indent + "OK, I've marked this task as not done yet:");
        System.out.println(indent + "  " + task);
    }

    public boolean handleInput(String input) throws FloraException {
        String command = input;
        int firstSpaceIndex = input.indexOf(" ");

        if (firstSpaceIndex != -1) {
            command = input.substring(0, firstSpaceIndex);
        }

        switch (command.toLowerCase()) {
            case "todo": {
                String taskDesc = null;
                if (firstSpaceIndex != -1 && firstSpaceIndex + 1 < input.length()) {
                    taskDesc = input.substring(firstSpaceIndex + 1);
                }

                try {
                    addTodo(taskDesc);
                } catch (FloraException e) {
                    System.out.println(indent + e.getMessage());
                }

                break;
            }

            case "deadline" : {
                String taskDesc = null;
                String dueDate = null;

                if (firstSpaceIndex != -1 && firstSpaceIndex + 1 < input.length()) {
                    taskDesc = input.substring(firstSpaceIndex + 1);
                    int byIndex = input.indexOf("/by");

                    if (byIndex != -1 && byIndex + 4 < input.length()) {
                        taskDesc = input.substring(firstSpaceIndex + 1, byIndex - 1);
                        dueDate = input.substring(byIndex + 4);
                    }
                }

                try {
                    addDeadline(taskDesc, dueDate);
                } catch (FloraException e) {
                    System.out.println(indent + e.getMessage());
                }

                break;
            }

            case "event" : {
                String taskDesc = null;
                String startTime = null;
                String endTime = null;

                if (firstSpaceIndex != -1) {
                    taskDesc = input.substring(firstSpaceIndex + 1);
                    int fromIndex = input.indexOf("/from");

                    if (fromIndex != -1 && fromIndex + 6 < input.length()) {
                        startTime = input.substring(fromIndex + 6);
                        int toIndex = input.indexOf("/to");

                        if (toIndex != -1 && toIndex + 4 < input.length()) {
                            taskDesc = input.substring(firstSpaceIndex + 1, fromIndex - 1);
                            startTime = input.substring(fromIndex + 6, toIndex - 1);
                            endTime = input.substring(toIndex + 4);
                        }
                    }
                }

                try {
                    addEvent(taskDesc, startTime, endTime);
                } catch (FloraException e) {
                    System.out.println(indent + e.getMessage());
                }

                break;
            }

            case "delete": {
                Integer taskIndex = null;
                if (firstSpaceIndex != -1 && firstSpaceIndex + 1 < input.length()) {
                    String taskIndexStr = input.substring(firstSpaceIndex + 1);
                    taskIndex = Integer.parseInt(taskIndexStr);
                }

                try {
                    deleteTask(taskIndex);
                } catch (FloraException e) {
                    System.out.println(indent + e.getMessage());
                }

                break;
            }

            case "mark": {
                Integer taskIndex = null;
                if (firstSpaceIndex != -1 && firstSpaceIndex + 1 < input.length()) {
                    String taskIndexStr = input.substring(firstSpaceIndex + 1);
                    taskIndex = Integer.parseInt(taskIndexStr);
                }

                try {
                    markTask(taskIndex);
                } catch (FloraException e) {
                    System.out.println(indent + e.getMessage());
                }

                break;
            }

            case "unmark": {
                Integer taskIndex = null;
                if (firstSpaceIndex != -1 && firstSpaceIndex + 1 < input.length()) {
                    String taskIndexStr = input.substring(firstSpaceIndex + 1);
                    taskIndex = Integer.parseInt(taskIndexStr);
                }

                try {
                    unmarkTask(taskIndex);
                } catch (FloraException e) {
                    System.out.println(indent + e.getMessage());
                }

                break;
            }

            case "list":
                listTasks();
                break;
            case "bye":
                System.out.println(farewell);
                System.out.println(line);
                return false;
            default:
                String[] strings = {"I guess bro", "Whatever that means"};
                Random rand = new Random(System.currentTimeMillis());
                int randomIndex = rand.nextInt(strings.length);
                String exceptionMessage = strings[randomIndex];

                throw new FloraException(exceptionMessage);
        }

        return true;
    }

    public static void main(String[] args) {
        Flora flora = new Flora();

        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println(line);
        System.out.println(greeting);

        boolean continueLoop = true;
        while (continueLoop) {
            System.out.println(line);
            System.out.println();

            input = sc.nextLine();
            System.out.println(line);

            try {
                continueLoop = flora.handleInput(input);
            } catch (FloraException e) {
                System.out.println(indent + e.getMessage());
            }
        }
    }
}
