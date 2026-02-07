import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;

public class Parser {
    private static final DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("d/M/yyyy[ H:mm]");

    public static Command parse(String input) throws FloraException {
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

                return new AddTodoCommand(taskDesc);
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
                String taskDueStr = input.substring(byIndex + 4);

                LocalDateTime taskDue;

                switch (taskDueStr.toLowerCase()) {
                    case "today", "tonight":
                        taskDue = LocalDate.now().atTime(LocalTime.MAX);
                        break;
                    case "tomorrow":
                        taskDue = LocalDate.now().plusDays(1).atTime(LocalTime.MAX);
                        break;
                    case "next week":
                        taskDue = LocalDate.now().plusWeeks(1).atTime(LocalTime.MAX);
                        break;
                    case "next month":
                        taskDue = LocalDate.now().plusMonths(1).atTime(LocalTime.MAX);
                        break;
                    default:
                        try {
                            taskDue = LocalDateTime.parse(taskDueStr, dateTimeFmt);
                        } catch (DateTimeParseException e) {
                            throw new FloraException("Invalid due date/time: " + taskDueStr);
                        }

                        break;
                }

                return new AddDeadlineCommand(taskDesc, taskDue);
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
                String taskStartStr = input.substring(fromIndex + 6, toIndex - 1);
                String taskEndStr = input.substring(toIndex + 4);

                LocalDateTime taskStart;
                LocalDateTime taskEnd;

                try {
                    taskStart = LocalDateTime.parse(taskStartStr, dateTimeFmt);
                } catch (DateTimeParseException e) {
                    throw new FloraException("Invalid start date/time: " + taskStartStr);
                }

                try {
                    taskEnd = LocalDateTime.parse(taskEndStr, dateTimeFmt);
                } catch (DateTimeParseException e) {
                    throw new FloraException("Invalid end date/time: " + taskEndStr);
                }

                return new AddEventCommand(taskDesc, taskStart, taskEnd);
            }

            case "delete": {
                int taskIndex = getTaskIndex(input, firstSpaceIndex);
                return new DeleteCommand(taskIndex);
            }

            case "mark": {
                int taskIndex = getTaskIndex(input, firstSpaceIndex);
                return new MarkCommand(taskIndex);
            }

            case "unmark": {
                int taskIndex = getTaskIndex(input, firstSpaceIndex);
                return new UnmarkCommand(taskIndex);
            }

            case "list":
                return new ListCommand();
            case "bye":
                return new ExitCommand();
            default:
                String[] strings = {"I guess bro", "Whatever that means"};
                Random rand = new Random(System.currentTimeMillis());
                int randomIndex = rand.nextInt(strings.length);
                throw new FloraException(strings[randomIndex]);
        }
    }

    private static int getTaskIndex(String input, int firstSpaceIndex) throws FloraException {
        if (firstSpaceIndex == -1 || firstSpaceIndex + 1 >= input.length()) {
            throw new FloraException("At least put an index bro");
        }

        String taskIndexStr = input.substring(firstSpaceIndex + 1);
        int taskIndex;

        try {
            taskIndex = Integer.parseInt(taskIndexStr);
        } catch (NumberFormatException e) {
            throw new FloraException("Invalid task index: " + e.getMessage());
        }

        return taskIndex;
    }
}
