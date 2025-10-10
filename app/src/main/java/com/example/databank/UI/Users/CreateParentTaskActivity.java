package com.example.databank.UI.Users;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.databank.R;
import com.example.databank.data.TasksRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateParentTaskActivity extends AppCompatActivity {
    private EditText titleEt, categoryEt, rewardEt;
    private String parentPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_parent_task);

        parentPhone = getIntent().getStringExtra("phone");
        titleEt = findViewById(R.id.title_input);
        categoryEt = findViewById(R.id.category_input);
        rewardEt = findViewById(R.id.reward_input);
        Button createBtn = findViewById(R.id.create_btn);

        createBtn.setOnClickListener(v -> {
            String title = titleEt.getText().toString().trim();
            String category = categoryEt.getText().toString().trim();
            String rewardStr = rewardEt.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "Введите название задачи", Toast.LENGTH_SHORT).show();
                return;
            }
            int reward = 0;
            try { reward = Integer.parseInt(rewardStr); } catch (Exception ignored) { }

            final String fTitle = title;
            final String fCategory = category;
            final int fReward = reward;

            if (TextUtils.isEmpty(parentPhone)) {
                Toast.makeText(this, "Не удалось определить аккаунт", Toast.LENGTH_LONG).show();
                return;
            }

            FirebaseDatabase.getInstance().getReference().child("Users").child(parentPhone)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            String childPhone = snapshot.child("childPhone").getValue(String.class);
                            if (TextUtils.isEmpty(childPhone)) {
                                Toast.makeText(CreateParentTaskActivity.this, "Ребёнок не привязан", Toast.LENGTH_LONG).show();
                                return;
                            }
                            new TasksRepository().assignTask(childPhone, fTitle, fCategory, fReward);
                            Toast.makeText(CreateParentTaskActivity.this, "Задача создана", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) { }
                    });
        });
    }
}

 
