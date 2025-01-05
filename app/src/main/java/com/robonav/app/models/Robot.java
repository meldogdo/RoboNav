package com.robonav.app.models;

import static com.robonav.app.utilities.JsonUtils.jsonArrayToList;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Robot {

    private final String id;                   // Unique ID for the robot
    private final String name;                 // Robot's name
    private final String ping;                 // Robot's network latency
    private final int battery;                 // Battery percentage
    private final List<String> tasks;          // List of task IDs assigned to the robot
    private String locationName;         // Location name (human-readable)
    private final String locationCoordinates;  // Location coordinates (x, y)

    // Constructor to initialize Robot object from a JSON object
    public Robot(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");                              // Retrieve robot ID
        this.name = jsonObject.getString("name");                          // Retrieve robot name
        this.ping = jsonObject.optString("ping", "Unknown");               // Retrieve ping, default to "Unknown"
        this.battery = jsonObject.optInt("battery", -1);                   // Retrieve battery level, default to -1
        this.locationName = jsonObject.optString("location_name", "");     // Retrieve location name, default to empty
        this.locationCoordinates = jsonObject.optString("location_coordinates", ""); // Retrieve location coordinates
        this.tasks = jsonArrayToList(jsonObject.optJSONArray("tasks"));    // Convert tasks array to List
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

    public String getLocationName() {
        return locationName;
    }
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    public String getLocationCoordinates() {
        return locationCoordinates;
    }

    // Override toString for better debugging output
    @NonNull
    @Override
    public String toString() {
        return "Robot{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ping='" + ping + '\'' +
                ", battery=" + battery +
                ", tasks=" + tasks +
                ", locationName='" + locationName + '\'' +
                ", locationCoordinates='" + locationCoordinates + '\'' +
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
    public static Robot findRobotByName(String name, List<Robot> robots) {
        for (Robot robot : robots) {
            if (robot.getName().equals(name)) {
                return robot;
            }
        }
        return null;
    }

}
