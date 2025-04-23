package tasks.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import tasks.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class TasksOperationsTest {


    private Date createDate(String dateString) {
        try {

            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date in test setup: " + dateString, e);
        }
    }


    private List<Integer> getTaskIds(Iterable<Task> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(Task::getId)
                .collect(Collectors.toList());
    }

    @Test
    void testEmptyTasksList() {
        ObservableList<Task> emptyList = FXCollections.observableArrayList();
        TasksOperations operations = new TasksOperations(emptyList);
        Iterable<Task> result = operations.incoming(new Date(), new Date());
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void testTaskWithNullNextTime() {
        Date taskTimeStart = createDate("2023-05-10");
        Date taskTimeEnd = createDate("2023-05-20");
        Task t = new Task(1, "T1", "Description", taskTimeStart, taskTimeEnd, 0, true);

        ObservableList<Task> list = FXCollections.observableArrayList(t);
        TasksOperations operations = new TasksOperations(list);
        Date start = new Date();
        Date end = new Date(start.getTime() + 100000);
        Iterable<Task> result = operations.incoming(start, end);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void testTaskAfterEnd() {
        Date now = new Date();
        Date start = new Date(now.getTime());
        Date end = new Date(now.getTime() + 1000);

        Date taskTimeStart = createDate("2023-05-10");
        Date taskTimeEnd = createDate("2023-05-20");
        Task t = new Task(1, "T1", "Description", taskTimeStart, taskTimeEnd, 0, true);

        ObservableList<Task> list = FXCollections.observableArrayList(t);
        TasksOperations operations = new TasksOperations(list);
        Iterable<Task> result = operations.incoming(start, end);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void testTaskAtEnd() {
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
    void testTaskBeforeEnd() {
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