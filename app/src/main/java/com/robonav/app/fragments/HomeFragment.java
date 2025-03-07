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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize SwipeRefreshLayout and RecyclerViews
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        robotRecyclerView = rootView.findViewById(R.id.robot_recycler_view);
        taskRecyclerView = rootView.findViewById(R.id.task_recycler_view);

        loadData();

        // Set swipe refresh listener to reload data
        swipeRefreshLayout.setOnRefreshListener(this::loadData);

        return rootView;
    }

    private void loadData() {
        List<Robot> robotList = new ArrayList<>();
        List<Task> taskList = new ArrayList<>();

        String robotUrl = "http://10.0.2.2:8080/api/robot/robots";
        String taskUrl = "http://10.0.2.2:8080/api/robot/tasks";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJpYXQiOjE3NDEzODI4MjgsImV4cCI6MTc0MTM4NjQyOH0.tfSNB89xG5I9joUrQeG_EEZPscnflxyUZUwP60BVZwE";

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Define your adapters upfront
        RobotAdapter robotAdapter = new RobotAdapter(getContext(), robotList, taskList);
        TaskAdapter taskAdapter = new TaskAdapter(getContext(), taskList, robotList);

        robotRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        robotRecyclerView.setAdapter(robotAdapter);

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        taskRecyclerView.setAdapter(taskAdapter);

        // Load robot data
        JsonArrayRequest robotRequest = new JsonArrayRequest(Request.Method.GET, robotUrl, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            Robot robot = new Robot(response.getJSONObject(i));
                            robotList.add(robot);
                        }
                        robotAdapter.notifyDataSetChanged(); // Notify adapter after robot data is loaded
                        loadTasks(taskList, robotList, taskAdapter); // Now load task data after robot data
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
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

        // Stop refreshing when data is loaded
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadTasks(List<Task> taskList, List<Robot> robotList, TaskAdapter taskAdapter) {
        String taskUrl = "http://10.0.2.2:8080/api/robot/tasks";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJpYXQiOjE3NDEzODI4MjgsImV4cCI6MTc0MTM4NjQyOH0.tfSNB89xG5I9joUrQeG_EEZPscnflxyUZUwP60BVZwE";

        // Load task data
        JsonArrayRequest taskRequest = new JsonArrayRequest(Request.Method.GET, taskUrl, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            Task task = new Task(response.getJSONObject(i));
                            taskList.add(task);
                        }
                        taskAdapter.notifyDataSetChanged(); // Notify adapter after task data is loaded
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
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
