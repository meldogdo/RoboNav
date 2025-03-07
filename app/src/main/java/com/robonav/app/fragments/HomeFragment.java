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

    private List<Robot> robotList;
    private List<Task> taskList;
    private RobotAdapter robotAdapter;
    private TaskAdapter taskAdapter;
    private RecyclerView robotRecyclerView;
    private RecyclerView taskRecyclerView;
    private RequestQueue queue;

    private static final String ROBOT_URL = "http://10.0.2.2:8080/api/robot/robots";
    private static final String TASK_URL = "http://10.0.2.2:8080/api/robot/tasks";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJpYXQiOjE3NDEzNzkwNTMsImV4cCI6MTc0MTM4MjY1M30.7wL0pMLwwtXESWO7rrC5BZRqyt0z9zdJQQGUABn_MJs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        robotRecyclerView = rootView.findViewById(R.id.robot_recycler_view);
        taskRecyclerView = rootView.findViewById(R.id.task_recycler_view);

        // Initialize the Volley request queue
        queue = Volley.newRequestQueue(requireContext());

        // Set swipe refresh listener to reload data
        swipeRefreshLayout.setOnRefreshListener(() -> {
            reloadFragment();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Initial data loading
        reloadFragment();

        return rootView;
    }

    private void reloadFragment() {
        // Clear the existing data
        robotList = new ArrayList<>();
        taskList = new ArrayList<>();

        // Recreate adapters
        robotAdapter = new RobotAdapter(getContext(), robotList, taskList);
        taskAdapter = new TaskAdapter(getContext(), taskList, robotList);

        // Reinitialize RecyclerViews
        robotRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        robotRecyclerView.setAdapter(robotAdapter);

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        taskRecyclerView.setAdapter(taskAdapter);

        // Load data again
        loadRobotData();
        loadTaskData();
    }

    private void loadRobotData() {
        JsonArrayRequest robotRequest = new JsonArrayRequest(Request.Method.GET, ROBOT_URL, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            Robot robot = new Robot(response.getJSONObject(i));
                            robotList.add(robot);
                        }
                        robotAdapter.notifyItemRangeInserted(0, robotList.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + TOKEN);
                return headers;
            }
        };
        queue.add(robotRequest);
    }

    private void loadTaskData() {
        JsonArrayRequest taskRequest = new JsonArrayRequest(Request.Method.GET, TASK_URL, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            Task task = new Task(response.getJSONObject(i));
                            taskList.add(task);
                        }
                        taskAdapter.notifyItemRangeInserted(0, taskList.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + TOKEN);
                return headers;
            }
        };
        queue.add(taskRequest);
    }
}
