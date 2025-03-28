package tasks.repository;


import org.apache.log4j.Logger;
import tasks.model.Task;

import java.util.*;

public class ArrayTaskList extends TaskList {

    private Task[] tasks;
    private int numberOfTasks;
    private int currentCapacity;
    private static final Logger log = Logger.getLogger(ArrayTaskList.class.getName());

    private class ArrayTaskListIterator implements Iterator<Task> {
        private int cursor;
        private int lastCalled = -1;

        @Override
        public boolean hasNext() {
            return cursor < numberOfTasks;
        }

        @Override
        public Task next() {
            if (!hasNext()) {
                log.error("Next iterator element doesn't exist");
                throw new NoSuchElementException("No next element");
            }
            lastCalled = cursor;
            return tasks[cursor++];
        }

        @Override
        public void remove() {
            if (lastCalled == -1) {
                throw new IllegalStateException();
            }
            ArrayTaskList.this.remove(tasks[lastCalled]);
            cursor = lastCalled;
            lastCalled = -1;
        }
    }

    public ArrayTaskList() {
        currentCapacity = 10;
        this.tasks = new Task[currentCapacity];
    }

    @Override
    public Iterator<Task> iterator() {
        return new ArrayTaskListIterator();
    }

    @Override
    public void add(Task task) {
        if (task == null) throw new NullPointerException("Task shouldn't be null");
        if (numberOfTasks == currentCapacity) {
            currentCapacity *= 2;
            tasks = Arrays.copyOf(tasks, currentCapacity);
        }
        tasks[numberOfTasks++] = task;
    }

    @Override
    public boolean remove(Task task) {
        if (task == null) return false;

        int indexOfTaskToDelete = -1;
        for (int i = 0; i < numberOfTasks; i++) {
            if (task.equals(tasks[i])) {
                indexOfTaskToDelete = i;
                break;
            }
        }

        if (indexOfTaskToDelete >= 0) {
            System.arraycopy(tasks, indexOfTaskToDelete + 1, tasks, indexOfTaskToDelete, numberOfTasks - indexOfTaskToDelete - 1);
            tasks[--numberOfTasks] = null; // Elimină referința ultimului element duplicat
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return numberOfTasks;
    }

    @Override
    public Task getTask(int index) {
        if (index < 0 || index >= numberOfTasks) {
            log.error("Not existing index");
            throw new IndexOutOfBoundsException("Index not found");
        }
        return tasks[index];
    }

    @Override
    public List<Task> getAll() {
        return Arrays.asList(Arrays.copyOf(tasks, numberOfTasks));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayTaskList that = (ArrayTaskList) o;

        return numberOfTasks == that.numberOfTasks && Arrays.equals(Arrays.copyOf(tasks, numberOfTasks), Arrays.copyOf(that.tasks, that.numberOfTasks));
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(Arrays.copyOf(tasks, numberOfTasks)) + numberOfTasks;
    }

    @Override
    public String toString() {
        return "ArrayTaskList{" +
                "tasks=" + Arrays.toString(Arrays.copyOf(tasks, numberOfTasks)) +
                ", numberOfTasks=" + numberOfTasks +
                ", currentCapacity=" + currentCapacity +
                '}';
    }

    @Override
    protected ArrayTaskList clone() {
        ArrayTaskList clonedList = new ArrayTaskList();
        for (int i = 0; i < numberOfTasks; i++) {
            clonedList.add(tasks[i]);
        }
        return clonedList;
    }
}
