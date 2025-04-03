package tasks.validator;

import tasks.model.Task;
import java.util.Date;

public class TaskValidator {
    private static final int MIN_TITLE_LENGTH = 3;
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MIN_INTERVAL = 1; // in seconds

    public void validate(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        validateId(task.getId());
        validateTitle(task.getTitle());
        validateDescription(task.getDescription());
        validateDates(task.getStart(), task.getEnd(), task.getInterval());
        validateInterval(task.getInterval());
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Task ID must be positive");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }

        if (title.length() < MIN_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Task title must be at least %d characters long", MIN_TITLE_LENGTH)
            );
        }

        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Task title cannot exceed %d characters", MAX_TITLE_LENGTH)
            );
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Task description cannot exceed %d characters", MAX_DESCRIPTION_LENGTH)
            );
        }
    }

    private void validateDates(Date start, Date end, int interval) {
        if (start == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }

        // For non-repeated tasks
        if (interval == 0) {
            return;
        }

        // For repeated tasks
        if (end == null) {
            throw new IllegalArgumentException("End date is required for repeated tasks");
        }

        if (end.before(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        if (end.equals(start)) {
            throw new IllegalArgumentException("End date cannot be same as start date for repeated tasks");
        }
    }

    private void validateInterval(int interval) {
        if (interval < 0) {
            throw new IllegalArgumentException("Interval cannot be negative");
        }

        if (interval > 0 && interval < MIN_INTERVAL) {
            throw new IllegalArgumentException(
                    String.format("Interval must be at least %d seconds for repeated tasks", MIN_INTERVAL)
            );
        }
    }

    public void validateForUpdate(Task existingTask, Task updatedTask) {
        validate(updatedTask);

        // Ensure ID doesn't change
        if (existingTask.getId() != updatedTask.getId()) {
            throw new IllegalArgumentException("Cannot change task ID");
        }

        // Additional business rules for updates could go here
    }
}
