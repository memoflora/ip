package flora.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task remove(int index) {
        return tasks.remove(index - 1);
    }

    public Task get(int index) {
        return tasks.get(index - 1);
    }

    public TaskList find(String keyword) {
        TaskList matches = new TaskList();
        for (Task task : tasks) {
            String taskDesc = task.getDescription().toLowerCase();
            keyword = keyword.toLowerCase();

            if (taskDesc.contains(keyword)) {
                matches.add(task);
            }
        }

        return matches;
    }

    public int size() {
        return tasks.size();
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
