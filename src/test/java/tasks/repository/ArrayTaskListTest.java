package tasks.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.model.Task;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTaskListTest {
    private ArrayTaskList taskList;

    @BeforeEach
    void setUp() {
        taskList = new ArrayTaskList();
    }

    @Test
    @DisplayName("ECP1")
    void testCreateValidSimpleTask() {
        Task task = new Task(1, "Complete report", "Monthly report", new Date(2025, 5, 1), true);
        taskList.add(task);
        assertEquals(1, taskList.size());
    }

    @Test
    @DisplayName("ECP2")
    void testCreateValidRepeatingTask() {
        Task task = new Task(2, "Team meeting", "Weekly sync", new Date(2025, 5, 1), new Date(2025, 12, 31), 86400, true);
        taskList.add(task);
        assertEquals(1, taskList.size());
    }

    @Test
    @DisplayName("ECP3")
    void testCreateTaskWithShortTitle() {
        Task task = new Task(3, "AB", "Short task", new Date(2025, 5, 1), true);
        try {
            taskList.add(task);
        } catch (IllegalArgumentException e) {
            assertEquals("Task title must be at least 3 characters long", e.getMessage());
        }
    }

    @Test
    @DisplayName("ECP4")
    void testCreateRepeatingTaskWithoutEndDate() {
        Task task = new Task(4, "Null end", "Repeating task",
                new Date(2025, 5, 1),
                null,
                3600, true);
        try {
            taskList.add(task);
        } catch (IllegalArgumentException e) {
            assertEquals("End date is required for repeated tasks", e.getMessage());
        }
    }

    @Test
    @DisplayName("BVA1")
    public void testMinValidTitleLength() {
        Task task = new Task(1, "Doc", "Valid task",
                new Date(2025, 5, 1), true);
        taskList.add(task);
        assertEquals(1, taskList.size());
    }

    @Test
    @DisplayName("BVA2")
    public void testMaxValidTitleLength() {
        String maxTitle = "A".repeat(100);
        Task task = new Task(2, maxTitle, "Max length",
                new Date(2025, 5, 1),
                new Date(2025, 12, 31),
                86400, true);
        taskList.add(task);
        assertEquals(1, taskList.size());
    }

    @Test
    @DisplayName("BVA3")
    public void testBelowMinTitleLength() {
        Task task = new Task(3, "AB", "Invalid",
                new Date(2025, 5, 1), true);
        try {
            taskList.add(task);
        } catch (IllegalArgumentException e) {
            assertEquals("Task title must be at least 3 characters long", e.getMessage());
        }
    }

    @Test
    @DisplayName("BVA4")
    public void testAboveMaxTitleLength() {
        String longTitle = "A".repeat(101);
        Task task = new Task(4, longTitle, "Too long",
                new Date(2025, 5, 1),
                new Date(2025, 12, 31),
                86400, true);
        try {
            taskList.add(task);
        } catch (IllegalArgumentException e) {
            assertEquals("Task title cannot exceed 100 characters", e.getMessage());
        }
    }

}