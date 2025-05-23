package tasks.repository;

import javafx.collections.ObservableList;
import org.apache.log4j.Logger;
import tasks.Main;
import tasks.model.Task;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskIO {
    private static final String IO_ERROR = "IO exception reading or writing file";
    private static final String[] TIME_ENTITY = {" day"," hour", " minute"," second"};
    private static final int SECONDS_IN_DAY = 86400;
    private static final int SECONDS_IN_HOUR = 3600;
    private static final int SECONDS_IN_MIN = 60;


    private static final Logger log = Logger.getLogger(TaskIO.class.getName());
    public static void write(TaskList tasks, OutputStream out) throws IOException {
        try (DataOutputStream dataOutputStream = new DataOutputStream(out)) {
            dataOutputStream.writeInt(tasks.size());
            for (Task t : tasks) {
                dataOutputStream.writeInt(t.getTitle().length());
                dataOutputStream.writeUTF(t.getTitle());
                dataOutputStream.writeBoolean(t.isActive());
                dataOutputStream.writeInt(t.getInterval());
                if (t.isRepeated()) {
                    dataOutputStream.writeLong(t.getStart().getTime());
                    dataOutputStream.writeLong(t.getEnd().getTime());
                }
            }
        }
    }
    public static void read(TaskList tasks, InputStream in)throws IOException {
        try (DataInputStream dataInputStream = new DataInputStream(in)) {
            int listLength = dataInputStream.readInt();
            for (int i = 0; i < listLength; i++) {
                String title = dataInputStream.readUTF();
                boolean isActive = dataInputStream.readBoolean();
                int interval = dataInputStream.readInt();
                Date startTime = new Date(dataInputStream.readLong());
                Task taskToAdd;
                if (interval > 0) {
                    Date endTime = new Date(dataInputStream.readLong());
                    taskToAdd = new Task(1, title, "", startTime, endTime, interval, true);
                } else {
                    taskToAdd = new Task(1, title, "", startTime, true);
                }
                taskToAdd.setActive(isActive);
                tasks.add(taskToAdd);
            }
        }
    }
    public static void writeBinary(TaskList tasks, File file)throws IOException{
        try (FileOutputStream fos = new FileOutputStream(file)) {
            write(tasks, fos);
        } catch (IOException e) {
            log.error(IO_ERROR);
        }
    }

    public static void readBinary(TaskList tasks, File file) throws IOException{
        try (FileInputStream fis = new FileInputStream(file)) {
            read(tasks, fis);
        } catch (IOException e) {
            log.error(IO_ERROR);
        }
    }
    public static void write(TaskList tasks, Writer out) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(out);
        Task lastTask = tasks.getTask(tasks.size()-1);
        for (Task t : tasks){
            bufferedWriter.write(getFormattedTask(t));
            bufferedWriter.write(t.equals(lastTask) ? ';' : '.');
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

    }

    public static void read(TaskList tasks, Reader in)  throws IOException {
        BufferedReader reader = new BufferedReader(in);
        String line;
        Task t;
        while ((line = reader.readLine()) != null){
            t = getTaskFromString(line);
            tasks.add(t);
        }
        reader.close();

    }
    public static void writeText(TaskList tasks, File file) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            write(tasks, fileWriter);
        } catch (IOException e) {
            log.error(IO_ERROR);
        }

    }
    public static void readText(TaskList tasks, File file) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            read(tasks, fileReader);
        }
    }
    //// service methods for reading
    private static Task getTaskFromString (String line){
        boolean isRepeated = line.contains("from");//if contains - means repeated
        boolean isActive = !line.contains("inactive");//if isn't inactive - means active
        //Task(String title, Date time)   Task(String title, Date start, Date end, int interval)
        Task result;
        String title = getTitleFromText(line);
        if (isRepeated){
            Date startTime = getDateFromText(line, true);
            Date endTime = getDateFromText(line, false);
            int interval = getIntervalFromText(line);
            result = new Task(1, title, "", startTime, endTime, interval, true);
        }
        else {
            Date startTime = getDateFromText(line, true);
            result = new Task(1, title, "", startTime, true);
        }
        result.setActive(isActive);
        return result;
    }
    //
    private static int getIntervalFromText(String line) {
        int totalSeconds = 0;

        int start = line.lastIndexOf("[");
        int end = line.lastIndexOf("]");
        if (start == -1 || end == -1 || start >= end) return 0; // Handle invalid input

        String trimmed = line.substring(start + 1, end);
        Pattern pattern = Pattern.compile("(\\d+) (day|hour|minute|second)s?");
        Matcher matcher = pattern.matcher(trimmed);

        Map<String, Integer> timeMultipliers = Map.of(
                "day", SECONDS_IN_DAY,
                "hour", SECONDS_IN_HOUR,
                "minute", SECONDS_IN_MIN,
                "second", 1
        );

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            totalSeconds += value * timeMultipliers.get(matcher.group(2));
        }

        return totalSeconds;
    }

    private static Date getDateFromText (String line, boolean isStartTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");
        Date date = null;
        String trimmedDate;
        int start;
        int end;

        if (isStartTime){
            start = line.indexOf("[");
            end = line.indexOf("]");
        }
        else {
            int firstRightBracket = line.indexOf("]");
            start = line.indexOf("[", firstRightBracket+1);
            end = line.indexOf("]", firstRightBracket+1);
        }
        trimmedDate = line.substring(start, end+1);
        try {
            date = simpleDateFormat.parse(trimmedDate);
        }
        catch (ParseException e){
            log.error("date parse exception");
        }
        return date;

    }
    private static String getTitleFromText(String line){
        int start = 1;
        int end = line.lastIndexOf("\"");
        String result = line.substring(start, end);
        result = result.replace("\"\"", "\"");
        return result;
    }


    ////service methods for writing
    private static String getFormattedTask(Task task){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");

        StringBuilder result = new StringBuilder();
        String title = task.getTitle();
        if (title.contains("\"")) title = title.replace("\"","\"\"");
        result.append("\"").append(title).append("\"");

        if (task.isRepeated()){
            result.append(" from ");
            result.append(simpleDateFormat.format(task.getStart()));
            result.append(" to ");
            result.append(simpleDateFormat.format(task.getEnd()));
            result.append(" every ").append("[");
            result.append(getFormattedInterval(task.getInterval()));
            result.append("]");
        }
        else {
            result.append(" at ");
            result.append(simpleDateFormat.format(task.getStart()));
        }
        if (!task.isActive()) result.append(" inactive");
        return result.toString().trim();
    }

    public static String getFormattedInterval(int interval){
        if (interval <= 0) throw new IllegalArgumentException("Interval <= 0");
        StringBuilder sb = new StringBuilder();

        int days = interval/ SECONDS_IN_DAY;
        int hours = (interval - SECONDS_IN_DAY *days) / SECONDS_IN_HOUR;
        int minutes = (interval - (SECONDS_IN_DAY *days + SECONDS_IN_HOUR *hours)) / SECONDS_IN_MIN;
        int seconds = (interval - (SECONDS_IN_DAY *days + SECONDS_IN_HOUR *hours + SECONDS_IN_MIN *minutes));

        int[] time = new int[]{days, hours, minutes, seconds};
        int i = 0;
        int j = time.length-1;
        while (time[i] == 0 || time[j] == 0){
            if (time[i] == 0) i++;
            if (time[j] == 0) j--;
        }

        for (int k = i; k <= j; k++){
            sb.append(time[k]);
            sb.append(time[k] > 1 ? TIME_ENTITY[k]+ "s" : TIME_ENTITY[k]);
            sb.append(" ");
        }
        return sb.toString();
    }


    public static void rewriteFile(ObservableList<Task> tasksList) {
        LinkedTaskList taskList = new LinkedTaskList();
        for (Task t : tasksList){
            taskList.add(t);
        }
        try {
            TaskIO.writeBinary(taskList, Main.savedTasksFile);
        }
        catch (IOException e){
            log.error(IO_ERROR);
        }
    }
}
