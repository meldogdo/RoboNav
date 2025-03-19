package com.robonav.app.models;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Task {
    private final String id;       // Added task ID
    private final String name;
    private final String robotId;
    private final int progress;
    private final String createdBy; // New field for createdBy
    private final String dateCreated; // New field for dateCreated

    private final String state;

    private final String end;

    List<String> instructions;

    private final int instructionIndex;

    // Constructor to initialize from JSON object
    public Task(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");          // Retrieve task ID
        this.name = jsonObject.getString("name");      // Retrieve task name
        this.robotId = jsonObject.getString("robot");    // Retrieve assigned robot
        this.progress = jsonObject.getInt("progress"); // Retrieve task progress
        this.createdBy = jsonObject.getString("createdBy"); // Retrieve createdBy
        this.dateCreated = jsonObject.getString("dateCreated"); // Retrieve dateCreated
        this.state = jsonObject.getString("state");
        this.end = jsonObject.getString("dateCompleted");
        this.instructionIndex = jsonObject.getInt("instruction_index");
        // Parse instructions list
        this.instructions = new ArrayList<>();
        // Get the instructions field as a string
        String instructionsString = jsonObject.getString("instructions");
        // Parse the string as a JSON array (since it looks like a string formatted as an array)
        try {
            // Remove any unwanted characters and parse it
            JSONArray instructionsArray = new JSONArray(instructionsString);

            for (int i = 0; i < instructionsArray.length(); i++) {
                this.instructions.add(instructionsArray.getString(i));
            }
        } catch (JSONException e) {
        }


    }

    // Getters for the class variables
    public String getEnd(){ return end;}
    public String getState(){ return state; }

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
    public List<String> getInstructions() { return instructions; }
    public int getInstructionIndex() { return instructionIndex; }

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
