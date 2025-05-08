package tasks.services;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tasks.model.Task;
import tasks.repository.ArrayTaskList;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TasksServiceTest {
    private TasksService tasksService;
    
    @Mock
    private ArrayTaskList mockTaskList;
    
    private Task task1;
    private Task task2;
    private Date startDate;
    private Date endDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tasksService = new TasksService(mockTaskList);
        
        startDate = new Date();
        endDate = new Date(startDate.getTime() + 86400000); // +1 day
        
        // Create mock tasks
        task1 = mock(Task.class);
        task2 = mock(Task.class);
        
        // Configure mock tasks
        when(task1.getTitle()).thenReturn("Task 1");
        when(task1.getStart()).thenReturn(startDate);
        when(task1.getEnd()).thenReturn(endDate);
        when(task1.getInterval()).thenReturn(3600); // 1 hour
        when(task1.isActive()).thenReturn(true);
        
        when(task2.getTitle()).thenReturn("Task 2");
        when(task2.getStart()).thenReturn(startDate);
        when(task2.isActive()).thenReturn(true);
    }

    @Test
    @DisplayName("Test getObservableList with mock repository")
    void testGetObservableListWithMock() {
        // Arrange
        List<Task> taskList = Arrays.asList(task1, task2);
        when(mockTaskList.getAll()).thenReturn(taskList);

        // Act
        ObservableList<Task> result = tasksService.getObservableList();

        // Assert
        assertEquals(2, result.size());
        verify(mockTaskList).getAll();
        assertEquals(task1, result.get(0));
        assertEquals(task2, result.get(1));
    }

    @Test
    @DisplayName("Test getIntervalInHours with mock task")
    void testGetIntervalInHours() {
        // Arrange
        when(task1.getInterval()).thenReturn(3600); // 1 hour

        // Act
        String result = tasksService.getIntervalInHours(task1);

        // Assert
        assertEquals("01:00", result);
        verify(task1, atLeastOnce()).getInterval();
    }

    @Test
    @DisplayName("Test filterTasks with mock repository")
    void testFilterTasks() {
        // Arrange
        List<Task> taskList = Arrays.asList(task1, task2);
        when(mockTaskList.getAll()).thenReturn(taskList);
        
        // Configure mock behavior for nextTimeAfter
        when(task1.nextTimeAfter(any(Date.class))).thenReturn(new Date(startDate.getTime() + 3600000));
        when(task2.nextTimeAfter(any(Date.class))).thenReturn(null);

        // Act
        Iterable<Task> filtered = tasksService.filterTasks(startDate, endDate);

        // Assert
        verify(mockTaskList).getAll();
        verify(task1).nextTimeAfter(any(Date.class));
        verify(task2).nextTimeAfter(any(Date.class));
        
        // Convert Iterable to List for easier assertion
        List<Task> filteredList = new java.util.ArrayList<>();
        filtered.forEach(filteredList::add);
        
        assertTrue(filteredList.contains(task1));
        assertEquals(1, filteredList.size()); // Only task1 should be in the filtered list
    }

    @Test
    @DisplayName("Test parseFromStringToSeconds")
    void testParseFromStringToSeconds() {
        // Arrange
        String timeString = "01:30"; // 1 hour and 30 minutes

        // Act
        int seconds = tasksService.parseFromStringToSeconds(timeString);

        // Assert
        assertEquals(5400, seconds); // 1 hour 30 minutes = 5400 seconds
    }
}