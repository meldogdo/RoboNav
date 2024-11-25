package com.robonav.app;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Task {
    private final String id;       // Added task ID
    private final String name;
    private final String robotId;
    private final int progress;

    // Constructor to initialize from JSON object
    public Task(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");          // Retrieve task ID
        this.name = jsonObject.getString("name");      // Retrieve task name
        this.robotId = jsonObject.getString("robot");    // Retrieve assigned robot
        this.progress = jsonObject.getInt("progress"); // Retrieve task progress
    }

    // Getters for the class variables
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRobotId() {
        return robotId;
    }

    public int getProgress() {
        return progress;
    }

    // Override toString for better debug output
    @NonNull
    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", robot_id='" + robotId + '\'' +
                ", progress=" + progress +
                '}';
    }

    // This method finds the Robot object based on the robot ID assigned to the task
    public static Robot getRobotForTask(Task task, List<Robot> robots) {
        for (Robot robot : robots) {
            if (robot.getId().equals(task.getRobotId())) {
                return robot;  // Return the robot whose ID matches the task's robotId
            }
        }
        return null; // Return null if no matching robot is found
    }
}
