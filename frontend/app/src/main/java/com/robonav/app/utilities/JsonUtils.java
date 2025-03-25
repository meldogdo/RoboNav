package com.robonav.app.utilities;

import static android.content.Context.MODE_PRIVATE;
import static com.robonav.app.utilities.FragmentUtils.extractVolleyErrorMessage;
import static com.robonav.app.utilities.FragmentUtils.showMessage;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import android.util.Pair;


import com.android.volley.DefaultRetryPolicy;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class JsonUtils {

    // Helper method to get token from SharedPreferences
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
                    String mess = extractVolleyErrorMessage(error);
                    showMessage(mess, context);
                    future.completeExceptionally(new Exception(error));
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
                    String mess = extractVolleyErrorMessage(error);
                    showMessage(mess, context);
                    future.completeExceptionally(new Exception(error));
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
                    String mess = extractVolleyErrorMessage(error);
                    showMessage(mess, context);
                    future.completeExceptionally(new Exception(error));
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

    public static CompletableFuture<List<String>> fetchRecentInstructions(Context context, String robotId) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        String instructionsUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/instructions";
        if (robotId != null && !robotId.isEmpty()) {
            instructionsUrl += "?robotId=" + robotId;  // Adding robotId as a query parameter if specified
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        String token = getTokenFromPrefs(context);
        if (token == null) {
            future.completeExceptionally(new Exception("Authentication error: No token found."));
            return future;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, instructionsUrl, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");

                        if (data.length() == 0) {
                            future.complete(Collections.emptyList());  // No instructions found
                            return;
                        }

                        List<String> instructionMessages = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            String message = data.getString(i);
                            instructionMessages.add(message);
                        }

                        future.complete(instructionMessages);  // Return the list of instructions
                    } catch (JSONException e) {
                        future.completeExceptionally(new Exception("Failed to parse instruction data.", e));
                    }
                },
                error -> {
                    error.printStackTrace();
                    String mess = extractVolleyErrorMessage(error);
                    showMessage(mess, context);
                    future.completeExceptionally(new Exception(error));

        }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);  // Adding the token to the request headers
                return headers;
            }
        };

        // Add the request to the queue
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
                    String mess = extractVolleyErrorMessage(error);
                    showMessage(mess, context);
                    future.completeExceptionally(new Exception(error));
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
}