import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Storage {
    private final Path filePath;


    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    public Task parseTask(String line) {
        try {
            String[] parts = line.split(" \\| ");
            String type = parts[0];
            boolean isDone = parts[1].equals("1");
            String description = parts[2];

            Task task;
            DateTimeFormatter dateTimeFileFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy[ HH:mm]");

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
                    return null;
            }

            if (isDone) {
                task.markAsDone();
            }

            return task;
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Task task = parseTask(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (Exception e) {
                    System.out.println("Skipping corrupted line: " + line);
                }
            }
        }

        return tasks;
    }

    public void save(ArrayList<Task> tasks) throws IOException {
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Task task : tasks) {
                writer.write(task.toFileString());
                writer.newLine();
            }
        }
    }
}
