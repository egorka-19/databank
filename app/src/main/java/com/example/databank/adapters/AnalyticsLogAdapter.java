package com.example.databank.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databank.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnalyticsLogAdapter extends RecyclerView.Adapter<AnalyticsLogAdapter.Holder> {
    public static class LogItem {
        public final String title;       // "Пополнение" или "Списание"
        public final String subtitle;    // источник: задача, вывод средств и т.п.
        public final int amount;         // +/− сумма в ₽
        public final long timestamp;     // millis

        public LogItem(String title, String subtitle, int amount, long timestamp) {
            this.title = title;
            this.subtitle = subtitle;
            this.amount = amount;
            this.timestamp = timestamp;
        }
    }

    private final List<LogItem> items = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public void submitList(List<LogItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analytics_log, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        LogItem item = items.get(position);
        holder.title.setText(item.title);
        holder.subtitle.setText(item.subtitle);
        String sign = item.amount >= 0 ? "+" : "";
        holder.amount.setText(sign + item.amount + " ₽");
        holder.amount.setTextColor(item.amount >= 0 ? 0xFF1B5E20 : 0xFFB71C1C);
        holder.date.setText(dateFormat.format(new Date(item.timestamp)));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;
        final TextView amount;
        final TextView date;

        Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
        }
    }
}


