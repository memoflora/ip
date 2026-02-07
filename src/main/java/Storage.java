import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Storage {
    private final Path filePath;
    private static final DateTimeFormatter dateTimeFileFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy[ HH:mm]");

    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    public static Task parseTask(String line) throws FloraException {
        String[] parts = line.split(" \\| ");
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task;

        switch (type) {
            case "T":
                task = new Todo(description);
                break;
            case "D":
                String dueStr = parts[3];
                LocalDateTime due = LocalDateTime.parse(dueStr, dateTimeFileFmt);
                task = new Deadline(description, due);
                break;
            case "E":
                String startStr = parts[3];
                String endStr = parts[4];
                LocalDateTime start = LocalDateTime.parse(startStr, dateTimeFileFmt);
                LocalDateTime end = LocalDateTime.parse(endStr, dateTimeFileFmt);
                task = new Event(description, start, end);
                break;
            default:
                throw new FloraException("Invalid task type: " + type);
        }

        if (isDone) {
            task.mark();
        }

        return task;
    }

    public List<Task> load() throws FloraException {
        List<Task> tasks = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Task task = parseTask(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (FloraException e) {
                    throw new FloraException("Skipped corrupted line: " + line + " | " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new FloraException("Failed loading storage: " + e.getMessage());
        }

        return tasks;
    }

    public void save(TaskList tasks) throws FloraException {
        try {
            Files.createDirectories(filePath.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (Task task : tasks) {
                    writer.write(task.toFileString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new FloraException("Failed saving to storage: " + e.getMessage());
        }
    }
}
