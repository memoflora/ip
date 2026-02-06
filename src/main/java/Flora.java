import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Flora {
    private final Ui ui;
    private final Storage storage;
    private final ArrayList<Task> tasks;

    public Flora() {
        ui = new Ui();
        storage = new Storage("./data/flora.txt");
        ArrayList<Task> loadedTasks;

        try {
            loadedTasks = storage.load();
        } catch (IOException e) {
            ui.showLoadingError(e.getMessage());
            loadedTasks = new ArrayList<>();
        }

        this.tasks = loadedTasks;
    }

    public void saveTasks() {
        try {
            storage.save(tasks);
        } catch (IOException e) {
            ui.showSavingError(e.getMessage());
        }
    }

    public void listTasks() {
        ui.showListTasks(tasks);
    }

    public void addTodo(String taskDesc) {
        Todo todo = new Todo(taskDesc);
        tasks.add(todo);
        saveTasks();
        ui.showAddTodo(todo, tasks.size());
    }

    public void addDeadline(String taskDesc, LocalDateTime dueDate) {
        Deadline deadline = new Deadline(taskDesc, dueDate);
        tasks.add(deadline);
        saveTasks();
        ui.showAddDeadline(deadline, tasks.size());
    }

    public void addEvent(String taskDesc, LocalDateTime startTime, LocalDateTime endTime) {
        Event event = new Event(taskDesc, startTime, endTime);
        tasks.add(event);
        saveTasks();
        ui.showAddEvent(event, tasks.size());
    }

    public void deleteTask(int index) throws FloraException {
        if (index < 1 || index > tasks.size()) {
            throw new FloraException("Bro's out of bounds");
        }

        Task task = tasks.remove(index - 1);
        saveTasks();
        ui.showDeleteTask(task, tasks.size());
    }

    public void markTask(int index) throws FloraException {
        if (index < 1 || index > tasks.size()) {
            throw new FloraException("Bro's out of bounds");
        }

        Task task = tasks.get(index - 1);
        if (task.getIsDone()) {
            throw new FloraException("Please stop");
        }

        task.markAsDone();
        saveTasks();
        ui.showMarkTask(task);
    }

    public void unmarkTask(int index) throws FloraException {
        if (index < 1 || index > tasks.size()) {
            throw new FloraException("Bro's out of bounds");
        }

        Task task = tasks.get(index - 1);
        if (!task.getIsDone()) {
            throw new FloraException("Please stop");
        }

        task.markAsNotDone();
        saveTasks();
        ui.showUnmarkTask(task);
    }

    public boolean parseInput(String input) throws FloraException, NumberFormatException {
        String command = input;
        int firstSpaceIndex = input.indexOf(" ");

        if (firstSpaceIndex != -1) {
            command = input.substring(0, firstSpaceIndex);
        }

        switch (command.toLowerCase()) {
            case "todo": {
                if (firstSpaceIndex == -1 || firstSpaceIndex + 1 >= input.length()) {
                    throw new FloraException("At least put something bro");
                }

                String taskDesc = input.substring(firstSpaceIndex + 1);
                addTodo(taskDesc);
                break;
            }

            case "deadline" : {
                if (firstSpaceIndex == -1 || firstSpaceIndex + 1 >= input.length()) {
                    throw new FloraException("At least put something bro");
                }

                int byIndex = input.indexOf("/by");
                if (byIndex == -1 || byIndex + 4 >= input.length()) {
                    throw new FloraException("At least set a due date bro");
                }

                String taskDesc = input.substring(firstSpaceIndex + 1, byIndex - 1);
                String dueStr = input.substring(byIndex + 4);

                LocalDateTime due;

                switch (dueStr.toLowerCase()) {
                    case "today", "tonight": {
                        due = LocalDate.now().atTime(LocalTime.MAX);
                        break;
                    }

                    case "tomorrow": {
                        due = LocalDate.now().plusDays(1).atTime(LocalTime.MAX);
                        break;
                    }

                    case "next week": {
                        due = LocalDate.now().plusWeeks(1).atTime(LocalTime.MAX);
                        break;
                    }

                    case "next month": {
                        due = LocalDate.now().plusMonths(1).atTime(LocalTime.MAX);
                        break;
                    }

                    default: {
                        DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("d/M/yyyy[ H:mm]");

                        try {
                            due = LocalDateTime.parse(dueStr, dateTimeFmt);
                        } catch (DateTimeParseException e) {
                            throw new FloraException("Invalid due date/time: " + dueStr);
                        }

                        break;
                    }
                }

                addDeadline(taskDesc, due);
                break;
            }

            case "event" : {
                if (firstSpaceIndex == -1 || firstSpaceIndex + 1 >= input.length()) {
                    throw new FloraException("At least put something bro");
                }

                int fromIndex = input.indexOf("/from");
                if (fromIndex == -1 || fromIndex + 6 >= input.length()) {
                    throw new FloraException("At least set a start time bro");
                }

                int toIndex = input.indexOf("/to");
                if (toIndex == -1 || toIndex + 4 >= input.length()) {
                    throw new FloraException("At least set an end time bro");
                }

                String taskDesc = input.substring(firstSpaceIndex + 1, fromIndex - 1);
                String startStr = input.substring(fromIndex + 6, toIndex - 1);
                String endStr = input.substring(toIndex + 4);

                LocalDateTime start;
                LocalDateTime end;
                DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("d/M/yyyy[ H:mm]");

                try {
                    start = LocalDateTime.parse(startStr, dateTimeFmt);
                } catch (DateTimeParseException e) {
                    throw new FloraException("Invalid start date/time: " + startStr + " " + endStr);
                }

                try {
                    end = LocalDateTime.parse(endStr, dateTimeFmt);
                } catch (DateTimeParseException e) {
                    throw new FloraException("Invalid end date/time: " + startStr + " " + endStr);
                }

                addEvent(taskDesc, start, end);
                break;
            }

            case "delete": {
                if (firstSpaceIndex == -1 || firstSpaceIndex + 1 >= input.length()) {
                    throw new FloraException("At least put an index bro");
                }

                String taskIndexStr = input.substring(firstSpaceIndex + 1);
                int taskIndex = Integer.parseInt(taskIndexStr);
                deleteTask(taskIndex);
                break;
            }

            case "mark": {
                if (firstSpaceIndex == -1 || firstSpaceIndex + 1 >= input.length()) {
                    throw new FloraException("At least put an index bro");
                }

                String taskIndexStr = input.substring(firstSpaceIndex + 1);
                int taskIndex = Integer.parseInt(taskIndexStr);
                markTask(taskIndex);
                break;
            }

            case "unmark": {
                if (firstSpaceIndex == -1 || firstSpaceIndex + 1 >= input.length()) {
                    throw new FloraException("At least put an index bro");
                }

                String taskIndexStr = input.substring(firstSpaceIndex + 1);
                int taskIndex = Integer.parseInt(taskIndexStr);
                unmarkTask(taskIndex);
                break;
            }

            case "list":
                listTasks();
                break;
            case "bye":
                ui.showFarewell();
                return false;
            default:
                throw new FloraException(ui.getInvalidCmdErrorMsg());
        }

        return true;
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        String input;

        ui.showGreeting();

        boolean continueLoop = true;
        while (continueLoop) {
            input = sc.nextLine();
            ui.showLine();

            try {
                continueLoop = parseInput(input);
            } catch (FloraException | NumberFormatException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
                ui.showNewLine();
            }
        }
    }

    public static void main(String[] args) {
        new Flora().run();
    }
}
