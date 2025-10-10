package com.example.databank.UI.Users;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databank.R;
import com.example.databank.data.TasksRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParentTasksActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ParentTasksAdapter adapter;
    private String childPhone;
    private final TasksRepository tasksRepository = new TasksRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_tasks);

        recyclerView = findViewById(R.id.tasks_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        childPhone = getIntent().getStringExtra("childPhone");
        if (TextUtils.isEmpty(childPhone)) {
            Toast.makeText(this, "Не указан номер ребенка", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        adapter = new ParentTasksAdapter(new ArrayList<>(), new ParentTasksAdapter.TaskActionListener() {
            @Override
            public void onApprove(String taskId, int reward) {
                tasksRepository.approveTask(childPhone, taskId);
            }

            @Override
            public void onReject(String taskId) {
                tasksRepository.rejectTask(childPhone, taskId, "На доработку");
            }
        });
        recyclerView.setAdapter(adapter);

        loadTasks();
    }

    private void loadTasks() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(childPhone).child("tasks")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Map<String, Object>> tasks = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Object status = child.child("status").getValue();
                            String s = status == null ? null : String.valueOf(status);
                            if ("pending_review".equals(s)) {
                                tasks.add((Map<String, Object>) child.getValue());
                            }
                        }
                        adapter.submit(tasks);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}


