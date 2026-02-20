package flora;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
import flora.parser.Parser;
import flora.storage.Storage;
import flora.task.Deadline;
import flora.task.EditResult;
import flora.task.Event;
import flora.task.Task;
import flora.task.TaskList;
import flora.task.Todo;

public class FloraTest {

    /** Provides a Storage backed by a fresh temp file for each test. */
    @TempDir
    Path tempDir;

    private Storage tempStorage() {
        return new Storage(tempDir.resolve("tasks.txt").toString());
    }

    // ==================== Todo ====================

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

    // ==================== Deadline ====================

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

    // ==================== Event ====================

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

    // ==================== Todo.edit ====================

    @Test
    public void todoEdit_newDesc_updatesDescription() throws FloraException {
        Todo todo = new Todo("old desc");
        EditResult result = todo.edit("new desc", null, null, null);
        assertEquals("new desc", result.task().getDescription());
        assertTrue(result.invalidFields().isEmpty());
    }

    @Test
    public void todoEdit_nullDesc_keepsCurrentDescription() throws FloraException {
        Todo todo = new Todo("original");
        EditResult result = todo.edit(null, null, null, null);
        assertEquals("original", result.task().getDescription());
    }

    @Test
    public void todoEdit_dateFieldsReportedAsInvalid() throws FloraException {
        Todo todo = new Todo("buy milk");
        LocalDateTime someDate = LocalDateTime.of(2024, 1, 1, 12, 0);
        EditResult result = todo.edit(null, someDate, someDate, someDate);
        assertTrue(result.invalidFields().contains("/by"));
        assertTrue(result.invalidFields().contains("/from"));
        assertTrue(result.invalidFields().contains("/to"));
    }

    @Test
    public void todoEdit_preservesDoneStatus() throws FloraException {
        Todo todo = new Todo("buy milk");
        todo.mark();
        EditResult result = todo.edit("buy groceries", null, null, null);
        assertTrue(result.task().isDone());
    }

    // ==================== Deadline.edit ====================

    @Test
    public void deadlineEdit_newDesc_updatesDescription() throws FloraException {
        Deadline deadline = new Deadline("old task", LocalDateTime.of(2024, 12, 1, 18, 0));
        EditResult result = deadline.edit("new task", null, null, null);
        assertEquals("new task", result.task().getDescription());
        assertTrue(result.invalidFields().isEmpty());
    }

    @Test
    public void deadlineEdit_newDue_updatesDueDate() throws FloraException {
        LocalDateTime oldDue = LocalDateTime.of(2024, 12, 1, 18, 0);
        LocalDateTime newDue = LocalDateTime.of(2024, 12, 15, 18, 0);
        Deadline deadline = new Deadline("submit", oldDue);
        EditResult result = deadline.edit(null, newDue, null, null);
        assertTrue(result.task().toString().contains("15 Dec 2024"));
        assertTrue(result.invalidFields().isEmpty());
    }

    @Test
    public void deadlineEdit_eventFieldsReportedAsInvalid() throws FloraException {
        Deadline deadline = new Deadline("submit", LocalDateTime.of(2024, 12, 1, 18, 0));
        LocalDateTime someDate = LocalDateTime.of(2024, 1, 1, 12, 0);
        EditResult result = deadline.edit(null, null, someDate, someDate);
        assertTrue(result.invalidFields().contains("/from"));
        assertTrue(result.invalidFields().contains("/to"));
    }

    @Test
    public void deadlineEdit_preservesDoneStatus() throws FloraException {
        Deadline deadline = new Deadline("submit", LocalDateTime.of(2024, 12, 1, 18, 0));
        deadline.mark();
        EditResult result = deadline.edit("new task", null, null, null);
        assertTrue(result.task().isDone());
    }

    // ==================== Event.edit ====================

    @Test
    public void eventEdit_newDesc_updatesDescription() throws FloraException {
        Event event = new Event("old meeting",
                LocalDateTime.of(2024, 8, 6, 14, 0),
                LocalDateTime.of(2024, 8, 6, 16, 0));
        EditResult result = event.edit("new meeting", null, null, null);
        assertEquals("new meeting", result.task().getDescription());
    }

    @Test
    public void eventEdit_newStart_updatesStart() throws FloraException {
        Event event = new Event("meeting",
                LocalDateTime.of(2024, 8, 6, 14, 0),
                LocalDateTime.of(2024, 8, 6, 16, 0));
        LocalDateTime newStart = LocalDateTime.of(2024, 8, 6, 13, 0);
        EditResult result = event.edit(null, null, newStart, null);
        assertTrue(result.task().toString().contains("at 13:00"));
    }

