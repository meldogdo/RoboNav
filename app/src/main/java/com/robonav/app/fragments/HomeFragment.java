package com.robonav.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robonav.app.utilities.JsonUtils;
import com.robonav.app.R;
import com.robonav.app.models.Robot;
import com.robonav.app.adapters.RobotAdapter;
import com.robonav.app.models.Task;
import com.robonav.app.adapters.TaskAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/** @noinspection CallToPrintStackTrace*/
public class HomeFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Robot RecyclerView setup
        RecyclerView robotRecyclerView = rootView.findViewById(R.id.robot_recycler_view);
        String robotJson = JsonUtils.loadJSONFromAsset(requireContext(), "robots.json");
        List<Robot> robotList = new ArrayList<>();
        try {
            JSONArray robots = new JSONArray(robotJson);
            for (int i = 0; i < robots.length(); i++) {
                robotList.add(new Robot(robots.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Task RecyclerView setup
        RecyclerView taskRecyclerView = rootView.findViewById(R.id.task_recycler_view);
        String taskJson = JsonUtils.loadJSONFromAsset(requireContext(), "tasks.json");
        List<Task> taskList = new ArrayList<>();
        try {
            JSONArray tasks = new JSONArray(taskJson);
            for (int i = 0; i < tasks.length(); i++) {
                taskList.add(new Task(tasks.getJSONObject(i))); // Assuming `ic_home` is the placeholder task icon
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TaskAdapter taskAdapter = new TaskAdapter(getContext(), taskList, robotList);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        taskRecyclerView.setAdapter(taskAdapter);

        RobotAdapter robotAdapter = new RobotAdapter(getContext(), robotList, taskList);
        robotRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        robotRecyclerView.setAdapter(robotAdapter);
        return rootView;
    }
}
