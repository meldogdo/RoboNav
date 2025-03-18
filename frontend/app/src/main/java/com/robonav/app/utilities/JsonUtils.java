package com.robonav.app.utilities;



import static com.robonav.app.utilities.FragmentUtils.showMessage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.robonav.app.models.Robot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class JsonUtils {

    private static String getTokenFromPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        return prefs.getString("JWT_TOKEN", null); // Default to empty string if not found
    }

    // Reads JSON file from the assets folder
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public static CompletableFuture<List<String>> loadRobotNames(Context context) {
        // Initialize the CompletableFuture to return the result
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        List<String> robotNames = new ArrayList<>();
        String robotUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/robots";
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest robotRequest = new JsonArrayRequest(Request.Method.GET, robotUrl, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            Robot robot = new Robot(response.getJSONObject(i));
                            robotNames.add(robot.getName());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Complete the future when robot names are loaded
                    future.complete(robotNames);
                },
                error -> {
                    error.printStackTrace();
                    // If there's an error, complete the future exceptionally
                    future.completeExceptionally(error);
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                // Correct the header map type
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getTokenFromPrefs(context));
                return headers;
            }
        };

        queue.add(robotRequest);

        return future;  // Return the CompletableFuture
    }

    public static CompletableFuture<HashMap<String, Pair<String, String>>> loadRobotTasks(Context context) {
        CompletableFuture<HashMap<String, Pair<String, String>>> future = new CompletableFuture<>();
        HashMap<String, Pair<String, String>> taskMap = new HashMap<>();

        String taskUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/tasks";
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest taskRequest = new JsonArrayRequest(Request.Method.GET, taskUrl, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject taskObj = response.getJSONObject(i);
                            String id = taskObj.getString("id");
                            String name = taskObj.getString("name");
                            String robotId = taskObj.getString("robot");

                            // Change the key from 'id' to 'name'
                            taskMap.put(name, new Pair<>(id, robotId)); // Now name is the key
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        future.completeExceptionally(e);
                        return;
                    }
                    future.complete(taskMap);
                },
                error -> {
                    error.printStackTrace();
                    future.completeExceptionally(error);
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getTokenFromPrefs(context));
                return headers;
            }
        };

        // Add the request to the queue
        queue.add(taskRequest);
        return future;
    }

    public static CompletableFuture<Boolean> sendInstructionToTask(Context context, String taskId, String instruction) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        String instructionUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/task/instruction";
        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("task_id", taskId);
            requestBody.put("instruction", instruction);
        } catch (JSONException e) {
            future.completeExceptionally(e);
            return future;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, instructionUrl, requestBody,
                response -> {
                    // Task instruction successfully sent
                    future.complete(true);
                    showMessage("Instruction queued successfully.", context);
                },
                error -> {
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        String responseBody = new String(error.networkResponse.data);

                        String message = "Unexpected error.";
                        switch (statusCode) {
                            case 400:
                                if (responseBody.contains("Task not found or has already started")) {
                                    message = "Task not found or already started.";
                                }
                                break;
                        }
                        showMessage(message, context);
                    }
                    future.completeExceptionally(new Exception("Failed to send instruction to task."));
                }) {

            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                String token = getTokenFromPrefs(context);
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");

                return headers;
            }
        };

        queue.add(request);
        return future;
    }











    // Helper method to convert JSON array of tasks to List<String>
    public static List<String> jsonArrayToList(JSONArray jsonArray) throws JSONException {
        List<String> taskList = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                taskList.add(jsonArray.getString(i));
            }
        }
        return taskList;
    }

    public static CompletableFuture<List<JSONObject>> loadLocationDetails(Context context, String robotId) {
        CompletableFuture<List<JSONObject>> future = new CompletableFuture<>();
        List<JSONObject> locationDetails = new ArrayList<>();
        String locationUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/" + robotId + "/location";
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest locationRequest = new JsonArrayRequest(Request.Method.GET, locationUrl, null,
                response -> {
                    try {
                        // Iterate through the JSONArray directly
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject location = response.getJSONObject(i);

                            JSONObject locationInfo = new JSONObject();
                            locationInfo.put("location_name", location.getString("location_name"));
                            locationInfo.put("location_coordinates", location.getString("location_coordinates"));

                            locationDetails.add(locationInfo);
                        }

                        Log.d("LocationDetails", locationDetails.toString());
                        future.complete(locationDetails);

                    } catch (JSONException e) {
                        Log.e("LocationDetailsError", "Error parsing JSON", e);
                        future.completeExceptionally(e);
                    } catch (Exception e) {
                        Log.e("LocationDetailsError", "Error occurred", e);
                        future.completeExceptionally(e);
                    }
                },
                error -> {
                    Log.e("LocationDetailsError", "Network error", error);
                    future.completeExceptionally(error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                String token = getTokenFromPrefs(context);
                Log.d("AuthToken", "Token: " + token);
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(locationRequest);

        return future;
    }

    public static CompletableFuture<List<String>> loadCallbacks(Context context) {
        // Initialize the CompletableFuture to return the result
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        String url = ConfigManager.getBaseUrl() + "/api/protected/robot/callbacks";

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // Parse the response
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray data = jsonResponse.getJSONArray("data");

                        List<String> callbackMessages = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            callbackMessages.add(data.getString(i));
                        }

                        // Complete the future with the callback messages data
                        future.complete(callbackMessages);

                    } catch (JSONException e) {
                        // Handle the exception and complete the future exceptionally
                        future.completeExceptionally(new Exception("Error parsing callback data.", e));
                    }
                },
                error -> {
                    // Handle the error and complete the future exceptionally
                    future.completeExceptionally(new Exception("Failed to fetch callbacks.", error));
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                // Correct the header map type
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getTokenFromPrefs(context));
                return headers;
            }
        };

        queue.add(request);
        return future;  // Return the CompletableFuture with the list of callback messages
    }



    // Method to send robot instruction to API
    public static CompletableFuture<String> sendRobotInstruction(Context context, String robotId, String instruction) {
        // Initialize the CompletableFuture to return the result
        CompletableFuture<String> future = new CompletableFuture<>();

        // Prepare the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("robot_id", robotId);
            requestBody.put("instruction", instruction);
        } catch (JSONException e) {
            e.printStackTrace();
            future.completeExceptionally(e);
            return future;  // Return if there's an error preparing the request
        }

        // Define the URL for the API endpoint
        String apiUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/task/instruction";
        RequestQueue queue = Volley.newRequestQueue(context);

        // Create a JsonObjectRequest for the POST request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, apiUrl, requestBody,
                response -> {
                    try {
                        // Parse the response to get the message
                        String message = response.getString("message");
                        // Complete the future with the success message
                        future.complete(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        future.completeExceptionally(e);  // Complete exceptionally if JSON parsing fails
                    }
                },
                error -> {
                    error.printStackTrace();
                    // Complete the future exceptionally if there was an error with the request
                    future.completeExceptionally(error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Provide authorization headers if needed
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getTokenFromPrefs(context));  // Add token from preferences
                return headers;
            }
        };

        // Add the request to the queue
        queue.add(request);

        // Return the CompletableFuture that will be completed when the response is received
        return future;
    }








}