    @Test
    public void eventEdit_newEnd_updatesEnd() throws FloraException {
        Event event = new Event("meeting",
                LocalDateTime.of(2024, 8, 6, 14, 0),
                LocalDateTime.of(2024, 8, 6, 16, 0));
        LocalDateTime newEnd = LocalDateTime.of(2024, 8, 6, 17, 0);
        EditResult result = event.edit(null, null, null, newEnd);
        assertTrue(result.task().toString().contains("at 17:00"));
    }

    @Test
    public void eventEdit_startAfterEnd_throwsException() {
        Event event = new Event("meeting",
                LocalDateTime.of(2024, 8, 6, 14, 0),
                LocalDateTime.of(2024, 8, 6, 16, 0));
        LocalDateTime laterStart = LocalDateTime.of(2024, 8, 6, 17, 0);
        assertThrows(FloraException.class, () -> event.edit(null, null, laterStart, null));
    }

    @Test
    public void eventEdit_startEqualsEnd_throwsException() {
        Event event = new Event("meeting",
                LocalDateTime.of(2024, 8, 6, 14, 0),
                LocalDateTime.of(2024, 8, 6, 16, 0));
        // Edit start to match the existing end → start == end → rejected
        LocalDateTime sameAsEnd = LocalDateTime.of(2024, 8, 6, 16, 0);
        assertThrows(FloraException.class, () -> event.edit(null, null, sameAsEnd, null));
    }

    @Test
    public void eventEdit_deadlineFieldReportedAsInvalid() throws FloraException {
        Event event = new Event("meeting",
                LocalDateTime.of(2024, 8, 6, 14, 0),
                LocalDateTime.of(2024, 8, 6, 16, 0));
        LocalDateTime someDate = LocalDateTime.of(2024, 1, 1, 12, 0);
        EditResult result = event.edit(null, someDate, null, null);
        assertTrue(result.invalidFields().contains("/by"));
    }

    @Test
    public void eventEdit_preservesDoneStatus() throws FloraException {
        Event event = new Event("meeting",
                LocalDateTime.of(2024, 8, 6, 14, 0),
                LocalDateTime.of(2024, 8, 6, 16, 0));
        event.mark();
        EditResult result = event.edit("new meeting", null, null, null);
        assertTrue(result.task().isDone());
    }

    // ==================== Task: getDetailsKey ====================

    @Test
    public void todoGetDetailsKey_includesTypeAndDescription() {
        Todo todo = new Todo("buy milk");
        assertEquals("T|buy milk", todo.getDetailsKey());
    }

    @Test
    public void deadlineGetDetailsKey_includesTypeDescriptionAndDue() {
        LocalDateTime due = LocalDateTime.of(2024, 12, 1, 18, 0);
        Deadline deadline = new Deadline("submit", due);
        String key = deadline.getDetailsKey();
        assertTrue(key.startsWith("D|submit|"));
        assertTrue(key.contains("2024-12-01"));
    }

    @Test
    public void eventGetDetailsKey_includesTypeDescriptionAndBothDates() {
        LocalDateTime start = LocalDateTime.of(2024, 8, 6, 14, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 6, 16, 0);
        Event event = new Event("meeting", start, end);
        String key = event.getDetailsKey();
        assertTrue(key.startsWith("E|meeting|"));
        assertTrue(key.contains("2024-08-06"));
    }

    @Test
    public void getDetailsKey_doneStatusDoesNotAffectKey() {
        Todo undone = new Todo("buy milk");
        Todo done = new Todo("buy milk");
        done.mark();
        assertEquals(undone.getDetailsKey(), done.getDetailsKey());
    }

    @Test
    public void getDetailsKey_differentTypes_produceDifferentKeys() {
        LocalDateTime due = LocalDateTime.of(2024, 12, 1, 18, 0);
        Todo todo = new Todo("task");
        Deadline deadline = new Deadline("task", due);
        assertNotEquals(todo.getDetailsKey(), deadline.getDetailsKey());
    }

    @Test
    public void getDetailsKey_sameTypeDifferentDesc_produceDifferentKeys() {
        assertNotEquals(new Todo("task A").getDetailsKey(), new Todo("task B").getDetailsKey());
    }

