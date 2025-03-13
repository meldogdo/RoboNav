package com.robonav.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.robonav.app.R;
import com.robonav.app.adapters.RobotAdapter;
import com.robonav.app.adapters.TaskAdapter;
import com.robonav.app.models.Robot;
import com.robonav.app.models.Task;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView robotRecyclerView, taskRecyclerView;
    private RobotAdapter robotAdapter;
    private TaskAdapter taskAdapter;
    private List<Robot> robotList;
    private List<Task> taskList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize SwipeRefreshLayout and RecyclerViews
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        robotRecyclerView = rootView.findViewById(R.id.robot_recycler_view);
        taskRecyclerView = rootView.findViewById(R.id.task_recycler_view);

        // Initialize lists
        robotList = new ArrayList<>();
        taskList = new ArrayList<>();

        // Define your adapters upfront
        robotAdapter = new RobotAdapter(getContext(), robotList, taskList);
        taskAdapter = new TaskAdapter(getContext(), taskList, robotList);

        robotRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        robotRecyclerView.setAdapter(robotAdapter);

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        taskRecyclerView.setAdapter(taskAdapter);

        // Set swipe refresh listener to reload data
        swipeRefreshLayout.setOnRefreshListener(this::loadData);

        // Trigger the refresh on initial load
        swipeRefreshLayout.setRefreshing(true); // Start the refreshing animation
        loadData(); // Load the data after starting the refresh

        return rootView;
    }

    private void loadData() {
        // Clear the lists before loading new data
        robotList.clear();
        taskList.clear();

        // Notify adapters that the data is being cleared
        robotAdapter.notifyDataSetChanged();
        taskAdapter.notifyDataSetChanged();

        String robotUrl = "http://10.0.2.2:8080/api/robot/protected/robots";
        String taskUrl = "http://10.0.2.2:8080/api/robot/protected/tasks";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJpYXQiOjE3NDE3MzYzMTIsImV4cCI6MTc0MTczOTkxMn0.SN3NGzV7yZWK94cNw1d8bK3OqhMN0CJcdVs-z1IsVJ0"; // Ensure this token is up to date

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Load robot data first
        JsonArrayRequest robotRequest = new JsonArrayRequest(Request.Method.GET, robotUrl, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            Robot robot = new Robot(response.getJSONObject(i));
                            robotList.add(robot);
                        }

                        // After loading robots, load tasks
                        loadTasks(taskList, robotList, taskAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false); // Stop refreshing on error
                    }
                },
                error -> {
                    error.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false); // Stop refreshing on error
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        // Add robot request to the queue
        queue.add(robotRequest);

        // Start swipe refresh and prevent it from stopping until both requests finish
        swipeRefreshLayout.setRefreshing(true);
    }

    private void loadTasks(List<Task> taskList, List<Robot> robotList, TaskAdapter taskAdapter) {
        String taskUrl = "http://10.0.2.2:8080/api/robot/tasks";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJpYXQiOjE3NDE3MzYzMTIsImV4cCI6MTc0MTczOTkxMn0.SN3NGzV7yZWK94cNw1d8bK3OqhMN0CJcdVs-z1IsVJ0"; // Ensure this token is up to date

        // Load task data after robots are loaded
        JsonArrayRequest taskRequest = new JsonArrayRequest(Request.Method.GET, taskUrl, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            Task task = new Task(response.getJSONObject(i));
                            taskList.add(task);
                        }

                        // Notify both adapters that the data has been updated
                        taskAdapter.notifyDataSetChanged();
                        robotAdapter.notifyDataSetChanged(); // In case robot list size has changed
                        swipeRefreshLayout.setRefreshing(false); // Stop refreshing after both requests are complete
                    } catch (JSONException e) {
                        e.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false); // Stop refreshing on error
                    }
                },
                error -> {
                    error.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false); // Stop refreshing on error
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        // Add task request to the queue
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(taskRequest);
    }

}
