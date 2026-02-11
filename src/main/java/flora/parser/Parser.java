package flora.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;

import flora.command.AddDeadlineCommand;
import flora.command.AddEventCommand;
import flora.command.AddTodoCommand;
import flora.command.Command;
import flora.command.DeleteCommand;
import flora.command.ExitCommand;
import flora.command.FindCommand;
import flora.command.ListCommand;
import flora.command.MarkCommand;
import flora.command.UnmarkCommand;
import flora.exception.FloraException;

/**
 * Parses user input into executable commands.
 */
public class Parser {
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("d/M/yyyy[ H:mm]");

    /**
     * Parses the given user input string and returns the corresponding command.
     *
     * @param input The raw user input string.
     * @return The command corresponding to the user input.
     * @throws FloraException If the input is invalid or cannot be parsed.
     */
    public static Command parse(String input) throws FloraException {
        String command = input;
        int firstSpaceIndex = input.indexOf(" ");

        if (firstSpaceIndex != -1) {
            command = input.substring(0, firstSpaceIndex);
        }

        switch (command.toLowerCase()) {
        case "todo": {
            String taskDesc = getArguments(input, firstSpaceIndex, "At least put something bro");
            return new AddTodoCommand(taskDesc);
        }

        case "deadline": {
            getArguments(input, firstSpaceIndex, "At least put something bro");

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
                taskDue = parseDateTime(taskDueStr, "due date/time");
                break;
            }

            return new AddDeadlineCommand(taskDesc, taskDue);
        }

        case "event": {
            getArguments(input, firstSpaceIndex, "At least put something bro");

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

            LocalDateTime taskStart = parseDateTime(taskStartStr, "start date/time");
            LocalDateTime taskEnd = parseDateTime(taskEndStr, "end date/time");

            return new AddEventCommand(taskDesc, taskStart, taskEnd);
        }

        case "find": {
            String keyword = getArguments(input, firstSpaceIndex, "Put a keyword.");
            return new FindCommand(keyword);
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

    /**
     * Validates that the input has arguments after the command and returns them.
     *
     * @param input           The raw user input string.
     * @param firstSpaceIndex The index of the first space in the input.
     * @param errorMsg        The error message to throw if arguments are missing.
     * @return The arguments substring after the command.
     * @throws FloraException If no arguments are provided.
     */
    private static String getArguments(String input, int firstSpaceIndex, String errorMsg) throws FloraException {
        if (firstSpaceIndex == -1 || firstSpaceIndex + 1 >= input.length()) {
            throw new FloraException(errorMsg);
        }
        return input.substring(firstSpaceIndex + 1);
    }

    /**
     * Parses a date/time string into a LocalDateTime.
     *
     * @param dateStr   The date/time string to parse.
     * @param fieldName The name of the field, used in error messages.
     * @return The parsed LocalDateTime.
     * @throws FloraException If the string cannot be parsed.
     */
    private static LocalDateTime parseDateTime(String dateStr, String fieldName) throws FloraException {
        try {
            return LocalDateTime.parse(dateStr, DATE_TIME_FMT);
        } catch (DateTimeParseException e) {
            throw new FloraException("Invalid " + fieldName + ": " + dateStr);
        }
    }

    /**
     * Extracts and validates the task index from the user input.
     *
     * @param input           The raw user input string.
     * @param firstSpaceIndex The index of the first space in the input.
     * @return The parsed task index.
     * @throws FloraException If the index is missing or not a valid integer.
     */
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
