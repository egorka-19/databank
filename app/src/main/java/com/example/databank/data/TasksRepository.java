package com.example.databank.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.databank.Model.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;
import java.util.Map;

public class TasksRepository {
    private final DatabaseReference root;

    public TasksRepository() {
        this.root = FirebaseDatabase.getInstance().getReference();
    }

    public void assignTask(String childPhone, String title, String category, int reward) {
        DatabaseReference tasksRef = root.child("Users").child(childPhone).child("tasks");
        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long nextNumber = snapshot.getChildrenCount() + 1;
                DatabaseReference newTaskRef = tasksRef.push();
                Map<String, Object> data = new HashMap<>();
                data.put("id", newTaskRef.getKey());
                data.put("taskNumber", nextNumber);
                data.put("childPhone", childPhone);
                data.put("title", title);
                data.put("category", category);
                data.put("reward", reward);
                data.put("status", "in_progress");
                data.put("createdAt", System.currentTimeMillis());
                data.put("updatedAt", System.currentTimeMillis());
                newTaskRef.setValue(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void submitForReview(String childPhone, String taskId, String imageUrl) {
        DatabaseReference taskRef = root.child("Users").child(childPhone).child("tasks").child(taskId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "pending_review");
        updates.put("imageUrl", imageUrl);
        updates.put("updatedAt", System.currentTimeMillis());
        taskRef.updateChildren(updates);
    }

    public void approveTask(String childPhone, String taskId, int reward) {
        DatabaseReference userRef = root.child("Users").child(childPhone);
        DatabaseReference taskRef = userRef.child("tasks").child(taskId);

        // Set status completed
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "completed");
        updates.put("updatedAt", System.currentTimeMillis());
        taskRef.updateChildren(updates);

        // Increase savings atomically via transaction
        userRef.child("savings").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer current = 0;
                Object value = currentData.getValue();
                if (value instanceof Long) current = ((Long) value).intValue();
                else if (value instanceof Integer) current = (Integer) value;
                currentData.setValue(current + reward);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
            }
        });
    }

    public void approveTask(String childPhone, String taskId) {
        DatabaseReference userRef = root.child("Users").child(childPhone);
        DatabaseReference taskRef = userRef.child("tasks").child(taskId);

        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer reward = 0;
                Object r = snapshot.child("reward").getValue();
                if (r instanceof Long) reward = ((Long) r).intValue();
                else if (r instanceof Integer) reward = (Integer) r;

                // Set status to completed
                Map<String, Object> updates = new HashMap<>();
                updates.put("status", "completed");
                updates.put("updatedAt", System.currentTimeMillis());
                taskRef.updateChildren(updates);

                // Atomically add reward to savings
                DatabaseReference savingsRef = userRef.child("savings");
                final int inc = reward != null ? reward : 0;
                savingsRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        Integer current = 0;
                        Object value = currentData.getValue();
                        if (value instanceof Long) current = ((Long) value).intValue();
                        else if (value instanceof Integer) current = (Integer) value;
                        currentData.setValue(current + inc);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void rejectTask(String childPhone, String taskId, String reason) {
        DatabaseReference taskRef = root.child("Users").child(childPhone).child("tasks").child(taskId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "rejected");
        updates.put("rejectionReason", reason);
        updates.put("updatedAt", System.currentTimeMillis());
        taskRef.updateChildren(updates);
    }
}


