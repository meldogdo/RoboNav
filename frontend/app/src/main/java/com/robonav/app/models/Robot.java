package com.robonav.app.models;

import static com.robonav.app.utilities.JsonUtils.jsonArrayToList;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Robot {

    private final String id;                   // Unique ID for the robot
    private final String name;                 // Robot's name
    private final String ipAdd;                // Robot's IP address
    private final int battery;                 // Battery percentage
    private final List<String> tasks;          // List of task IDs assigned to the robot
    private String locationName;               // Location name (human-readable)
    private final String locationCoordinates;  // Location coordinates (x, y)

    private final int isCharging;

    // Constructor to initialize Robot object from a JSON object
    public Robot(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");                              // Retrieve robot ID
        this.name = jsonObject.getString("name");                          // Retrieve robot name
        this.ipAdd = jsonObject.optString("ip_add", "Unknown");            // Retrieve IP address, default to "Unknown"
        this.battery = jsonObject.optInt("battery", -1);                   // Retrieve battery level, default to -1
        this.locationName = jsonObject.optString("location_name", "");     // Retrieve location name, default to empty
        this.locationCoordinates = jsonObject.optString("location_coordinates", ""); // Retrieve location coordinates
        this.tasks = jsonArrayToList(jsonObject.optJSONArray("tasks"));    // Convert tasks array to List
        this.isCharging = jsonObject.getInt("charging");
    }

    public int getIsCharging(){
        return isCharging;
    }

    // Getters for the class variables
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIpAdd() {
        return ipAdd;
    }

    public int getBattery() {
        return battery;
    }

    public List<String> getTasks() {
        return tasks;
    }

    // Method to get the location name
    public String getLocationName() {
        if (locationName != null && !locationName.equals("Unknown")) {
            return locationName;
        } else {
            return "Unnamed";
        }
    }

    // Method to get the location coordinates
    public String getLocationCoordinates() {
        if (locationCoordinates != null && !locationCoordinates.equals("Unknown")) {
            return locationCoordinates;
        } else {
            return "Unknown";
        }
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    // Override toString for better debugging output
    @NonNull
    @Override
    public String toString() {
        return "Robot{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ipAdd='" + ipAdd + '\'' +
                ", battery=" + battery +
                ", tasks=" + tasks +
                ", locationName='" + locationName + '\'' +
                ", locationCoordinates='" + locationCoordinates + '\'' +
                '}';
    }

    // Utility function to find the task in progress (progress between 0 and 99)
    public static Task getTaskInProgress(Robot robot, List<Task> taskList) {
        // Get the list of tasks for the robot
        List<Task> tasksForRobot = getTasksForRobot(robot, taskList);

        // Filter out tasks that are not active (state 1)
        tasksForRobot = tasksForRobot.stream()
                .filter(task -> task.getState().equals("1")) // Filter for active tasks only
                .collect(Collectors.toList());

        // If no active tasks are found, return null
        if (tasksForRobot.isEmpty()) {
            return null;
        }

        // Return the first active task (since we want just the one active task)
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

    public double getLatitude() {
        String[] coordinates = locationCoordinates.split(",");
        return coordinates.length == 2 ? Double.parseDouble(coordinates[0]) : 0;
    }

    public double getLongitude() {
        String[] coordinates = locationCoordinates.split(",");
        return coordinates.length == 2 ? Double.parseDouble(coordinates[1]) : 0;
    }
}
