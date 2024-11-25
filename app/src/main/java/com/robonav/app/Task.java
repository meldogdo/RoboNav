package com.robonav.app;

import org.json.JSONException;
import org.json.JSONObject;

public class Task {
    private String name;
    private String robot;
    private int progress;
    private String expectedEndTime;
    private String responsibleRobot;

    // Constructor to parse JSON object
    public Task(JSONObject jsonObject) {
        try {
            this.name = jsonObject.optString("name", "Unknown Task"); // Default to "Unknown Task" if key is missing
            this.robot = jsonObject.optString("robot", "Unknown Robot"); // Default to "Unknown Robot"
            this.progress = jsonObject.optInt("progress", -2); // Default to -2 (Queued) if key is missing
            this.expectedEndTime = jsonObject.optString("expectedEndTime", "N/A"); // Default to "N/A"
            this.responsibleRobot = jsonObject.optString("responsibleRobot", "Unknown"); // Default to "Unknown"
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getRobot() {
        return robot;
    }

    public int getProgress() {
        return progress;
    }

    public String getExpectedEndTime() {
        return expectedEndTime;
    }

    public String getResponsibleRobot() {
        return responsibleRobot;
    }
}