    @Test
    public void getDetailsKey_sameDescDifferentDue_produceDifferentKeys() {
        LocalDateTime due1 = LocalDateTime.of(2024, 12, 1, 18, 0);
        LocalDateTime due2 = LocalDateTime.of(2024, 12, 2, 18, 0);
        assertNotEquals(
                new Deadline("submit", due1).getDetailsKey(),
                new Deadline("submit", due2).getDetailsKey());
    }

    // ==================== TaskList ====================

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
    public void taskList_set_replacesTaskAtIndex() {
        TaskList list = new TaskList();
        list.add(new Todo("original"));
        Todo replacement = new Todo("replacement");
        list.set(1, replacement);
        assertEquals(replacement, list.get(1));
        assertEquals(1, list.size());
    }

    @Test
    public void taskList_constructorWithList() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        TaskList list = new TaskList(tasks);
        assertEquals(2, list.size());
    }

    @Test
    public void taskList_stream_returnsAllTasks() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        list.add(new Todo("b"));
        list.add(new Todo("c"));
        assertEquals(3, list.stream().count());
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

    @Test
    public void taskList_containsTaskWithDetails_foundMatch() {
        TaskList list = new TaskList();
        list.add(new Todo("buy milk"));
        assertTrue(list.containsTaskWithDetails(new Todo("buy milk")));
    }

    @Test
    public void taskList_containsTaskWithDetails_noMatch() {
        TaskList list = new TaskList();
        list.add(new Todo("buy milk"));
        assertFalse(list.containsTaskWithDetails(new Todo("buy groceries")));
    }

    @Test
    public void taskList_containsTaskWithDetails_ignoresDoneStatus() {
        TaskList list = new TaskList();
        Todo done = new Todo("buy milk");
        done.mark();
        list.add(done);
        // An undone task with same description should still be detected as duplicate
        assertTrue(list.containsTaskWithDetails(new Todo("buy milk")));
    }

    @Test
    public void taskList_containsTaskWithDetailsExcluding_excludesTargetIndex() {
        TaskList list = new TaskList();
        list.add(new Todo("buy milk"));
        list.add(new Todo("buy groceries"));
        // Excluding index 1 ("buy milk"), there is no match for "buy milk"
        assertFalse(list.containsTaskWithDetailsExcluding(new Todo("buy milk"), 1));
    }

    @Test
    public void taskList_containsTaskWithDetailsExcluding_findsOtherIndex() {
        TaskList list = new TaskList();
        list.add(new Todo("buy milk"));
        list.add(new Todo("buy groceries"));
        // Excluding index 2, task at index 1 ("buy milk") is still checked
        assertTrue(list.containsTaskWithDetailsExcluding(new Todo("buy milk"), 2));
    }

    // ==================== Parser: basic commands ====================

    @Test
    public void parser_parseTodo_returnsAddTodoCommand() throws FloraException {
        Command cmd = Parser.parse("todo read book");
        assertInstanceOf(AddTodoCommand.class, cmd);
    }

    @Test
    public void parseTodo_noDescription_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () -> Parser.parse("todo"));
        assertEquals("At least put something bro", ex.getMessage());
    }

    @Test
    public void parser_parseDeadline_returnsAddDeadlineCommand() throws FloraException {
        Command cmd = Parser.parse("deadline submit /by 1/12/2024 18:00");
        assertInstanceOf(AddDeadlineCommand.class, cmd);
    }

    @Test
    public void parseDeadline_noBy_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () -> Parser.parse("deadline submit report"));
        assertEquals("At least set a due date bro", ex.getMessage());
    }

    @Test
    public void parseDeadline_invalidDate_throwsException() {
        assertThrows(FloraException.class, () -> Parser.parse("deadline submit /by not-a-date"));
    }

    @Test
    public void parser_parseEvent_returnsAddEventCommand() throws FloraException {
        Command cmd = Parser.parse("event meeting /from 6/8/2024 14:00 /to 6/8/2024 16:00");
        assertInstanceOf(AddEventCommand.class, cmd);
    }

    @Test
    public void parseEvent_noFrom_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () ->
                Parser.parse("event meeting /to 6/8/2024 16:00"));
        assertEquals("At least set a start time bro", ex.getMessage());
    }

    @Test
    public void parseEvent_noTo_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () ->
                Parser.parse("event meeting /from 6/8/2024"));
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
    public void parseFind_noKeyword_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () -> Parser.parse("find"));
        assertEquals("Put a keyword.", ex.getMessage());
    }

    @Test
    public void parseMark_noIndex_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () -> Parser.parse("mark"));
        assertEquals("At least put an index bro", ex.getMessage());
    }

    @Test
    public void parseMark_invalidIndex_throwsException() {
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

    // ==================== Parser: edit command ====================

    @Test
    public void parseEdit_descOnly_returnsEditCommand() throws FloraException {
        Command cmd = Parser.parse("edit 1 /desc new description");
        assertInstanceOf(EditCommand.class, cmd);
    }

    @Test
    public void parseEdit_byDateOnly_returnsEditCommand() throws FloraException {
        Command cmd = Parser.parse("edit 1 /by 1/1/2026 12:00");
        assertInstanceOf(EditCommand.class, cmd);
    }

    @Test
    public void parseEdit_allFields_returnsEditCommand() throws FloraException {
        Command cmd = Parser.parse("edit 1 /desc meeting /from 6/8/2024 14:00 /to 6/8/2024 16:00");
        assertInstanceOf(EditCommand.class, cmd);
    }

    @Test
    public void parseEdit_noIndex_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () -> Parser.parse("edit"));
        assertEquals("At least put an index bro", ex.getMessage());
    }

    @Test
    public void parseEdit_invalidIndexNonNumeric_throwsException() {
        assertThrows(FloraException.class, () -> Parser.parse("edit abc /desc x"));
    }

    @Test
    public void parseEdit_zeroIndex_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () -> Parser.parse("edit 0 /desc x"));
        assertTrue(ex.getMessage().contains("Invalid task index"));
    }

    @Test
    public void parseEdit_noFields_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () -> Parser.parse("edit 1"));
        assertTrue(ex.getMessage().contains("At least change something"));
    }

    @Test
    public void parseEdit_fromAndToReversed_throwsException() {
        assertThrows(FloraException.class, () ->
                Parser.parse("edit 1 /from 6/8/2024 16:00 /to 6/8/2024 14:00"));
    }

    @Test
    public void parseEdit_fromAndToEqual_throwsException() {
        assertThrows(FloraException.class, () ->
                Parser.parse("edit 1 /from 6/8/2024 14:00 /to 6/8/2024 14:00"));
    }

    // ==================== Parser: event start/end validation ====================

    @Test
    public void parseEvent_startAfterEnd_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () ->
                Parser.parse("event meeting /from 6/8/2024 16:00 /to 6/8/2024 14:00"));
        assertTrue(ex.getMessage().contains("Start time must be before end time"));
    }

    @Test
    public void parseEvent_startEqualsEnd_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () ->
                Parser.parse("event meeting /from 6/8/2024 14:00 /to 6/8/2024 14:00"));
        assertTrue(ex.getMessage().contains("Start time must be before end time"));
    }

    // ==================== Parser: non-existent dates ====================

    @Test
    public void parseDeadline_feb30_throwsException() {
        assertThrows(FloraException.class, () -> Parser.parse("deadline task /by 30/2/2026 14:00"));
    }

    @Test
    public void parseDeadline_april31_throwsException() {
        assertThrows(FloraException.class, () -> Parser.parse("deadline task /by 31/4/2026 14:00"));
    }

    @Test
    public void parseDeadline_feb29NonLeapYear_throwsException() {
        // 2025 is not a leap year
        assertThrows(FloraException.class, () -> Parser.parse("deadline task /by 29/2/2025 14:00"));
    }

    @Test
    public void parseDeadline_feb29LeapYear_doesNotThrow() throws FloraException {
        // 2024 is a leap year
        Command cmd = Parser.parse("deadline task /by 29/2/2024 14:00");
        assertInstanceOf(AddDeadlineCommand.class, cmd);
    }

    @Test
    public void parseEvent_nonExistentStartDate_throwsException() {
        assertThrows(FloraException.class, () ->
                Parser.parse("event fair /from 30/2/2026 10:00 /to 1/3/2026 10:00"));
    }

    @Test
    public void parseEvent_nonExistentEndDate_throwsException() {
        assertThrows(FloraException.class, () ->
                Parser.parse("event fair /from 1/3/2026 10:00 /to 30/2/2026 10:00"));
    }

    // ==================== Parser: natural language date shortcuts ====================

    @Test
    public void parseDeadline_today_doesNotThrow() throws FloraException {
        assertInstanceOf(AddDeadlineCommand.class, Parser.parse("deadline task /by today"));
    }

    @Test
    public void parseDeadline_tonight_doesNotThrow() throws FloraException {
        assertInstanceOf(AddDeadlineCommand.class, Parser.parse("deadline task /by tonight"));
    }

    @Test
    public void parseDeadline_tomorrow_doesNotThrow() throws FloraException {
        assertInstanceOf(AddDeadlineCommand.class, Parser.parse("deadline task /by tomorrow"));
    }

    @Test
    public void parseDeadline_nextWeek_doesNotThrow() throws FloraException {
        assertInstanceOf(AddDeadlineCommand.class, Parser.parse("deadline task /by next week"));
    }

    @Test
    public void parseDeadline_nextMonth_doesNotThrow() throws FloraException {
        assertInstanceOf(AddDeadlineCommand.class, Parser.parse("deadline task /by next month"));
    }

    // ==================== Storage.parseTask ====================

    @Test
    public void storage_parseTask_todo() throws FloraException {
        Task task = Storage.parseTask("T | 0 | read book");
        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    public void storage_parseTask_todoDone() throws FloraException {
        Task task = Storage.parseTask("T | 1 | read book");
        assertTrue(task.isDone());
    }

    @Test
    public void storage_parseTask_deadline() throws FloraException {
        Task task = Storage.parseTask("D | 0 | submit report | 01/12/2024 18:00");
        assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    public void storage_parseTask_deadlineWithTime() throws FloraException {
        Task task = Storage.parseTask("D | 1 | submit report | 01/12/2024 18:00");
        assertInstanceOf(Deadline.class, task);
        assertTrue(task.isDone());
        assertEquals("[D][X] submit report (by: 1 Dec 2024 at 18:00)", task.toString());
    }

    @Test
    public void storage_parseTask_event() throws FloraException {
        Task task = Storage.parseTask(
                "E | 0 | project meeting | 06/08/2024 14:00 | 06/08/2024 16:00");
        assertInstanceOf(Event.class, task);
        assertEquals("project meeting", task.getDescription());
    }

    @Test
    public void parseTask_invalidType_throwsException() {
        FloraException ex = assertThrows(FloraException.class, () -> Storage.parseTask("X | 0 | something"));
        assertTrue(ex.getMessage().contains("Invalid task type"));
    }

    @Test
    public void storageParseTask_missingDueDateDeadline_throwsException() {
        // Only 3 fields; deadline needs 4
        assertThrows(FloraException.class, () -> Storage.parseTask("D | 0 | submit report"));
    }

    @Test
    public void storageParseTask_missingDatesForEvent_throwsException() {
        // Only 4 fields; event needs 5
        assertThrows(FloraException.class, () ->
                Storage.parseTask("E | 0 | meeting | 06/08/2024 14:00"));
    }

    @Test
    public void storageParseTask_nonExistentDateDeadline_throwsException() {
        // Feb 30 doesn't exist
        assertThrows(FloraException.class, () ->
                Storage.parseTask("D | 0 | task | 30/02/2026 14:00"));
    }

    @Test
    public void storageParseTask_nonExistentDateEvent_throwsException() {
        assertThrows(FloraException.class, () ->
                Storage.parseTask("E | 0 | fair | 30/02/2026 10:00 | 01/03/2026 10:00"));
    }

    // ==================== Storage: round-trips ====================

    @Test
    public void storage_todoRoundTrip() throws FloraException {
        Todo original = new Todo("read book");
        Task parsed = Storage.parseTask(original.toFileString());
        assertEquals(original.toString(), parsed.toString());
    }

    @Test
    public void storage_deadlineRoundTrip() throws FloraException {
        Deadline original = new Deadline("submit", LocalDateTime.of(2024, 12, 1, 18, 0));
        Task parsed = Storage.parseTask(original.toFileString());
        assertEquals(original.toString(), parsed.toString());
    }

    @Test
    public void storage_deadlineMidnight_roundTrip() throws FloraException {
        // Midnight deadline is stored without time ("D | 0 | task | 01/12/2024")
        Deadline original = new Deadline("submit report", LocalDateTime.of(2024, 12, 1, 0, 0));
        Task parsed = Storage.parseTask(original.toFileString());
        assertEquals(original.toString(), parsed.toString());
    }

    @Test
    public void storage_eventRoundTrip() throws FloraException {
        Event original = new Event("meeting",
                LocalDateTime.of(2024, 8, 6, 14, 0),
                LocalDateTime.of(2024, 8, 6, 16, 0));
        Task parsed = Storage.parseTask(original.toFileString());
        assertEquals(original.toString(), parsed.toString());
    }

    @Test
    public void storage_eventMidnight_roundTrip() throws FloraException {
        // All-day event stored without time ("E | 0 | fair | 06/08/2024 | 08/08/2024")
        Event original = new Event("book fair",
                LocalDateTime.of(2024, 8, 6, 0, 0),
                LocalDateTime.of(2024, 8, 8, 0, 0));
        Task parsed = Storage.parseTask(original.toFileString());
        assertEquals(original.toString(), parsed.toString());
    }

    @Test
    public void storage_markedTaskRoundTrip() throws FloraException {
        Todo original = new Todo("read book");
        original.mark();
        Task parsed = Storage.parseTask(original.toFileString());
        assertTrue(parsed.isDone());
        assertEquals(original.toString(), parsed.toString());
    }

    // ==================== Command: AddTodoCommand ====================

    @Test
    public void addTodoCommand_execute_addsTaskToList() throws FloraException {
        TaskList tasks = new TaskList();
        AddTodoCommand cmd = new AddTodoCommand("buy milk");
        cmd.execute(tasks, tempStorage());
        assertEquals(1, tasks.size());
        assertEquals("buy milk", tasks.get(1).getDescription());
    }

    @Test
    public void addTodoCommand_executeDuplicate_throwsException() throws FloraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));
        AddTodoCommand cmd = new AddTodoCommand("buy milk");
        assertThrows(FloraException.class, () -> cmd.execute(tasks, tempStorage()));
    }

    @Test
    public void addTodoCommand_execute_duplicateRegardlessOfDoneStatus() throws FloraException {
        // Done task and undone task with the same description count as duplicates
        TaskList tasks = new TaskList();
        Todo done = new Todo("buy milk");
        done.mark();
        tasks.add(done);
        AddTodoCommand cmd = new AddTodoCommand("buy milk");
        assertThrows(FloraException.class, () -> cmd.execute(tasks, tempStorage()));
    }

    @Test
    public void addTodoCommand_getMessage_showsTaskAndCount() throws FloraException {
        TaskList tasks = new TaskList();
        AddTodoCommand cmd = new AddTodoCommand("buy milk");
        cmd.execute(tasks, tempStorage());
        String msg = cmd.getMessage();
        assertTrue(msg.contains("[T][ ] buy milk"));
        assertTrue(msg.contains("1 task"));
    }

    // ==================== Command: AddDeadlineCommand ====================

    @Test
    public void addDeadlineCommand_execute_addsTaskToList() throws FloraException {
        TaskList tasks = new TaskList();
        LocalDateTime due = LocalDateTime.of(2024, 12, 1, 18, 0);
        AddDeadlineCommand cmd = new AddDeadlineCommand("submit", due);
        cmd.execute(tasks, tempStorage());
        assertEquals(1, tasks.size());
        assertInstanceOf(Deadline.class, tasks.get(1));
    }

    @Test
    public void addDeadlineCommand_executeDuplicate_throwsException() throws FloraException {
        LocalDateTime due = LocalDateTime.of(2024, 12, 1, 18, 0);
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit", due));
        AddDeadlineCommand cmd = new AddDeadlineCommand("submit", due);
        assertThrows(FloraException.class, () -> cmd.execute(tasks, tempStorage()));
    }

    @Test
    public void addDeadlineCommand_samDescDifferentDue_notDuplicate() throws FloraException {
        LocalDateTime due1 = LocalDateTime.of(2024, 12, 1, 18, 0);
        LocalDateTime due2 = LocalDateTime.of(2024, 12, 2, 18, 0);
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit", due1));
        AddDeadlineCommand cmd = new AddDeadlineCommand("submit", due2);
        assertDoesNotThrow(() -> cmd.execute(tasks, tempStorage()));
    }

    // ==================== Command: AddEventCommand ====================

    @Test
    public void addEventCommand_execute_addsTaskToList() throws FloraException {
        TaskList tasks = new TaskList();
        LocalDateTime start = LocalDateTime.of(2024, 8, 6, 14, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 6, 16, 0);
        AddEventCommand cmd = new AddEventCommand("meeting", start, end);
        cmd.execute(tasks, tempStorage());
        assertEquals(1, tasks.size());
        assertInstanceOf(Event.class, tasks.get(1));
    }

    @Test
    public void addEventCommand_startEqualsEnd_throwsException() {
        LocalDateTime t = LocalDateTime.of(2024, 8, 6, 14, 0);
        AddEventCommand cmd = new AddEventCommand("meeting", t, t);
        assertThrows(FloraException.class, () -> cmd.execute(new TaskList(), tempStorage()));
    }

    @Test
    public void addEventCommand_startAfterEnd_throwsException() {
        LocalDateTime start = LocalDateTime.of(2024, 8, 6, 16, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 6, 14, 0);
        AddEventCommand cmd = new AddEventCommand("meeting", start, end);
        assertThrows(FloraException.class, () -> cmd.execute(new TaskList(), tempStorage()));
    }

    @Test
    public void addEventCommand_executeDuplicate_throwsException() throws FloraException {
        LocalDateTime start = LocalDateTime.of(2024, 8, 6, 14, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 6, 16, 0);
        TaskList tasks = new TaskList();
        tasks.add(new Event("meeting", start, end));
        AddEventCommand cmd = new AddEventCommand("meeting", start, end);
        assertThrows(FloraException.class, () -> cmd.execute(tasks, tempStorage()));
    }

    // ==================== Command: DeleteCommand ====================

    @Test
    public void deleteCommand_execute_removesTask() throws FloraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));
        tasks.add(new Todo("buy groceries"));
        DeleteCommand cmd = new DeleteCommand(1);
        cmd.execute(tasks, tempStorage());
        assertEquals(1, tasks.size());
        assertEquals("buy groceries", tasks.get(1).getDescription());
    }

    @Test
    public void deleteCommand_emptyList_throwsException() {
        TaskList tasks = new TaskList();
        DeleteCommand cmd = new DeleteCommand(1);
        assertThrows(FloraException.class, () -> cmd.execute(tasks, tempStorage()));
    }

    @Test
    public void deleteCommand_indexTooHigh_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only task"));
        DeleteCommand cmd = new DeleteCommand(2);
        assertThrows(FloraException.class, () -> cmd.execute(tasks, tempStorage()));
    }

    @Test
    public void deleteCommand_getMessage_showsRemovedTaskAndNewCount() throws FloraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("task to delete"));
        DeleteCommand cmd = new DeleteCommand(1);
        cmd.execute(tasks, tempStorage());
        String msg = cmd.getMessage();
        assertTrue(msg.contains("[T][ ] task to delete"));
        assertTrue(msg.contains("0 task"));
    }

    // ==================== Command: MarkCommand ====================

    @Test
    public void markCommand_execute_marksTask() throws FloraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));
        MarkCommand cmd = new MarkCommand(1);
        cmd.execute(tasks, tempStorage());
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    public void markCommand_alreadyMarked_returnsInfoMessage() throws FloraException {
        TaskList tasks = new TaskList();
        Todo task = new Todo("buy milk");
        task.mark();
        tasks.add(task);
        MarkCommand cmd = new MarkCommand(1);
        cmd.execute(tasks, null); // Won't save since task is already marked
        assertEquals("That task is already marked bro", cmd.getMessage());
    }

    @Test
    public void markCommand_indexTooHigh_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only task"));
        MarkCommand cmd = new MarkCommand(2);
        assertThrows(FloraException.class, () -> cmd.execute(tasks, tempStorage()));
    }

    @Test
    public void markCommand_getMessage_showsMarkedTask() throws FloraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));
        MarkCommand cmd = new MarkCommand(1);
        cmd.execute(tasks, tempStorage());
        assertTrue(cmd.getMessage().contains("[T][X] buy milk"));
    }

    // ==================== Command: UnmarkCommand ====================

    @Test
    public void unmarkCommand_execute_unmarksTask() throws FloraException {
        TaskList tasks = new TaskList();
        Todo task = new Todo("buy milk");
        task.mark();
        tasks.add(task);
        UnmarkCommand cmd = new UnmarkCommand(1);
        cmd.execute(tasks, tempStorage());
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    public void unmarkCommand_alreadyUnmarked_returnsInfoMessage() throws FloraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk")); // Not marked by default
        UnmarkCommand cmd = new UnmarkCommand(1);
        cmd.execute(tasks, null); // Won't save since task is already unmarked
        assertEquals("That task is already unmarked bro", cmd.getMessage());
    }

    @Test
    public void unmarkCommand_indexTooHigh_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only task"));
        UnmarkCommand cmd = new UnmarkCommand(2);
        assertThrows(FloraException.class, () -> cmd.execute(tasks, tempStorage()));
    }

    // ==================== Command: EditCommand ====================

    @Test
    public void editCommand_execute_updatesTaskDescription() throws FloraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("old desc"));
        EditCommand cmd = new EditCommand(1, "new desc", null, null, null);
        cmd.execute(tasks, tempStorage());
        assertEquals("new desc", tasks.get(1).getDescription());
        assertTrue(cmd.getMessage().contains("[T][ ] new desc"));
    }

    @Test
    public void editCommand_execute_updatesDeadlineDue() throws FloraException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit", LocalDateTime.of(2024, 12, 1, 18, 0)));
        LocalDateTime newDue = LocalDateTime.of(2024, 12, 15, 18, 0);
        EditCommand cmd = new EditCommand(1, null, newDue, null, null);
        cmd.execute(tasks, tempStorage());
        assertTrue(cmd.getMessage().contains("15 Dec 2024"));
    }

    @Test
    public void editCommand_indexTooHigh_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only task"));
        EditCommand cmd = new EditCommand(2, "new desc", null, null, null);
        assertThrows(FloraException.class, () -> cmd.execute(tasks, tempStorage()));
    }

    @Test
    public void editCommand_wouldCreateDuplicate_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));
        tasks.add(new Todo("buy groceries"));
        // Edit task 2 to be same as task 1 → duplicate
        EditCommand cmd = new EditCommand(2, "buy milk", null, null, null);
        assertThrows(FloraException.class, () -> cmd.execute(tasks, tempStorage()));
    }

    @Test
    public void editCommand_sameValues_notConsideredDuplicate() {
        // Editing task to its current description should not raise a duplicate error
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));
        EditCommand cmd = new EditCommand(1, "buy milk", null, null, null);
        assertDoesNotThrow(() -> cmd.execute(tasks, tempStorage()));
    }

    @Test
    public void editCommand_getMessage_showsIgnoredInvalidFields() throws FloraException {
        // /by is not applicable to a Todo; it should appear in the "Ignored" message
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));
        EditCommand cmd = new EditCommand(1, "new desc", LocalDateTime.of(2024, 12, 1, 18, 0), null, null);
        cmd.execute(tasks, tempStorage());
        String msg = cmd.getMessage();
        assertTrue(msg.contains("Ignored"));
        assertTrue(msg.contains("/by"));
    }

    // ==================== Command: FindCommand ====================

    @Test
    public void findCommand_execute_returnsMatchingTasks() throws FloraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("buy groceries"));
        FindCommand cmd = new FindCommand("book");
        cmd.execute(tasks, null);
        assertTrue(cmd.getMessage().contains("read book"));
        assertTrue(cmd.getMessage().contains("return book"));
        assertFalse(cmd.getMessage().contains("buy groceries"));
    }

    @Test
    public void findCommand_noMatches_returnsNoMatchMessage() throws FloraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));
        FindCommand cmd = new FindCommand("xyz");
        cmd.execute(tasks, null);
        assertEquals("No matching tasks.", cmd.getMessage());
    }

    @Test
    public void findCommand_execute_caseInsensitiveMatch() throws FloraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));
        FindCommand cmd = new FindCommand("book");
        cmd.execute(tasks, null);
        assertTrue(cmd.getMessage().contains("Read Book"));
    }

    // ==================== Command: ListCommand ====================

    @Test
    public void listCommand_emptyList_returnsEmptyMessage() throws FloraException {
        TaskList tasks = new TaskList();
        ListCommand cmd = new ListCommand();
        cmd.execute(tasks, null);
        assertEquals("Your list is empty.", cmd.getMessage());
    }

    @Test
    public void listCommand_withTasks_showsAllTasks() throws FloraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("task one"));
        tasks.add(new Todo("task two"));
        ListCommand cmd = new ListCommand();
        cmd.execute(tasks, null);
        String msg = cmd.getMessage();
        assertTrue(msg.contains("task one"));
        assertTrue(msg.contains("task two"));
    }

    @Test
    public void listCommand_getMessage_numbersTasks() throws FloraException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        ListCommand cmd = new ListCommand();
        cmd.execute(tasks, null);
        String msg = cmd.getMessage();
        assertTrue(msg.contains("1."));
        assertTrue(msg.contains("2."));
    }

    // ==================== Command.isExit ====================

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

    // ==================== FloraException ====================

    @Test
    public void floraException_messagePreserved() {
        FloraException ex = new FloraException("test error");
        assertEquals("test error", ex.getMessage());
    }
}
