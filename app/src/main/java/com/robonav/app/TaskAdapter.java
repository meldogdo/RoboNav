package com.robonav.app;

import static com.robonav.app.Task.getRobotForTask;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    private final List<Task> taskList;

    private final List<Robot> robotList;


    public TaskAdapter(Context context, List<Task> taskList,List<Robot> robotList) {
        this.context = context;
        this.taskList = taskList;
        this.robotList = robotList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Bind data to the task card
        holder.taskNameTextView.setText(task.getName());
        holder.taskRobotTextView.setText("Robot: " + getRobotForTask(task, robotList).getName());
        holder.taskProgressTextView.setText("Progress: " + task.getProgress() + "%");

        if (task.getProgress() >= 0) {
            // Show progress bar and set progress
            holder.taskProgressTextView.setText("Progress: " + task.getProgress() + "%");
            holder.taskProgressBar.setIndeterminate(false);
            holder.taskProgressBar.setVisibility(View.VISIBLE);
            holder.taskProgressBar.setProgress(task.getProgress());
            holder.taskIconImageView.setVisibility(View.GONE);
        } else {
            holder.taskProgressBar.setVisibility(View.GONE);
            if (task.getProgress() == -1) {
                holder.taskProgressTextView.setText("Status: Stopped");
                holder.taskIconImageView.setVisibility(View.VISIBLE);
                holder.taskIconImageView.setImageResource(R.drawable.ic_error);
            } else if (task.getProgress() == -2) {
                holder.taskProgressTextView.setText("Status: Queued");
                holder.taskIconImageView.setVisibility(View.VISIBLE);
                holder.taskIconImageView.setImageResource(R.drawable.ic_queue);
            }
        }

        // Set click listener to show popup
        holder.itemView.setOnClickListener(view -> showTaskPopup(view, task));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // ViewHolder class
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTextView, taskRobotTextView, taskProgressTextView;
        ProgressBar taskProgressBar;
        ImageView taskIconImageView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.task_name);
            taskRobotTextView = itemView.findViewById(R.id.task_robot);
            taskProgressTextView = itemView.findViewById(R.id.task_progress);
            taskProgressBar = itemView.findViewById(R.id.task_progress_bar);
            taskIconImageView = itemView.findViewById(R.id.task_icon);
        }
    }


    // Show popup method with animations
    private void showTaskPopup(View anchorView, Task task) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.task_popup_layout, null);

        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        // Set content for the popup
        TextView titleView = popupView.findViewById(R.id.popup_title);
        ProgressBar progressBar = popupView.findViewById(R.id.progress_bar);
        TextView progressStatus = popupView.findViewById(R.id.progress_status);
        TextView expectedEndTimeView = popupView.findViewById(R.id.expected_end_time);
        TextView responsibleRobotView = popupView.findViewById(R.id.responsible_robot);
        ImageView swipeDownIcon = popupView.findViewById(R.id.swipe_down_icon);

        // Bind data
        titleView.setText(task.getName());
        progressBar.setProgress(task.getProgress());
        progressStatus.setText("Progress: " + task.getProgress() + "%");
        expectedEndTimeView.setText("Expected End Time: " + "task.getExpectedEndTime()");
        responsibleRobotView.setText("Completed By: " + "ms ksnsk");

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

    // Dismiss popup with slide-down animation
    private void dismissWithAnimation(View popupView, PopupWindow popupWindow) {
        Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                popupWindow.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        popupView.startAnimation(slideDown);
    }

    // Check if touch event is inside popup bounds
    private boolean isInsideViewBounds(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        float x = event.getRawX();
        float y = event.getRawY();
        return x >= location[0] && x <= location[0] + view.getWidth() &&
                y >= location[1] && y <= location[1] + view.getHeight();
    }
}
