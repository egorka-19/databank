package com.example.databank.UI.Users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.databank.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WithdrawActivity extends AppCompatActivity {
    private String phone;
    private TextView savingsText;
    private Button withdrawBtn;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        phone = getIntent().getStringExtra("phone");
        if (phone == null || phone.isEmpty()) {
            Toast.makeText(this, "Ошибка: не получен номер телефона", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadingBar = new ProgressDialog(this);
        savingsText = findViewById(R.id.savings_value);
        withdrawBtn = findViewById(R.id.withdraw_btn);

        loadSavings();

        withdrawBtn.setOnClickListener(v -> performWithdraw());
    }

    private void loadSavings() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);
        userRef.child("savings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int savings = 0;
                Object value = snapshot.getValue();
                if (value instanceof Integer) savings = (Integer) value;
                else if (value instanceof Long) savings = ((Long) value).intValue();
                else if (value instanceof String) {
                    try { savings = Integer.parseInt((String) value); } catch (NumberFormatException ignored) {}
                }
                savingsText.setText(String.valueOf(savings));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WithdrawActivity.this, "Ошибка загрузки: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performWithdraw() {
        loadingBar.setTitle("Вывод средств");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);

        // Обнуляем savings и текущую цель
        java.util.HashMap<String, Object> updates = new java.util.HashMap<>();
        updates.put("savings", 0);
        updates.put("goalName", "");
        updates.put("goalDescription", "");
        updates.put("deadline", "");
        updates.put("targetAmount", 0);

        userRef.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(WithdrawActivity.this, "Вывод выполнен. Цель сброшена.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(WithdrawActivity.this, CreateGoalActivity.class);
                    i.putExtra("phone", phone);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(WithdrawActivity.this, "Ошибка: не удалось выполнить вывод", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


