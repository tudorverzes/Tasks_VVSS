package tasks.model;

import org.apache.log4j.Logger;
import tasks.repository.TaskIO;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Task implements Serializable, Cloneable {
    private int id;
    private String title;
    private String description;
    private Date start;
    private Date end;
    private int interval = 0;
    private boolean active;

    public String getFormattedDateStart() {
        return start.toString();
    }

    public String getFormattedRepeated () {
        return interval == 0 ? "No" : "Yes";
    }

    public Task(int id, String title, String description, Date start, Date end, int interval, boolean active) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
        this.interval = interval;
        this.active = active;
    }

    public Task(int id, String title, String description, Date start, Date end, boolean active) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
        this.active = active;
    }

    public Task(int id, String title, String description, Date start, boolean active) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.start = start;
        this.active = active;
    }

    public Task(Task task) {
        this.title = task.title;
        this.description = task.description;
        this.start = task.start;
        this.end = task.end;
        this.interval = task.interval;
        this.active = task.active;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public int getInterval() {
        return interval;
    }

    public boolean isActive() {
        return active;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRepeated() {
        return interval != 0;
    }

    public Date nextTimeAfter(Date current) {
        if (current == null || !active) {
            return null;
        }

        if (current.after(end) || current.equals(end)) {
            return null;
        }

        if (current.before(start)) {
            return new Date(start.getTime());
        }

        long currentTime = current.getTime();
        long startTime = start.getTime();
        long endTime = end.getTime();
        long intervalMillis = interval * 1000L;

        long nextTime = startTime;
        while (nextTime <= endTime && nextTime <= currentTime) {
            nextTime += intervalMillis;
        }

        return nextTime <= endTime ? new Date(nextTime) : null;
    }

    @Override
    public Task clone() {
        try {
            Task clone = (Task) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}