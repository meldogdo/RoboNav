package com.robonav.app;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;

public class Robot {
    private final String id;            // Added robot ID
    private final String name;
    private final String ping;
    private final int battery;
    private final List<String> tasks;  // Changed to List<String> to hold multiple task IDs
    private final String location;

    // Constructor to initialize from JSON object
    public Robot(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");               // Retrieve robot ID
        this.name = jsonObject.getString("name");           // Retrieve robot name
        this.ping = jsonObject.getString("ping");           // Retrieve ping
        this.battery = jsonObject.getInt("battery");        // Retrieve battery level
        this.location = jsonObject.getString("location");  // Retrieve robot location
        this.tasks = jsonArrayToList(jsonObject.getJSONArray("tasks")); // Convert tasks array to List
    }

    // Helper method to convert JSON array of tasks to List<String>
    private List<String> jsonArrayToList(JSONArray jsonArray) throws JSONException {
        List<String> taskList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            taskList.add(jsonArray.getString(i));
        }
        return taskList;
    }

    // Getters for the class variables
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPing() {
        return ping;
    }

    public int getBattery() {
        return battery;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public String getLocation() {
        return location;
    }

    // Override toString for better debug output
    @NonNull
    @Override
    public String toString() {
        return "Robot{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ping='" + ping + '\'' +
                ", battery=" + battery +
                ", tasks=" + tasks +
                ", location='" + location + '\'' +
                '}';
    }

    // Utility function to find the task in progress (progress between 0 and 99)
    public static Task getTaskInProgress(Robot robot, List<Task> taskList) {
        List<Task> tasksForRobot = getTasksForRobot(robot, taskList);
        if (tasksForRobot.isEmpty()) {
            return null; // No task in progress if the list is empty
        }
        // Return the first task as the active task (or your logic)
        return tasksForRobot.get(0);
    }


    // Method to get tasks based on robot ID from a list of tasks
    public static List<Task> getTasksForRobot(Robot robot, List<Task> taskList) {
        if (robot.getTasks().isEmpty()) {
            return new ArrayList<>(); // Return an empty list if the robot has no tasks
        }
        // Existing logic for finding tasks
        List<Task> tasksForRobot = new ArrayList<>();
        for (String taskId : robot.getTasks()) {
            for (Task task : taskList) {
                if (task.getId().equals(taskId)) {
                    tasksForRobot.add(task);
                }
            }
        }
        return tasksForRobot;
    }

}
