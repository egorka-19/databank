package com.example.databank.UI.Users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databank.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static class Message {
        final boolean isUser;
        final String text;

        Message(boolean isUser, String text) {
            this.isUser = isUser;
            this.text = text;
        }

        static Message user(String t) { return new Message(true, t); }
        static Message assistant(String t) { return new Message(false, t); }
    }

    private final List<Message> items = new ArrayList<>();
    private static final int TYPE_USER = 1;
    private static final int TYPE_ASSISTANT = 2;

    void add(Message m) {
        items.add(m);
        notifyItemInserted(items.size() - 1);
    }

    @Override public int getItemViewType(int position) {
        return items.get(position).isUser ? TYPE_USER : TYPE_ASSISTANT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
            return new UserHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_assistant, parent, false);
            return new AssistantHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message m = items.get(position);
        if (holder instanceof UserHolder) ((UserHolder) holder).text.setText(m.text);
        else if (holder instanceof AssistantHolder) ((AssistantHolder) holder).text.setText(m.text);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class UserHolder extends RecyclerView.ViewHolder {
        final TextView text;
        UserHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.message_text);
        }
    }

    static class AssistantHolder extends RecyclerView.ViewHolder {
        final TextView text;
        AssistantHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.message_text);
        }
    }
}


