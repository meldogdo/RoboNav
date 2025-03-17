package com.robonav.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.robonav.app.R;
import com.robonav.app.activities.CreateRobotActivity;
import com.robonav.app.activities.CreateTaskActivity;
import com.robonav.app.adapters.RobotAdapter;
import com.robonav.app.adapters.TaskAdapter;
import com.robonav.app.interfaces.OnUpdateListener;
import com.robonav.app.models.Robot;
import com.robonav.app.models.Task;
import com.robonav.app.utilities.ConfigManager;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements OnUpdateListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView robotRecyclerView, taskRecyclerView;
    private RobotAdapter robotAdapter;
    private TaskAdapter taskAdapter;
    private List<Robot> robotList;
    private List<Task> taskList;
    private String token;
    private Button createRobotButton, createTaskButton;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            onUpdate(); // Refresh HomeFragment when a robot is created
        }
        if (requestCode == 2 && resultCode == getActivity().RESULT_OK) {
            onUpdate(); // Refresh HomeFragment when a task is created
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize SwipeRefreshLayout and RecyclerViews
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        robotRecyclerView = rootView.findViewById(R.id.robot_recycler_view);
        taskRecyclerView = rootView.findViewById(R.id.task_recycler_view);

        // Find buttons in layout
        createRobotButton = rootView.findViewById(R.id.createRobotButton);
        createTaskButton = rootView.findViewById(R.id.createTaskButton);

        // Click listener for Create Robot button
        createRobotButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateRobotActivity.class);
            startActivityForResult(intent, 1);
        });

        // Click listener for Create Task button
        createTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateTaskActivity.class);
            startActivityForResult(intent, 2);
        });

        // Initialize lists
        robotList = new ArrayList<>();
        taskList = new ArrayList<>();

        // Define adapters
        robotAdapter = new RobotAdapter(getContext(), robotList, taskList, this);
        taskAdapter = new TaskAdapter(getContext(), taskList, robotList, this);

        robotRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        robotRecyclerView.setAdapter(robotAdapter);

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        taskRecyclerView.setAdapter(taskAdapter);

        // Set swipe refresh listener to reload data
        swipeRefreshLayout.setOnRefreshListener(this::loadData);

        SharedPreferences prefs = requireContext().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        token = prefs.getString("JWT_TOKEN", null);

        return rootView;
    }

    private void loadData() {
        // Clear the lists before loading new data
        robotList.clear();
        taskList.clear();

        // Notify adapters that the data is being cleared
        robotAdapter.notifyDataSetChanged();
        taskAdapter.notifyDataSetChanged();

        String robotUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/robots";
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
                        loadTasks();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },
                error -> {
                    error.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(robotRequest);
        swipeRefreshLayout.setRefreshing(true);
    }

    private void loadTasks() {
        String taskUrl = ConfigManager.getBaseUrl() + "/api/protected/robot/tasks";
        JsonArrayRequest taskRequest = new JsonArrayRequest(Request.Method.GET, taskUrl, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            Task task = new Task(response.getJSONObject(i));
                            taskList.add(task);
                        }
                        taskAdapter.notifyDataSetChanged();
                        robotAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },
                error -> {
                    error.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(taskRequest);
    }

    // Implement the generic update callback
    @Override
    public void onUpdate() {
        loadData(); // Refresh the entire fragment
    }
}
