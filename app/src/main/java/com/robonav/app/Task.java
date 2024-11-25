package com.robonav.app;

import org.json.JSONException;
import org.json.JSONObject;

public class Task {
    private String name;
    private String robot;
    private int progress;

    public Task(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("name");
        this.robot = jsonObject.getString("robot");
        this.progress = jsonObject.getInt("progress");

    }

    public String getName() {
        return name;
    }

    public String getRobot() {
        return robot;
    }

    public int getProgress() {
        return progress;
    }


}
