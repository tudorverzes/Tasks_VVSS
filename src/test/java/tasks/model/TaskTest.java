package tasks.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;
    private Date startDate;
    private Date endDate;

    @BeforeEach
    void setUp() {
        startDate = new Date();
        endDate = new Date(startDate.getTime() + 86400000); // +1 day
        task = new Task(1, "Test Task", "Test Description", startDate, endDate, 3600, true); // 1 hour interval
    }

    @Test
    @DisplayName("Test task creation with all parameters")
    void testTaskCreation() {
        assertEquals(1, task.getId());
        assertEquals("Test Task", task.getTitle());
        assertEquals("Test Description", task.getDescription());
        assertEquals(startDate, task.getStart());
        assertEquals(endDate, task.getEnd());
        assertEquals(3600, task.getInterval());
        assertTrue(task.isActive());
    }

    @Test
    @DisplayName("Test task is repeated when interval > 0")
    void testIsRepeated() {
        assertTrue(task.isRepeated());
        
        Task nonRepeatedTask = new Task(2, "Non-repeated", "Desc", startDate, true);
        assertFalse(nonRepeatedTask.isRepeated());
    }

    @Test
    @DisplayName("Test nextTimeAfter for repeated task")
    void testNextTimeAfterRepeated() {
        Date current = new Date(startDate.getTime() - 1000); // 1 second before start
        Date next = task.nextTimeAfter(current);
        assertEquals(startDate, next);

        current = new Date(startDate.getTime() + 1800000); // 30 minutes after start
        next = task.nextTimeAfter(current);
        assertEquals(startDate.getTime() + 3600000, next.getTime()); // Should return next hour
    }

    @Test
    @DisplayName("Test nextTimeAfter returns null after end date")
    void testNextTimeAfterEnd() {
        Date current = new Date(endDate.getTime() + 1000); // 1 second after end
        Date next = task.nextTimeAfter(current);
        assertNull(next);
    }
}