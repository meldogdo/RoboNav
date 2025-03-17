package com.robonav.app.adapters;

import static com.robonav.app.utilities.FragmentUtils.showMessage;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.robonav.app.R;
import com.robonav.app.interfaces.OnUpdateListener;
import com.robonav.app.models.Robot;
import com.robonav.app.models.Task;
import com.robonav.app.utilities.ConfigManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    private final List<Task> taskList;
    private final List<Robot> robotList;
    private final OnUpdateListener onUpdateListener;
    private final String token;

    public TaskAdapter(Context context, List<Task> taskList, List<Robot> robotList, OnUpdateListener onUpdateListener) {
        this.context = context;
        this.taskList = taskList;
        this.robotList = robotList;
        this.onUpdateListener = onUpdateListener;

        // Retrieve token from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        this.token = prefs.getString("JWT_TOKEN", null);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        List<Task> orderedTaskList = getOrderedTaskList();

        if (orderedTaskList.size() > position) {
            Task task = orderedTaskList.get(position);
            Robot responsibleRobot = getRobotForTask(task);

            holder.taskNameTextView.setText(task.getName());
            holder.taskRobotTextView.setText("Fulfilled By: " + (responsibleRobot != null ? responsibleRobot.getName() : "Unknown Robot"));
            String dateCreated = !Objects.equals(task.getDateCreated(), "null") ? task.getDateCreated() : "Unknown";
            holder.taskStartedTextView.setText("Started: " + dateCreated);

            // Handle task status and icon based on progress
            updateTaskStatus(holder, task);
            // Set click listener to show popup
            holder.itemView.setOnClickListener(view -> {showTaskPopup(view, task, responsibleRobot, position);});

        }
    }

    // Utility method to get the ordered task list
    private List<Task> getOrderedTaskList() {
        // Separate completed tasks
        List<Task> completedTasks = new ArrayList<>();
        for (Task task : taskList) {
            if ("2".equals(task.getState())) {
                completedTasks.add(task);
            }
        }

        // Sort completed tasks by dateCreated (newest first)
        completedTasks.sort((task1, task2) -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date1 = dateFormat.parse(task1.getEnd());
                Date date2 = dateFormat.parse(task2.getEnd());
                return date2.compareTo(date1);  // Sort in descending order
            } catch (ParseException e) {
                return 0;
            }
        });

        // Limit to the 5 most recent completed tasks
        completedTasks = completedTasks.size() > 5 ? completedTasks.subList(0, 5) : completedTasks;

        // Create a new list that includes all tasks, keeping the order of non-completed tasks
        List<Task> orderedTaskList = new ArrayList<>();

        // Add non-completed tasks in the same order as in the original taskList
        for (Task task : taskList) {
            if (!"2".equals(task.getState())) {
                orderedTaskList.add(task);
            }
        }

        // Add sorted completed tasks
        orderedTaskList.addAll(completedTasks);

        return orderedTaskList;
    }

    private void deleteTask(int taskId, int position) {
        String url = ConfigManager.getBaseUrl() + "/api/protected/robot/task/" + taskId + "/delete";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    try {
                        String message = response.getString("message");
                        showMessage(message, context);
                    } catch (JSONException e) {
                        showMessage("Task deleted successfully", context);
                    }
                    taskList.remove(position);
                    notifyItemRemoved(position);
                    if (onUpdateListener != null) {
                        onUpdateListener.onUpdate();
                    }
                },
                error -> {
                    try {
                        String errorMsg = new String(error.networkResponse.data);
                        JSONObject errorJson = new JSONObject(errorMsg);
                        String message = errorJson.getString("message");
                        showMessage(message, context);
                    } catch (Exception e) {
                        showMessage("Failed to delete task", context);
                    }
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    // Helper method to update task status
    private void updateTaskStatus(TaskViewHolder holder, Task task) {
        if (task.getState().equals("1")) {
            holder.taskProgressTextView.setText("Status: Active");
            holder.taskIconImageView.setImageResource(R.drawable.ic_active);
            holder.taskIconImageView.setVisibility(View.VISIBLE);
        } else if (task.getState().equals("0")) {
            holder.taskProgressTextView.setText("Status: Not Started");
            holder.taskIconImageView.setImageResource(R.drawable.ic_waiting);
            holder.taskIconImageView.setVisibility(View.VISIBLE);
        } else if (task.getState().equals("2")) {
            holder.taskProgressTextView.setText("Status: Complete");
            holder.taskIconImageView.setImageResource(R.drawable.ic_task);
            holder.taskIconImageView.setVisibility(View.VISIBLE);
        }else if (task.getState().equals("3")) {
            holder.taskProgressTextView.setText("Status: Stopped");
            holder.taskIconImageView.setImageResource(R.drawable.ic_paused);
            holder.taskIconImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return getOrderedTaskList().size();
    }

    private Robot getRobotForTask(Task task) {
        for (Robot robot : robotList) {
            if (robot.getId().equals(task.getRobotId())) {
                return robot;
            }
        }
        return null;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskStartedTextView;
        TextView taskNameTextView, taskRobotTextView, taskProgressTextView;
        ImageView taskIconImageView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.task_name);
            taskRobotTextView = itemView.findViewById(R.id.task_robot);
            taskProgressTextView = itemView.findViewById(R.id.task_progress);
            taskIconImageView = itemView.findViewById(R.id.task_icon);
            taskStartedTextView = itemView.findViewById(R.id.task_start);
        }
    }

    private void showTaskPopup(View anchorView, Task task, Robot responsibleRobot, int position) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.task_popup_layout, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        TextView titleView = popupView.findViewById(R.id.popup_title);
        ImageView swipeDownIcon = popupView.findViewById(R.id.swipe_down_icon);
        TextView endTitle = popupView.findViewById(R.id.end_time_title);
        TextView progressStatus = popupView.findViewById(R.id.progress_status);
        TextView endDate = popupView.findViewById(R.id.date_completed);
        TextView responsibleRobotView = popupView.findViewById(R.id.responsible_robot);
        TextView dateStarted = popupView.findViewById(R.id.date_started); // New TextView
        Button deleteButton = popupView.findViewById(R.id.delete_task_button);
        Button startButton = popupView.findViewById(R.id.start_task_button);
        Button stopButton = popupView.findViewById(R.id.stop_task_button);
        Button resumeButton = popupView.findViewById(R.id.resume_task_button); // New button for resuming

        // Ensure correct button visibility based on task state
        updateTaskPopupUI(task, startButton, stopButton, resumeButton, progressStatus);

        // Start Task Button Click Listener
        startButton.setOnClickListener(v -> {
            startTask(Integer.parseInt(task.getId()), startButton, stopButton, resumeButton, progressStatus);
            popupWindow.dismiss();
        });

        // Stop Task Button Click Listener
        stopButton.setOnClickListener(v -> {
            stopTask(Integer.parseInt(task.getId()), startButton, stopButton, resumeButton, progressStatus);
            popupWindow.dismiss();
        });

        // Resume Task Button Click Listener
        resumeButton.setOnClickListener(v -> {
            resumeTask(Integer.parseInt(task.getId()), startButton, stopButton, resumeButton, progressStatus);
            popupWindow.dismiss();
        });

        // Delete Button Click Listener
        deleteButton.setOnClickListener(v -> {
            deleteTask(Integer.parseInt(task.getId()), position);
            popupWindow.dismiss();
        });

        titleView.setText(task.getName());
        responsibleRobotView.setText((responsibleRobot != null ? responsibleRobot.getName() : "Unknown"));

        if (!"null".equals(task.getEnd())) {
            endTitle.setVisibility(View.VISIBLE);
            endDate.setText(task.getEnd());
            endDate.setVisibility(View.VISIBLE);
        } else {
            endTitle.setVisibility(View.GONE);
            endDate.setVisibility(View.GONE);
        }

        // Check if date is null and set it accordingly
        String dateCreated = !Objects.equals(task.getDateCreated(), "null") ? task.getDateCreated() : "Unknown";
        dateStarted.setText(dateCreated);

        // Handle task status
        if (task.getState().equals("1")) {
            progressStatus.setText("Active");
        } else {
            if (task.getState().equals("0")) {
                progressStatus.setText("Not Started");
            }
            else if (task.getState().equals("2")){
                progressStatus.setText("Complete");
            }
        }

        // Handle swipe-down icon click
        swipeDownIcon.setOnClickListener(v -> dismissWithAnimation(popupView, popupWindow));

        // Handle outside touch to dismiss with animation
        popupWindow.setTouchInterceptor((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (!isInsideViewBounds(popupView, event)) {
                    dismissWithAnimation(popupView, popupWindow);
                    return true; // Consume the touch event
                }
            }
            return false;
        });

        // Apply slide-up animation
        popupView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up));

        // Show the popup
        popupWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);
    }

    private void dismissWithAnimation(View popupView, PopupWindow popupWindow) {
        Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                popupWindow.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        popupView.startAnimation(slideDown);
    }

    private boolean isInsideViewBounds(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        float x = event.getRawX();
        float y = event.getRawY();
        return x >= location[0] && x <= location[0] + view.getWidth() &&
                y >= location[1] && y <= location[1] + view.getHeight();
    }

    private void startTask(int taskId, Button startTaskButton, Button stopTaskButton, Button resumeTaskButton, TextView progressStatus) {
        String url = ConfigManager.getBaseUrl() + "/api/protected/robot/task/" + taskId + "/start";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    try {
                        String message = response.getString("message");
                        showMessage(message, context);

                        // Update UI to reflect task has started
                        startTaskButton.setVisibility(View.GONE);
                        resumeTaskButton.setVisibility(View.GONE); // Hide Resume button if it was stopped before
                        stopTaskButton.setVisibility(View.VISIBLE);
                        progressStatus.setText("Active");

                        // Notify update listener if needed
                        if (onUpdateListener != null) {
                            onUpdateListener.onUpdate();
                        }

                    } catch (JSONException e) {
                        showMessage("Task started successfully", context);
                    }
                },
                error -> {
                    try {
                        String errorMsg = new String(error.networkResponse.data);
                        JSONObject errorJson = new JSONObject(errorMsg);
                        String message = errorJson.getString("message");
                        showMessage(message, context);
                    } catch (Exception e) {
                        showMessage("Failed to start task", context);
                    }
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private void stopTask(int taskId, Button startTaskButton, Button stopTaskButton, Button resumeTaskButton, TextView progressStatus) {
        String url = ConfigManager.getBaseUrl() + "/api/protected/robot/task/" + taskId + "/stop";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    try {
                        String message = response.getString("message");
                        showMessage(message, context);

                        // Update UI to reflect that the task is stopped
                        startTaskButton.setVisibility(View.GONE);
                        stopTaskButton.setVisibility(View.GONE);
                        resumeTaskButton.setVisibility(View.VISIBLE);
                        progressStatus.setText("Stopped");

                        // Notify update listener if needed
                        if (onUpdateListener != null) {
                            onUpdateListener.onUpdate();
                        }

                    } catch (JSONException e) {
                        showMessage("Task stopped successfully", context);
                    }
                },
                error -> {
                    try {
                        String errorMsg = new String(error.networkResponse.data);
                        JSONObject errorJson = new JSONObject(errorMsg);
                        String message = errorJson.getString("message");
                        showMessage(message, context);
                    } catch (Exception e) {
                        showMessage("Failed to stop task", context);
                    }
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private void resumeTask(int taskId, Button startTaskButton, Button stopTaskButton, Button resumeTaskButton, TextView progressStatus) {
        String url = ConfigManager.getBaseUrl() + "/api/protected/robot/task/" + taskId + "/resume";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    try {
                        String message = response.getString("message");
                        showMessage(message, context);

                        // Update UI to reflect that the task is resumed (active)
                        startTaskButton.setVisibility(View.GONE);
                        stopTaskButton.setVisibility(View.VISIBLE);
                        resumeTaskButton.setVisibility(View.GONE);
                        progressStatus.setText("Active");

                        // Notify update listener if needed
                        if (onUpdateListener != null) {
                            onUpdateListener.onUpdate();
                        }

                    } catch (JSONException e) {
                        showMessage("Task resumed successfully", context);
                    }
                },
                error -> {
                    try {
                        String errorMsg = new String(error.networkResponse.data);
                        JSONObject errorJson = new JSONObject(errorMsg);
                        String message = errorJson.getString("message");
                        showMessage(message, context);
                    } catch (Exception e) {
                        showMessage("Failed to resume task", context);
                    }
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private void updateTaskPopupUI(Task task, Button startButton, Button stopButton, Button resumeButton, TextView progressStatus) {
        switch (task.getState()) {
            case "0": // Not Started
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
                resumeButton.setVisibility(View.GONE);
                progressStatus.setText("Not Started");
                break;
            case "1": // Active
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
                resumeButton.setVisibility(View.GONE);
                progressStatus.setText("Active");
                break;
            case "2": // Completed
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.GONE);
                resumeButton.setVisibility(View.GONE);
                progressStatus.setText("Completed");
                break;
            case "3": // Stopped
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.GONE);
                resumeButton.setVisibility(View.VISIBLE);
                progressStatus.setText("Stopped");
                break;
        }
    }
}

