package tasks.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tasks.model.Task;
import tasks.repository.ArrayTaskList; 
import tasks.services.DateService;
import tasks.services.TasksService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class TasksServiceIntegrationTest {
    private TasksService tasksService; 
    private ArrayTaskList taskRepository; 
    

    private Date getSpecificDate(int year, int month, int day, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, minute, 0); 
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @BeforeEach
    void setUp() {
        taskRepository = new ArrayTaskList(); 
        tasksService = new TasksService(taskRepository); 
    }
    
    @Test
    void testGetObservableList_WhenRepositoryHasMockTasks_ShouldReturnObservableListOfMocks() {
        
        Task mockTask1 = Mockito.mock(Task.class);
        when(mockTask1.getId()).thenReturn(1); 
        when(mockTask1.getTitle()).thenReturn("Mock Task 1");

        Task mockTask2 = Mockito.mock(Task.class);
        when(mockTask2.getId()).thenReturn(2);
        when(mockTask2.getTitle()).thenReturn("Mock Task 2");

        ArrayTaskList mockRepositoryForStep2 = Mockito.mock(ArrayTaskList.class);
        tasksService = new TasksService(mockRepositoryForStep2); 

        when(mockRepositoryForStep2.getAll()).thenReturn(List.of(mockTask1, mockTask2));

        
        List<Task> observableList = tasksService.getObservableList();

        
        assertNotNull(observableList);
        assertEquals(2, observableList.size());
        assertTrue(observableList.contains(mockTask1));
        assertTrue(observableList.contains(mockTask2));
        Mockito.verify(mockRepositoryForStep2).getAll(); 
    }

    @Test
    void testGetIntervalInHours_WhenRepositoryProvidesMockTask_ShouldCalculateCorrectly() {
        
        Task mockTask = Mockito.mock(Task.class);
        int intervalSeconds = (2 * DateService.MINUTES_IN_HOUR + 30) * DateService.SECONDS_IN_MINUTE; 
        when(mockTask.getInterval()).thenReturn(intervalSeconds);
        
        String intervalFormatted = tasksService.getIntervalInHours(mockTask);
        
        assertEquals("02:30", intervalFormatted);
        Mockito.verify(mockTask).getInterval();
    }

    @Test
    void testFilterTasks_WithRealTasksInRepository_ShouldReturnFilteredTasks() {
        Date startDateFilter = getSpecificDate(2024, 3, 10, 0, 0); 
        Date endDateFilter = getSpecificDate(2024, 3, 12, 0, 0);   

        Task task1_fits = new Task(1, "Task 1 fits", "Desc1",
                getSpecificDate(2024, 3, 10, 10, 0), 
                getSpecificDate(2024, 3, 15, 10, 0), 
                3600, 
                true); 

        Task task2_no_fit_before = new Task(2, "Task 2 before", "Desc2",
                getSpecificDate(2024, 3, 8, 10, 0),
                true); 

        Task task3_fits_non_repeated = new Task(3, "Task 3 fits non-rep", "Desc3",
                getSpecificDate(2024, 3, 11, 12, 0), 
                true); 

        Task task4_no_fit_after = new Task(4, "Task 4 after", "Desc4",
                getSpecificDate(2024, 3, 13, 10, 0), 
                getSpecificDate(2024, 3, 18, 10, 0), 
                3600,
                true);

        taskRepository.add(task1_fits);
        taskRepository.add(task2_no_fit_before);
        taskRepository.add(task3_fits_non_repeated);
        taskRepository.add(task4_no_fit_after);
        
        Iterable<Task> filteredTasksIterable = tasksService.filterTasks(startDateFilter, endDateFilter);
        List<Task> filteredTasks = new java.util.ArrayList<>();
        filteredTasksIterable.forEach(filteredTasks::add);
        
        assertNotNull(filteredTasks);
        assertEquals(2, filteredTasks.size(), "Ar trebui sa fie 2 task-uri filtrate");
        assertTrue(filteredTasks.stream().anyMatch(t -> t.getId() == task1_fits.getId()), "Task1 ar trebui sa fie inclus");
        assertTrue(filteredTasks.stream().anyMatch(t -> t.getId() == task3_fits_non_repeated.getId()), "Task3 ar trebui sa fie inclus");
        assertFalse(filteredTasks.stream().anyMatch(t -> t.getId() == task2_no_fit_before.getId()), "Task2 nu ar trebui sa fie inclus");
        assertFalse(filteredTasks.stream().anyMatch(t -> t.getId() == task4_no_fit_after.getId()), "Task4 nu ar trebui sa fie inclus");
    }

    @Test
    void testGetObservableList_WithRealTasks_ShouldReturnCorrectList() {
        Task realTask1 = new Task(10, "Real Task 10", "Desc10", getSpecificDate(2024, 1, 1, 10, 0), true);
        Task realTask2 = new Task(11, "Real Task 11", "Desc11", getSpecificDate(2024, 1, 2, 12, 0), false);

        taskRepository.add(realTask1);
        taskRepository.add(realTask2);
        
        List<Task> observableList = tasksService.getObservableList();
        
        assertNotNull(observableList);
        assertEquals(2, observableList.size());
        assertTrue(observableList.stream().anyMatch(t -> t.getId() == 10 && t.getTitle().equals("Real Task 10")));
        assertTrue(observableList.stream().anyMatch(t -> t.getId() == 11 && t.getTitle().equals("Real Task 11")));
    }

    @Test
    void testParseFromStringToSeconds_ValidInput_ShouldReturnCorrectSeconds() {
        String timeString = "03:45";
        
        int totalSeconds = tasksService.parseFromStringToSeconds(timeString);
        
        int expectedSeconds = (3 * 60 + 45) * 60;
        assertEquals(expectedSeconds, totalSeconds);
    }

    @Test
    void testParseFromStringToSeconds_InvalidFormat_ShouldThrowException() {
        String invalidTimeString = "03-45";
        
        assertThrows(NumberFormatException.class, () -> {
            tasksService.parseFromStringToSeconds(invalidTimeString);
        }, "Ar trebui sa arunce NumberFormatException pentru format invalid de timp");
    }
}
