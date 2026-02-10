package flora;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

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
import flora.parser.Parser;
import flora.storage.Storage;
import flora.task.Deadline;
import flora.task.Event;
import flora.task.TaskList;
import flora.task.Todo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FloraTest {

    // ==================== Todo Tests ====================

    @Test
    public void todo_toString_notDone() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void todo_toString_done() {
        Todo todo = new Todo("read book");
        todo.mark();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void todo_toFileString_notDone() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toFileString());
    }

    @Test
    public void todo_toFileString_done() {
        Todo todo = new Todo("read book");
        todo.mark();
        assertEquals("T | 1 | read book", todo.toFileString());
    }

    @Test
    public void todo_markAndUnmark_togglesDoneStatus() {
        Todo todo = new Todo("read book");
        assertFalse(todo.isDone());
        todo.mark();
        assertTrue(todo.isDone());
        todo.unmark();
        assertFalse(todo.isDone());
    }

    // ==================== Deadline Tests ====================

    @Test
    public void deadline_toString_withTime() {
        LocalDateTime due = LocalDateTime.of(2024, 12, 1, 18, 0);
        Deadline deadline = new Deadline("submit report", due);
        assertEquals("[D][ ] submit report (by: 1 Dec 2024 at 18:00)", deadline.toString());
    }

    @Test
    public void deadline_toString_withoutTime() {
        LocalDateTime due = LocalDateTime.of(2024, 12, 1, 0, 0);
        Deadline deadline = new Deadline("submit report", due);
        assertEquals("[D][ ] submit report (by: 1 Dec 2024)", deadline.toString());
    }

    @Test
    public void deadline_toFileString_withTime() {
        LocalDateTime due = LocalDateTime.of(2024, 12, 1, 18, 0);
        Deadline deadline = new Deadline("submit report", due);
        assertEquals("D | 0 | submit report | 01/12/2024 18:00", deadline.toFileString());
    }

    @Test
    public void deadline_toFileString_withoutTime() {
        LocalDateTime due = LocalDateTime.of(2024, 12, 1, 0, 0);
        Deadline deadline = new Deadline("submit report", due);
        assertEquals("D | 0 | submit report | 01/12/2024", deadline.toFileString());
    }

    @Test
    public void deadline_markAndToString_showsX() {
        LocalDateTime due = LocalDateTime.of(2024, 12, 1, 18, 0);
        Deadline deadline = new Deadline("submit report", due);
        deadline.mark();
        assertEquals("[D][X] submit report (by: 1 Dec 2024 at 18:00)", deadline.toString());
    }

    // ==================== Event Tests ====================

    @Test
    public void event_toString_withTime() {
        LocalDateTime start = LocalDateTime.of(2024, 8, 6, 14, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 6, 16, 0);
        Event event = new Event("project meeting", start, end);
        assertEquals("[E][ ] project meeting (from: 6 Aug 2024 at 14:00 to: 6 Aug 2024 at 16:00)",
                event.toString());
    }

    @Test
    public void event_toString_withoutTime() {
        LocalDateTime start = LocalDateTime.of(2024, 8, 6, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 8, 0, 0);
        Event event = new Event("book fair", start, end);
        assertEquals("[E][ ] book fair (from: 6 Aug 2024 to: 8 Aug 2024)", event.toString());
    }

    @Test
    public void event_toString_oneHasTimeMidnight() {
        // If only one of start/end is midnight, time should still be shown for both
        LocalDateTime start = LocalDateTime.of(2024, 8, 6, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 6, 16, 0);
        Event event = new Event("seminar", start, end);
        assertEquals("[E][ ] seminar (from: 6 Aug 2024 at 00:00 to: 6 Aug 2024 at 16:00)",
                event.toString());
    }

    @Test
    public void event_toFileString_withTime() {
        LocalDateTime start = LocalDateTime.of(2024, 8, 6, 14, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 6, 16, 0);
        Event event = new Event("project meeting", start, end);
        assertEquals("E | 0 | project meeting | 06/08/2024 14:00 | 06/08/2024 16:00",
                event.toFileString());
    }

    @Test
    public void event_toFileString_withoutTime() {
        LocalDateTime start = LocalDateTime.of(2024, 8, 6, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 8, 0, 0);
        Event event = new Event("book fair", start, end);
        assertEquals("E | 0 | book fair | 06/08/2024 | 08/08/2024", event.toFileString());
    }

    // ==================== TaskList Tests ====================

    @Test
    public void taskList_addAndSize() {
        TaskList list = new TaskList();
        assertEquals(0, list.size());
        list.add(new Todo("task 1"));
        assertEquals(1, list.size());
        list.add(new Todo("task 2"));
        assertEquals(2, list.size());
    }

    @Test
    public void taskList_get_usesOneBasedIndex() {
        TaskList list = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        list.add(first);
        list.add(second);
        assertEquals(first, list.get(1));
        assertEquals(second, list.get(2));
    }

    @Test
    public void taskList_remove_usesOneBasedIndex() {
        TaskList list = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        list.add(first);
        list.add(second);
        list.remove(1);
        assertEquals(1, list.size());
        assertEquals(second, list.get(1));
    }

    @Test
    public void taskList_constructorWithList() {
        List<flora.task.Task> tasks = new ArrayList<>();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        TaskList list = new TaskList(tasks);
        assertEquals(2, list.size());
    }

    @Test
    public void taskList_find_caseInsensitive() {
        TaskList list = new TaskList();
        list.add(new Todo("Read Book"));
        list.add(new Todo("return book"));
        list.add(new Todo("buy groceries"));
        TaskList results = list.find("book");
        assertEquals(2, results.size());
    }

    @Test
    public void taskList_find_noMatches() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        TaskList results = list.find("xyz");
        assertEquals(0, results.size());
    }

    // ==================== Parser Tests ====================

    @Test
    public void parser_parseTodo_returnsAddTodoCommand() throws FloraException {
        Command cmd = Parser.parse("todo read book");
        assertInstanceOf(AddTodoCommand.class, cmd);
    }

    @Test
    public void parser_parseTodo_noDescription_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () -> Parser.parse("todo"));
        assertEquals("At least put something bro", ex.getMessage());
    }

    @Test
    public void parser_parseDeadline_returnsAddDeadlineCommand() throws FloraException {
        Command cmd = Parser.parse("deadline submit /by 1/12/2024 18:00");
        assertInstanceOf(AddDeadlineCommand.class, cmd);
    }

    @Test
    public void parser_parseDeadline_noBy_throwsException() {
        FloraException ex = assertThrows(FloraException.class,
                () -> Parser.parse("deadline submit report"));
        assertEquals("At least set a due date bro", ex.getMessage());
    }

    @Test
    public void parser_parseDeadline_invalidDate_throwsException() {
        assertThrows(FloraException.class,
                () -> Parser.parse("deadline submit /by not-a-date"));
    }

    @Test
    public void parser_parseEvent_returnsAddEventCommand() throws FloraException {
        Command cmd = Parser.parse("event meeting /from 6/8/2024 14:00 /to 6/8/2024 16:00");
        assertInstanceOf(AddEventCommand.class, cmd);
    }

    @Test
    public void parser_parseEvent_noFrom_throwsException() {
        FloraException ex = assertThrows(FloraException.class,
                () -> Parser.parse("event meeting /to 6/8/2024 16:00"));
        assertEquals("At least set a start time bro", ex.getMessage());
    }

    @Test
    public void parser_parseEvent_noTo_throwsException() {
        FloraException ex = assertThrows(FloraException.class,
                () -> Parser.parse("event meeting /from 6/8/2024 14:00"));
        assertEquals("At least set an end time bro", ex.getMessage());
    }

    @Test
    public void parser_parseList_returnsListCommand() throws FloraException {
        Command cmd = Parser.parse("list");
        assertInstanceOf(ListCommand.class, cmd);
    }

    @Test
    public void parser_parseBye_returnsExitCommand() throws FloraException {
        Command cmd = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, cmd);
    }

    @Test
    public void parser_parseMark_returnsMarkCommand() throws FloraException {
        Command cmd = Parser.parse("mark 1");
        assertInstanceOf(MarkCommand.class, cmd);
    }

    @Test
    public void parser_parseUnmark_returnsUnmarkCommand() throws FloraException {
        Command cmd = Parser.parse("unmark 1");
        assertInstanceOf(UnmarkCommand.class, cmd);
    }

    @Test
    public void parser_parseDelete_returnsDeleteCommand() throws FloraException {
        Command cmd = Parser.parse("delete 1");
        assertInstanceOf(DeleteCommand.class, cmd);
    }

    @Test
    public void parser_parseFind_returnsFindCommand() throws FloraException {
        Command cmd = Parser.parse("find book");
        assertInstanceOf(FindCommand.class, cmd);
    }

    @Test
    public void parser_parseFind_noKeyword_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () -> Parser.parse("find"));
        assertEquals("Put a keyword.", ex.getMessage());
    }

    @Test
    public void parser_parseMark_noIndex_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () -> Parser.parse("mark"));
        assertEquals("At least put an index bro", ex.getMessage());
    }

    @Test
    public void parser_parseMark_invalidIndex_throwsException() {
        assertThrows(FloraException.class, () -> Parser.parse("mark abc"));
    }

    @Test
    public void parser_parseUnknownCommand_throwsException() {
        assertThrows(FloraException.class, () -> Parser.parse("blah"));
    }

    @Test
    public void parser_caseInsensitive() throws FloraException {
        Command cmd = Parser.parse("LIST");
        assertInstanceOf(ListCommand.class, cmd);
    }

    // ==================== Storage.parseTask Tests ====================

    @Test
    public void storage_parseTask_todo() throws FloraException {
        flora.task.Task task = Storage.parseTask("T | 0 | read book");
        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    public void storage_parseTask_todoDone() throws FloraException {
        flora.task.Task task = Storage.parseTask("T | 1 | read book");
        assertTrue(task.isDone());
    }

    @Test
    public void storage_parseTask_deadline() throws FloraException {
        flora.task.Task task = Storage.parseTask("D | 0 | submit report | 01/12/2024 18:00");
        assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    public void storage_parseTask_deadlineWithTime() throws FloraException {
        flora.task.Task task = Storage.parseTask("D | 1 | submit report | 01/12/2024 18:00");
        assertInstanceOf(Deadline.class, task);
        assertTrue(task.isDone());
        assertEquals("[D][X] submit report (by: 1 Dec 2024 at 18:00)", task.toString());
    }

    @Test
    public void storage_parseTask_event() throws FloraException {
        flora.task.Task task = Storage.parseTask(
                "E | 0 | project meeting | 06/08/2024 14:00 | 06/08/2024 16:00");
        assertInstanceOf(Event.class, task);
        assertEquals("project meeting", task.getDescription());
    }

    @Test
    public void storage_parseTask_invalidType_throwsException() {
        FloraException ex = assertThrows(FloraException.class,
                () -> Storage.parseTask("X | 0 | something"));
        assertTrue(ex.getMessage().contains("Invalid task type"));
    }

    // ==================== Storage roundtrip Tests ====================

    @Test
    public void storage_todoRoundTrip() throws FloraException {
        Todo original = new Todo("read book");
        flora.task.Task parsed = Storage.parseTask(original.toFileString());
        assertEquals(original.toString(), parsed.toString());
    }

    @Test
    public void storage_deadlineRoundTrip() throws FloraException {
        Deadline original = new Deadline("submit", LocalDateTime.of(2024, 12, 1, 18, 0));
        flora.task.Task parsed = Storage.parseTask(original.toFileString());
        assertEquals(original.toString(), parsed.toString());
    }

    @Test
    public void storage_eventRoundTrip() throws FloraException {
        Event original = new Event("meeting",
                LocalDateTime.of(2024, 8, 6, 14, 0),
                LocalDateTime.of(2024, 8, 6, 16, 0));
        flora.task.Task parsed = Storage.parseTask(original.toFileString());
        assertEquals(original.toString(), parsed.toString());
    }

    @Test
    public void storage_markedTaskRoundTrip() throws FloraException {
        Todo original = new Todo("read book");
        original.mark();
        flora.task.Task parsed = Storage.parseTask(original.toFileString());
        assertTrue(parsed.isDone());
        assertEquals(original.toString(), parsed.toString());
    }

    // ==================== Command.isExit Tests ====================

    @Test
    public void exitCommand_isExit_returnsTrue() {
        ExitCommand cmd = new ExitCommand();
        assertTrue(cmd.isExit());
    }

    @Test
    public void nonExitCommand_isExit_returnsFalse() throws FloraException {
        Command cmd = Parser.parse("list");
        assertFalse(cmd.isExit());
    }

    // ==================== FloraException Tests ====================

    @Test
    public void floraException_messagePreserved() {
        FloraException ex = new FloraException("test error");
        assertEquals("test error", ex.getMessage());
    }
}
