package flora.task;

import java.util.List;

/**
 * Holds the result of a task edit operation: the updated task and
 * any field names that were ignored because they don't apply to the task type.
 */
public record EditResult(Task task, List<String> invalidFields) {
    /**
     * Returns true if any provided fields were invalid for this task type.
     *
     * @return {@code true} if there are invalid fields, {@code false} otherwise.
     */
    public boolean hasInvalidFields() {
        return !invalidFields.isEmpty();
    }
}
