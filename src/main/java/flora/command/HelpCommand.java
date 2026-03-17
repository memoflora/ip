package flora.command;

import flora.storage.Storage;
import flora.task.TaskList;

/**
 * Command to display a guide of all available commands.
 */
public class HelpCommand extends Command {

    private static final String HELP_MESSAGE = """
            Try one of these commands!

            Adding tasks
            • todo <description>
            • deadline <description> /by <date>
              (shortcuts: today, tomorrow, next week...)
            • event <description> /from <start> /to <end>

            Viewing tasks
            • list
            • find <keyword>

            Managing tasks
            • mark <task number>
            • unmark <task number>
            • delete <task number>
            • edit <task number> [/desc ..] [/by ..] [/from ..] [/to ..]

            Other
            • help — show this guide
            • bye — exit Flora

            Date format: d/M/yyyy or d/M/yyyy H:mm
            e.g. 25/12/2025 or 25/12/2025 14:00""";

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Storage storage) {
        // No side effects needed.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return HELP_MESSAGE;
    }
}
