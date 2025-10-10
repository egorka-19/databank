package com.example.databank.UI.Users;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.databank.Prevalent.Prevalent;
import com.example.databank.R;
import com.example.databank.databinding.ActivityAchievementsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {
    private ActivityAchievementsBinding binding;
    private String phone;
    private Integer currentAmount = 0;
    private Integer targetAmount = 0;
    private final AchievementsAdapter adapter = new AchievementsAdapter();
    private final List<AchievementsAdapter.Achievement> items = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAchievementsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Достижения");

        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);

        phone = getIntent() != null ? getIntent().getStringExtra("phone") : null;
        if (phone == null || phone.isEmpty()) {
            Object storedPhone = io.paperdb.Paper.book().read(Prevalent.UserPhoneKey);
            if (storedPhone instanceof String) phone = (String) storedPhone;
        }

        // Seed base achievements with drawable-provided icons
        items.add(new AchievementsAdapter.Achievement("first_task", "Первая задача", "Выполнить 1 задачу", R.drawable.first_task, false));
        items.add(new AchievementsAdapter.Achievement("half_goal", "Упорная копилка", "Накопить 50% поставленной цели", R.drawable.second_task, false));
        items.add(new AchievementsAdapter.Achievement("goal_complete", "Цель достигнута!", "Достигнуть 100% поставленной цели", R.drawable.third_task, false));

        adapter.submit(new ArrayList<>(items));

        if (phone != null && !phone.isEmpty()) {
            readExistingStateAndBind();
        }
    }

    private void readExistingStateAndBind() {
        // Load existing achievement flags
        FirebaseDatabase.getInstance().getReference().child("Users").child(phone).child("achievements")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        for (AchievementsAdapter.Achievement a : items) {
                            Boolean done = snapshot.child(a.id).getValue(Boolean.class);
                            if (done != null) a.completed = done;
                        }
                        adapter.submit(new ArrayList<>(items));
                        subscribeRealtime();
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) { subscribeRealtime(); }
                });
    }

    private void subscribeRealtime() {
        // Track tasks for first_task
        FirebaseDatabase.getInstance().getReference().child("Users").child(phone).child("tasks")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        boolean hasCompleted = false;
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String status = String.valueOf(child.child("status").getValue());
                            if ("completed".equals(status)) { hasCompleted = true; break; }
                        }
                        setAchievement("first_task", hasCompleted);
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) { }
                });

        // Track savings/target for half_goal and goal_complete
        FirebaseDatabase.getInstance().getReference().child("Users").child(phone).child("savings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        Object v = snapshot.getValue();
                        currentAmount = valueToInt(v);
                        evaluateGoalAchievements();
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) { }
                });

        FirebaseDatabase.getInstance().getReference().child("Users").child(phone).child("targetAmount")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        Object v = snapshot.getValue();
                        targetAmount = valueToInt(v);
                        evaluateGoalAchievements();
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) { }
                });
    }

    private void evaluateGoalAchievements() {
        if (targetAmount == null || targetAmount <= 0) {
            setAchievement("half_goal", false);
            setAchievement("goal_complete", false);
            return;
        }
        double ratio = currentAmount * 1.0 / targetAmount;
        setAchievement("half_goal", ratio >= 0.5);
        setAchievement("goal_complete", currentAmount >= targetAmount);
    }

    private void setAchievement(String id, boolean completed) {
        for (AchievementsAdapter.Achievement a : items) {
            if (a.id.equals(id)) {
                boolean changed = a.completed != completed;
                a.completed = completed;
                if (changed) {
                    // persist once changed
                    if (phone != null && !phone.isEmpty()) {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(phone)
                                .child("achievements").child(id).setValue(completed);
                    }
                    adapter.submit(new ArrayList<>(items));
                }
                break;
            }
        }
    }

    private int valueToInt(Object v) {
        if (v instanceof Long) return ((Long) v).intValue();
        if (v instanceof Integer) return (Integer) v;
        return 0;
    }
}


