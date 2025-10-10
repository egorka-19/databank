package com.example.databank.UI.Users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.databank.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WithdrawalActivity extends AppCompatActivity {
    private String phone;
    private TextView amountText;
    private Button withdrawBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        phone = getIntent().getStringExtra("phone");
        amountText = findViewById(R.id.available_amount);
        withdrawBtn = findViewById(R.id.withdraw_now_btn);

        DatabaseReference savingsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phone).child("savings");
        savingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int amount = 0;
                Object v = snapshot.getValue();
                if (v instanceof Long) amount = ((Long) v).intValue();
                else if (v instanceof Integer) amount = (Integer) v;
                amountText.setText("Доступно к выводу: " + amount + " ₽");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        withdrawBtn.setOnClickListener(v -> {
            // reset savings and go to create goal
            FirebaseDatabase.getInstance().getReference().child("Users").child(phone).child("savings").setValue(0);
            Intent i = new Intent(WithdrawalActivity.this, CreateGoalActivity.class);
            i.putExtra("phone", phone);
            startActivity(i);
            finish();
        });
    }
}


