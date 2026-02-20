package flora.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a list of tasks with operations to add, remove, find, and access tasks.
 */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList with the given list of tasks.
     *
     * @param tasks The initial list of tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        assert task != null : "Cannot add a null task";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given 1-based index.
     *
     * @param index The 1-based index of the task to remove.
     * @return The removed task.
     */
    public Task remove(int index) {
        assert index >= 1 && index <= tasks.size() : "Task index out of bounds: " + index;
        return tasks.remove(index - 1);
    }

    /**
     * Returns the task at the given 1-based index.
     *
     * @param index The 1-based index of the task.
     * @return The task at the specified index.
     */
    public Task get(int index) {
        assert index >= 1 && index <= tasks.size() : "Task index out of bounds: " + index;
        return tasks.get(index - 1);
    }

    /**
     * Replaces the task at the given 1-based index with the provided task.
     *
     * @param index The 1-based index of the task to replace.
     * @param task  The new task to place at that index.
     */
    public void set(int index, Task task) {
        assert index >= 1 && index <= tasks.size() : "Task index out of bounds: " + index;
        assert task != null : "Cannot set a null task";
        tasks.set(index - 1, task);
    }

    /**
     * Returns {@code true} if any task in the list has the same content (type, description,
     * and dates) as the given candidate, regardless of completion status.
     *
     * @param candidate The task to check against.
     * @return {@code true} if a task with identical details already exists.
     */
    public boolean containsTaskWithDetails(Task candidate) {
        String key = candidate.getDetailsKey();
        return tasks.stream().anyMatch(t -> t.getDetailsKey().equals(key));
    }

    /**
     * Returns {@code true} if any task other than the one at {@code excludeIndex} has
     * the same content as the given candidate. Used when editing a task to detect
     * whether the result would duplicate another existing task.
     *
     * @param candidate    The updated task to check against.
     * @param excludeIndex The 1-based index of the task being edited (excluded from search).
     * @return {@code true} if a different task with identical details already exists.
     */
    public boolean containsTaskWithDetailsExcluding(Task candidate, int excludeIndex) {
        String key = candidate.getDetailsKey();
        for (int i = 1; i <= tasks.size(); i++) {
            if (i != excludeIndex && tasks.get(i).getDetailsKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds all tasks whose descriptions contain the given keyword (case-insensitive).
     *
     * @param keyword The keyword to search for.
     * @return A new TaskList containing the matching tasks.
     */
    public TaskList find(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        List<Task> matches = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
        return new TaskList(matches);
    }

    /**
     * Returns a sequential stream over the tasks in this list.
     *
     * @return A stream of tasks.
     */
    public Stream<Task> stream() {
        return tasks.stream();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The size of the task list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
