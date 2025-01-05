package com.robonav.app.models;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Task {
    private final String id;       // Added task ID
    private final String name;
    private final String robotId;
    private final int progress;
    private final String createdBy; // New field for createdBy
    private final String dateCreated; // New field for dateCreated

    // Constructor to initialize from JSON object
    public Task(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");          // Retrieve task ID
        this.name = jsonObject.getString("name");      // Retrieve task name
        this.robotId = jsonObject.getString("robot");    // Retrieve assigned robot
        this.progress = jsonObject.getInt("progress"); // Retrieve task progress
        this.createdBy = jsonObject.getString("createdBy"); // Retrieve createdBy
        this.dateCreated = jsonObject.getString("dateCreated"); // Retrieve dateCreated


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
    public String getCreatedBy() {
        return createdBy;
    }
    public String getDateCreated() {
        return dateCreated;
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
}
