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
    void addValidTask() {
        Task task = new Task(1, "Task 1", "Valid task", new Date(), new Date(System.currentTimeMillis() + 3600000), 0, true);
        taskList.add(task);
        assertEquals(1, taskList.size());
    }

    @Test
    @DisplayName("ECP2")
    void addDuplicateIdTask() {
        Task task1 = new Task(1, "Task 1", "First task", new Date(), new Date(System.currentTimeMillis() + 3600000), 0, true);
        Task task2 = new Task(1, "Task 2", "Duplicate ID", new Date(), new Date(System.currentTimeMillis() + 3600000), 0, true);
        taskList.add(task1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskList.add(task2));
        assertEquals("Task ID must be unique", exception.getMessage());
    }

    @Test
    @DisplayName("ECP3")
    void listSizeAfterAdditions() {
        taskList.add(new Task(1, "Task 1", "First task", new Date(), new Date(System.currentTimeMillis() + 3600000), 0, true));
        taskList.add(new Task(2, "Task 2", "Second task", new Date(), new Date(System.currentTimeMillis() + 7200000), 0, true));
        assertEquals(2, taskList.size());
    }

    @Test
    @DisplayName("BVA3")
    public void testAddOneTask() {
        ArrayTaskList taskList = new ArrayTaskList();
        Task task = new Task(1, "Test Task", "Description", new Date(), true);

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertEquals(task, taskList.getTask(0));
    }

    @Test
    @DisplayName("BVA4")
    public void testGetTaskOutOfBounds() {
        ArrayTaskList taskList = new ArrayTaskList();

        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            taskList.getTask(0);
        });

        assertEquals("Index not found", exception.getMessage());
    }

}