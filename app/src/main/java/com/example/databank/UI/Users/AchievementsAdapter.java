package com.example.databank.UI.Users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databank.R;

import java.util.ArrayList;
import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.Holder> {
    static class Achievement {
        final String id;           // key for persistence
        final String title;
        final String description;
        final int iconResId;       // placeholder, user can replace
        boolean completed;

        Achievement(String id, String title, String description, int iconResId, boolean completed) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.iconResId = iconResId;
            this.completed = completed;
        }
    }

    private final List<Achievement> items = new ArrayList<>();

    void submit(List<Achievement> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Achievement a = items.get(position);
        holder.title.setText(a.title);
        holder.desc.setText(a.description);
        holder.icon.setImageResource(a.iconResId);
        holder.status.setText(a.completed ? "Выполнено" : "Не выполнено");
        holder.status.setTextColor(a.completed ? 0xFF1B5E20 : 0xFFB71C1C);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView title;
        final TextView desc;
        final TextView status;

        Holder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.description);
            status = itemView.findViewById(R.id.status);
        }
    }
}


