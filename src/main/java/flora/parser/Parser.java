package flora.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Random;

import flora.command.AddDeadlineCommand;
import flora.command.AddEventCommand;
import flora.command.AddTodoCommand;
import flora.command.Command;
import flora.command.DeleteCommand;
import flora.command.EditCommand;
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
    private static final DateTimeFormatter DATE_TIME_FMT =
            DateTimeFormatter.ofPattern("d/M/uuuu H:mm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_ONLY_FMT =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

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
        case "edit" -> parseEdit(input, firstSpaceIndex);
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

        LocalDateTime taskDue = parseDueDateTime(taskDueStr);

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

        LocalDateTime taskStart = parseDateTime(taskStartStr, "start date/time", LocalTime.MIDNIGHT);
        LocalDateTime taskEnd = parseDateTime(taskEndStr, "end date/time", LocalTime.MAX);

        if (!taskStart.isBefore(taskEnd)) {
            throw new FloraException("Start time must be before end time.");
        }

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
     * Parses an edit command from the user input.
     * Syntax: {@code edit <index> [/desc <newDesc>] [/by <newDue>] [/from <newStart>] [/to <newEnd>]}
     * At least one field must be provided. Fields irrelevant to the task type are rejected at execution.
     *
     * @param input           The raw user input string.
     * @param firstSpaceIndex The index of the first space in the input.
     * @return The parsed {@code EditCommand}.
     * @throws FloraException If the index is missing/invalid or no fields are provided.
     */
    private static Command parseEdit(String input, int firstSpaceIndex) throws FloraException {
        if (firstSpaceIndex == -1 || firstSpaceIndex + 1 >= input.length()) {
            throw new FloraException("At least put an index bro");
        }

        String afterCommand = input.substring(firstSpaceIndex + 1);
        int nextSpaceIndex = afterCommand.indexOf(" ");

        String indexStr = nextSpaceIndex == -1 ? afterCommand : afterCommand.substring(0, nextSpaceIndex);
        String fields = nextSpaceIndex == -1 ? "" : afterCommand.substring(nextSpaceIndex + 1);

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(indexStr);
        } catch (NumberFormatException e) {
            throw new FloraException("Invalid task index: " + indexStr);
        }

        if (taskIndex <= 0) {
            throw new FloraException("Invalid task index: " + taskIndex);
        }

        String newDesc = extractField(fields, "/desc");
        String byStr = extractField(fields, "/by");
        String fromStr = extractField(fields, "/from");
        String toStr = extractField(fields, "/to");

        if (newDesc == null && byStr == null && fromStr == null && toStr == null) {
            throw new FloraException("At least change something bro. "
                    + "Use /desc, /by, /from, or /to.");
        }

        LocalDateTime newDue = byStr != null ? parseDueDateTime(byStr) : null;
        LocalDateTime newStart = fromStr != null ? parseDateTime(fromStr, "start date/time", LocalTime.MIDNIGHT) : null;
        LocalDateTime newEnd = toStr != null ? parseDateTime(toStr, "end date/time", LocalTime.MAX) : null;

        if (newStart != null && newEnd != null && !newStart.isBefore(newEnd)) {
            throw new FloraException("Start time must be before end time.");
        }

        return new EditCommand(taskIndex, newDesc, newDue, newStart, newEnd);
    }

    /**
     * Extracts the value following a field marker (e.g., "/desc") within a string.
     * The value spans from after the marker to the start of the next marker or end of string.
     *
     * @param input  The string to search within (the portion after the task index).
     * @param marker The field marker to locate (e.g., "/desc", "/by").
     * @return The trimmed value after the marker, or {@code null} if the marker is absent
     *         or its value is blank.
     */
    private static String extractField(String input, String marker) {
        int markerIndex = input.indexOf(marker);
        if (markerIndex == -1) {
            return null;
        }

        int valueStart = markerIndex + marker.length();
        if (valueStart >= input.length()) {
            return null;
        }

        String[] allMarkers = {"/desc", "/by", "/from", "/to"};
        int valueEnd = input.length();
        for (String m : allMarkers) {
            if (m.equals(marker)) {
                continue;
            }
            int mIndex = input.indexOf(m, valueStart);
            if (mIndex != -1 && mIndex < valueEnd) {
                valueEnd = mIndex;
            }
        }

        String value = input.substring(valueStart, valueEnd).trim();
        return value.isBlank() ? null : value;
    }

    /**
     * Returns a randomly selected error message for invalid commands.
     *
     * @return A random error message string.
     */
    private static String getInvalidCommandMessage() {
        String[] errorMessages = {"I guess bro", "Whatever that means"};
        Random random = new Random(System.currentTimeMillis());
        int randomIndex = random.nextInt(errorMessages.length);
        return errorMessages[randomIndex];
    }

    /**
     * Parses a due date string, supporting natural language shortcuts
     * ("today", "tonight", "tomorrow", "next week", "next month") as well as
     * explicit date/time strings.
     *
     * @param dateStr The due date string to parse.
     * @return The parsed LocalDateTime.
     * @throws FloraException If the string is not a recognised shortcut and cannot be parsed.
     */
    private static LocalDateTime parseDueDateTime(String dateStr) throws FloraException {
        return switch (dateStr.toLowerCase()) {
        case "today", "tonight" -> LocalDate.now().atTime(LocalTime.MAX);
        case "tomorrow" -> LocalDate.now().plusDays(1).atTime(LocalTime.MAX);
        case "next week" -> LocalDate.now().plusWeeks(1).atTime(LocalTime.MAX);
        case "next month" -> LocalDate.now().plusMonths(1).atTime(LocalTime.MAX);
        default -> parseDateTime(dateStr, "due date/time", LocalTime.MAX);
        };
    }

    /**
     * Parses a date/time string into a LocalDateTime.
     * If no time component is provided, {@code defaultTime} is used.
     *
     * @param dateStr     The date/time string to parse.
     * @param fieldName   The name of the field, used in error messages.
     * @param defaultTime The time to use when only a date is given.
     * @return The parsed LocalDateTime.
     * @throws FloraException If the string cannot be parsed.
     */
    private static LocalDateTime parseDateTime(String dateStr, String fieldName,
            LocalTime defaultTime) throws FloraException {
        try {
            return LocalDateTime.parse(dateStr, DATE_TIME_FMT);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(dateStr, DATE_ONLY_FMT).atTime(defaultTime);
            } catch (DateTimeParseException e2) {
                throw new FloraException("Invalid " + fieldName + ": " + dateStr);
            }
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
