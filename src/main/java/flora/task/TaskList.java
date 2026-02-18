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
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given 1-based index.
     *
     * @param index The 1-based index of the task to remove.
     * @return The removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index - 1);
    }

    /**
     * Returns the task at the given 1-based index.
     *
     * @param index The 1-based index of the task.
     * @return The task at the specified index.
     */
    public Task get(int index) {
        return tasks.get(index - 1);
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
