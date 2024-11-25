package com.robonav.app;

import org.json.JSONException;
import org.json.JSONObject;

public class Robot {
    private final String name;
    private final String ping;
    private final String battery;
    private final String task;
    private final String location;

    public Robot(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("name");
        this.ping = jsonObject.getString("ping");
        this.battery = jsonObject.getString("battery");
        this.task = jsonObject.getString("task");
        this.location = jsonObject.getString("location");
    }

    public String getName() {
        return name;
    }

    public String getPing() {
        return ping;
    }

    public String getBattery() {
        return battery;
    }

    public int getBatteryPercentage() {
        return Integer.parseInt(battery.replace("%", ""));
    }

    public String getTask() {
        return task;
    }

    public String getLocation() {
        return location;
    }
}
