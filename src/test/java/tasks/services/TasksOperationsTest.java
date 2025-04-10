package tasks.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import tasks.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class TasksOperationsTest {

    // Helper to create Date objects easily in tests
    private Date createDate(String dateString) {
        try {
            // Using a lenient format that's easy to read
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date in test setup: " + dateString, e);
        }
    }

    // Helper to convert Iterable result to a List of IDs for easy assertion
    private List<Integer> getTaskIds(Iterable<Task> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(Task::getId)
                .collect(Collectors.toList());
    }


    @Test
    void F02_TC01_Path1_EmptyList() {
        // Input
        ObservableList<Task> taskList = FXCollections.observableArrayList(); // Use FXCollections to create an empty ObservableList
        TasksOperations scheduler = new TasksOperations(taskList);
        Date start = createDate("2023-01-01");
        Date end = createDate("2023-12-31");
        // Execute
        Iterable<Task> result = scheduler.incoming(start, end);

        // Output & Assert
        assertTrue(getTaskIds(result).isEmpty(), "Result should be empty for empty input list");
    }

    @Test
    void F02_TC02_Path2_NextTimeNull() {
        // Input: Task scheduled before start date, so nextTimeAfter returns null
        Date taskTimeStart = createDate("2023-05-10");
        Date taskTimeEnd = createDate("2023-05-20");
        Date start = createDate("2023-06-01");
        Date end = createDate("2023-12-31");
        Task task1 = new Task(1, "T1", "Description", taskTimeStart, taskTimeEnd, 0, true);
        ObservableList<Task> taskList = FXCollections.observableArrayList(task1);
        TasksOperations scheduler = new TasksOperations(taskList);

        // Execute
        Iterable<Task> result = scheduler.incoming(start, end);

        // Output & Assert
        assertTrue(getTaskIds(result).isEmpty(), "Result should be empty when nextTimeAfter returns null");
    }

    @Test
    void F02_TC03_Path3_NextTimeAfterEnd() {
        // Input: Task scheduled after end date
        Date start = createDate("2023-01-01");
        Date end = createDate("2024-01-31");
        Date taskTimeStart = createDate("2024-02-15");
        Date taskTimeEnd = createDate("2024-02-20");
        Task task2 = new Task(1, "T2", "Description", taskTimeStart, taskTimeEnd, 0, true);
        ObservableList<Task> taskList = FXCollections.observableArrayList(task2);
        TasksOperations scheduler = new TasksOperations(taskList);

        // Execute
        Iterable<Task> result = scheduler.incoming(start, end);

        // Output & Assert
        assertTrue(getTaskIds(result).isEmpty(), "Result should be empty when next time is after end date");
    }

    @Test
    void F02_TC04_Path4_TaskInRange() {
        // Input: Task scheduled within the start/end range
        Date start = createDate("2023-01-01");
        Date end = createDate("2023-12-31");
        Date taskTimeStart = createDate("2023-07-20");
        Date taskTimeEnd = createDate("2023-07-25");
        Task task3 = new Task(1, "T3", "Description", taskTimeStart, taskTimeEnd, 0, true);
        ObservableList<Task> taskList = FXCollections.observableArrayList(task3);
        TasksOperations scheduler = new TasksOperations(taskList);

        // Execute
        Iterable<Task> result = scheduler.incoming(start, end);

        // Output & Assert
        List<Integer> expectedIds = List.of(1);
        assertEquals(expectedIds, getTaskIds(result), "Result should contain the task within the range");
    }

    @Test
    void F02_TC05_LoopTwice_OneInOneOut() {
        // Input: Two tasks, one included, one excluded (after end)
        Date start = createDate("2023-01-01");
        Date end = createDate("2023-12-31");
        Task task4 = new Task(1, "T4", "Description", createDate("2023-11-01"), createDate("2023-11-10"), 0, true);
        Task task5 = new Task(2, "T5", "Description", createDate("2024-01-01"), createDate("2024-01-10"), 0, true);
        ObservableList<Task> taskList = FXCollections.observableArrayList(task4, task5);
        TasksOperations scheduler = new TasksOperations(taskList);

        // Execute
        Iterable<Task> result = scheduler.incoming(start, end);

        // Output & Assert
        List<Integer> expectedIds = List.of(1);
        assertEquals(expectedIds, getTaskIds(result), "Result should contain only the task within the range (T4)");
    }
}