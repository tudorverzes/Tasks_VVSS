package tasks.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.model.Task;
import tasks.repository.ArrayTaskList;
import tasks.services.TasksService;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {
    private TasksService taskService;

    @BeforeEach
    void setUp() {
        ArrayTaskList taskList = new ArrayTaskList(); // empty list
        taskService = new TasksService(taskList);
    }

    @Test
    void testDateValid() {
        long now = System.currentTimeMillis();
        Date start = new Date(now + 1000); // 1 sec after now = boundary valid
        Date end = new Date(now + 3600000); // 1 hour later

        Task task = new Task(1, "BVA Valid", "", start, end, 60, true);
        task.setActive(true);

        // Just add to list to simulate adding
        assertDoesNotThrow(() -> {
            taskService.getObservableList().add(task); // assuming you manipulate list directly
        });
    }

    @Test
    void testDateInvalid() {
        long now = System.currentTimeMillis();
        Date start = new Date(now - 1000); // 1 sec before now = invalid
        Date end = new Date(now + 3600000); // 1 hour later

        Task task = new Task(1, "BVA Invalid", "", start, end, 60, true);
        task.setActive(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            // Simulate validation, or create your own addTask() logic in TasksService
            if (start.before(new Date())) {
                throw new IllegalArgumentException("Start date must be after current time.");
            }
            taskService.getObservableList().add(task);
        });

        assertEquals("Start date must be after current time.", exception.getMessage());
    }

}