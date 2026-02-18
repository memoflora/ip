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

        return switch (command.toLowerCase()) {
        case "todo" -> parseTodo(input, firstSpaceIndex);
        case "deadline" -> parseDeadline(input, firstSpaceIndex);
        case "event" -> parseEvent(input, firstSpaceIndex);
        case "find" -> parseFind(input, firstSpaceIndex);
        case "delete" -> new DeleteCommand(getTaskIndex(input, firstSpaceIndex));
        case "mark" -> new MarkCommand(getTaskIndex(input, firstSpaceIndex));
        case "unmark" -> new UnmarkCommand(getTaskIndex(input, firstSpaceIndex));
        case "list" -> new ListCommand();
        case "bye" -> new ExitCommand();
        default -> throw new FloraException(getInvalidCommandMessage());
        };
    }

    /**
     * Parses a todo command from the user input.
     *
     * @param input           The raw user input string.
     * @param firstSpaceIndex The index of the first space in the input.
     * @return The parsed {@code AddTodoCommand}.
     * @throws FloraException If the task description is missing.
     */
    private static Command parseTodo(String input, int firstSpaceIndex) throws FloraException {
        if (firstSpaceIndex == -1 || firstSpaceIndex + 1 >= input.length()) {
            throw new FloraException("At least put something bro");
        }

        String taskDesc = input.substring(firstSpaceIndex + 1);
        assert !taskDesc.isBlank() : "Todo description must not be blank after parsing";
        return new AddTodoCommand(taskDesc);
    }

    /**
     * Parses a deadline command from the user input.
     * Extracts the task description and due date/time, supporting
     * natural language shortcuts (e.g., "today", "tomorrow", "next week")
     * as well as explicit date/time strings.
     *
     * @param input           The raw user input string.
     * @param firstSpaceIndex The index of the first space in the input.
     * @return The parsed {@code AddDeadlineCommand}.
     * @throws FloraException If the description or due date is missing or invalid.
     */
    private static Command parseDeadline(String input, int firstSpaceIndex) throws FloraException {
        if (firstSpaceIndex == -1 || firstSpaceIndex + 1 >= input.length()) {
            throw new FloraException("At least put something bro");
        }

        int byIndex = input.indexOf("/by");
        if (byIndex == -1 || byIndex + 4 >= input.length()) {
            throw new FloraException("At least set a due date bro");
        }

        String taskDesc = input.substring(firstSpaceIndex + 1, byIndex - 1);
        String taskDueStr = input.substring(byIndex + 4);
        assert !taskDesc.isBlank() : "Deadline description must not be blank";
        assert !taskDueStr.isBlank() : "Deadline due date string must not be blank";

        LocalDateTime taskDue = switch (taskDueStr.toLowerCase()) {
        case "today", "tonight" -> LocalDate.now().atTime(LocalTime.MAX);
        case "tomorrow" -> LocalDate.now().plusDays(1).atTime(LocalTime.MAX);
        case "next week" -> LocalDate.now().plusWeeks(1).atTime(LocalTime.MAX);
        case "next month" -> LocalDate.now().plusMonths(1).atTime(LocalTime.MAX);
        default -> parseDateTime(taskDueStr, "due date/time");
        };

        return new AddDeadlineCommand(taskDesc, taskDue);
    }

    /**
     * Parses an event command from the user input.
     * Extracts the task description, start date/time, and end date/time
     * by locating the {@code /from} and {@code /to} delimiters.
     *
     * @param input           The raw user input string.
     * @param firstSpaceIndex The index of the first space in the input.
     * @return The parsed {@code AddEventCommand}.
     * @throws FloraException If the description, start time, or end time is missing or invalid.
     */
    private static Command parseEvent(String input, int firstSpaceIndex) throws FloraException {
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
        assert !taskDesc.isBlank() : "Event description must not be blank";
        assert !taskStartStr.isBlank() : "Event start date string must not be blank";
        assert !taskEndStr.isBlank() : "Event end date string must not be blank";

        LocalDateTime taskStart = parseDateTime(taskStartStr, "start date/time");
        LocalDateTime taskEnd = parseDateTime(taskEndStr, "end date/time");

        return new AddEventCommand(taskDesc, taskStart, taskEnd);
    }

    /**
     * Parses a find command from the user input.
     *
     * @param input           The raw user input string.
     * @param firstSpaceIndex The index of the first space in the input.
     * @return The parsed {@code FindCommand}.
     * @throws FloraException If the keyword is missing.
     */
    private static Command parseFind(String input, int firstSpaceIndex) throws FloraException {
        if (firstSpaceIndex == -1 || firstSpaceIndex + 1 >= input.length()) {
            throw new FloraException("Put a keyword.");
        }

        String keyword = input.substring(firstSpaceIndex + 1);
        assert !keyword.isBlank() : "Find keyword must not be blank after parsing";
        return new FindCommand(keyword);
    }

    /**
     * Returns a randomly selected error message for invalid commands.
     *
     * @return A random error message string.
     */
    private static String getInvalidCommandMessage() {
        String[] strings = {"I guess bro", "Whatever that means"};
        Random rand = new Random(System.currentTimeMillis());
        int randomIndex = rand.nextInt(strings.length);
        return strings[randomIndex];
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

        assert taskIndex > 0 : "Task index must be positive, got: " + taskIndex;
        return taskIndex;
    }
}
