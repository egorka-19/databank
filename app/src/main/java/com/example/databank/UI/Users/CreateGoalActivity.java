package com.example.databank.UI.Users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.databank.databinding.ActivityCreateGoalBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateGoalActivity extends AppCompatActivity {
    private ActivityCreateGoalBinding binding;
    private ProgressDialog loadingBar;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Получаем номер телефона из Intent
        phone = getIntent().getStringExtra("phone");
        if (phone == null || phone.isEmpty()) {
            Toast.makeText(this, "Ошибка: не получен номер телефона", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadingBar = new ProgressDialog(this);

        binding.createGoalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGoal();
            }
        });
    }

    private void createGoal() {
        String goalName = binding.goalNameInput.getText().toString().trim();
        String goalDescription = binding.goalDescriptionInput.getText().toString().trim();
        String deadline = binding.deadlineInput.getText().toString().trim();
        String amountStr = binding.amountInput.getText().toString().trim();

        // Валидация полей
        if (TextUtils.isEmpty(goalName)) {
            Toast.makeText(this, "Введите название цели", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(goalDescription)) {
            Toast.makeText(this, "Введите описание цели", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(deadline)) {
            Toast.makeText(this, "Введите срок выполнения", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Введите сумму для накопления", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверяем, что сумма - это число
        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "Сумма должна быть больше 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Введите корректную сумму", Toast.LENGTH_SHORT).show();
            return;
        }

        // Показываем индикатор загрузки
        loadingBar.setTitle("Создание цели");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // Обновляем данные пользователя в базе данных
        updateUserGoal(goalName, goalDescription, deadline, amount);
    }

    private void updateUserGoal(String goalName, String goalDescription, String deadline, int amount) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);

        HashMap<String, Object> goalData = new HashMap<>();
        goalData.put("goalName", goalName);
        goalData.put("goalDescription", goalDescription);
        goalData.put("deadline", deadline);
        goalData.put("targetAmount", amount);
        goalData.put("savings", 0); // Начальные сбережения

        userRef.updateChildren(goalData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                
                if (task.isSuccessful()) {
                    Toast.makeText(CreateGoalActivity.this, "Цель создана успешно!", Toast.LENGTH_SHORT).show();
                    
                    // Переходим на главный экран
                    Intent homeIntent = new Intent(CreateGoalActivity.this, HomeActivity.class);
                    homeIntent.putExtra("phone", phone);
                    startActivity(homeIntent);
                    finish();
                } else {
                    Toast.makeText(CreateGoalActivity.this, "Ошибка создания цели", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
