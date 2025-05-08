package tasks.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tasks.model.Task;
import tasks.validator.TaskValidator;

import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArrayTaskListTest {
    private ArrayTaskList taskList;
    
    @Mock
    private TaskValidator mockValidator;
    
    private Task task1;
    private Task task2;
    private Date startDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskList = new ArrayTaskList();
        startDate = new Date();
        
        task1 = mock(Task.class);
        task2 = mock(Task.class);
        
        // Configure mock tasks
        when(task1.getId()).thenReturn(1);
        when(task1.getTitle()).thenReturn("Task 1");
        when(task2.getId()).thenReturn(2);
        when(task2.getTitle()).thenReturn("Task 2");
    }

    @Test
    @DisplayName("Test add task with mock validator")
    void testAddTaskWithMockValidator() {
        // Arrange
        Task realTask = new Task(1, "Real Task", "Description", startDate, true);
        
        // Act
        taskList.add(realTask);
        
        // Assert
        assertEquals(1, taskList.size());
        assertEquals(realTask, taskList.getTask(0));
    }

    @Test
    @DisplayName("Test iterator with mock tasks")
    void testIteratorWithMockTasks() {
        // Arrange
        taskList.add(task1);
        taskList.add(task2);
        
        // Act & Assert
        Iterator<Task> iterator = taskList.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(task1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(task2, iterator.next());
        assertFalse(iterator.hasNext());
        
        verify(task1, atLeastOnce()).getId();
        verify(task2, atLeastOnce()).getId();
    }

    @Test
    @DisplayName("Test remove task with mock")
    void testRemoveTaskWithMock() {
        // Arrange
        taskList.add(task1);
        taskList.add(task2);
        
        // Act
        boolean removed = taskList.remove(task1);
        
        // Assert
        assertTrue(removed);
        assertEquals(1, taskList.size());
        assertEquals(task2, taskList.getTask(0));
    }

    @Test
    @DisplayName("Test iterator remove with mock tasks")
    void testIteratorRemoveWithMockTasks() {
        // Arrange
        taskList.add(task1);
        taskList.add(task2);
        Iterator<Task> iterator = taskList.iterator();
        
        // Act
        iterator.next(); // Move to first element
        iterator.remove(); // Remove first element
        
        // Assert
        assertEquals(1, taskList.size());
        assertEquals(task2, taskList.getTask(0));
    }
}