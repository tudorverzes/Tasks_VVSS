package tasks.services;

import javafx.collections.ObservableList;
import tasks.model.Task;

import java.util.*;

public class TasksOperations {
    public final List<Task> tasks;

    public TasksOperations(ObservableList<Task> tasksList){
        tasks=new ArrayList<>();
        tasks.addAll(tasksList);
    }
    public Iterable<Task> incoming(Date start, Date end){
        ArrayList<Task> incomingTasks = new ArrayList<>();

        if (!tasks.isEmpty()) {
            for (Task t : tasks) {
                Date nextTime = t.nextTimeAfter(start);
                if (nextTime != null && (nextTime.before(end) || nextTime.equals(end))) {
                    incomingTasks.add(t);
                }
            }
        }

        return incomingTasks;
    }
}

