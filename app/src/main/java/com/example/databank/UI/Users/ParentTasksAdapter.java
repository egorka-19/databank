package com.example.databank.UI.Users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databank.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParentTasksAdapter extends RecyclerView.Adapter<ParentTasksAdapter.ViewHolder> {

    public interface TaskActionListener {
        void onApprove(String taskId, int reward);
        void onReject(String taskId);
    }

    private final List<Map<String, Object>> items;
    private final TaskActionListener listener;

    public ParentTasksAdapter(List<Map<String, Object>> items, TaskActionListener listener) {
        this.items = items == null ? new ArrayList<>() : items;
        this.listener = listener;
    }

    public void submit(List<Map<String, Object>> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent_task, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> item = items.get(position);
        String title = String.valueOf(item.get("title"));
        Object rewardObj = item.get("reward");
        int reward = 0;
        if (rewardObj instanceof Long) reward = ((Long) rewardObj).intValue();
        else if (rewardObj instanceof Integer) reward = (Integer) rewardObj;
        String status = String.valueOf(item.get("status"));
        String id = String.valueOf(item.get("id"));

        holder.taskTitle.setText(title);
        holder.taskReward.setText("Вознаграждение: " + reward + " ₽");
        holder.taskStatus.setText(status);

        final int finalReward = reward;
        holder.approveBtn.setOnClickListener(v -> listener.onApprove(id, finalReward));
        holder.rejectBtn.setOnClickListener(v -> listener.onReject(id));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskReward, taskStatus;
        Button approveBtn, rejectBtn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskReward = itemView.findViewById(R.id.task_reward);
            taskStatus = itemView.findViewById(R.id.task_status);
            approveBtn = itemView.findViewById(R.id.approve_btn);
            rejectBtn = itemView.findViewById(R.id.reject_btn);
        }
    }
}


