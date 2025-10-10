package com.example.databank.UI.Users;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class TopUpBalanceActivity extends AppCompatActivity {
    private String phone;
    private TextView currentBalanceTv;
    private EditText amountEt;
    private Button topupBtn;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_balance);

        phone = getIntent().getStringExtra("phone");
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Ошибка: не получен номер телефона", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentBalanceTv = findViewById(R.id.current_balance_tv);
        amountEt = findViewById(R.id.amount_input);
        topupBtn = findViewById(R.id.topup_btn);
        loadingBar = new ProgressDialog(this);

        loadCurrentBalance();

        topupBtn.setOnClickListener(v -> {
            String amountStr = amountEt.getText().toString().trim();
            if (TextUtils.isEmpty(amountStr)) {
                Toast.makeText(this, "Введите сумму пополнения", Toast.LENGTH_SHORT).show();
                return;
            }
            int amount = 0;
            try { amount = Integer.parseInt(amountStr); } catch (NumberFormatException ignored) {}
            if (amount <= 0) {
                Toast.makeText(this, "Сумма должна быть больше 0", Toast.LENGTH_SHORT).show();
                return;
            }

            performTopUp(amount);
        });
    }

    private void loadCurrentBalance() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(phone).child("balance")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int bal = 0;
                        Object v = snapshot.getValue();
                        if (v instanceof Long) bal = ((Long) v).intValue();
                        else if (v instanceof Integer) bal = (Integer) v;
                        else if (v instanceof String) { try { bal = Integer.parseInt((String) v); } catch (Exception ignored) {} }
                        currentBalanceTv.setText(String.valueOf(bal) + " ₽");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(TopUpBalanceActivity.this, "Ошибка: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void performTopUp(int amount) {
        loadingBar.setTitle("Пополнение");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        DatabaseReference balanceRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phone).child("balance");
        final int inc = amount;
        balanceRef.runTransaction(new Transaction.Handler() {
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
            public void onComplete(@androidx.annotation.Nullable DatabaseError error, boolean committed, @androidx.annotation.Nullable DataSnapshot currentData) {
                loadingBar.dismiss();
                if (error == null) {
                    Toast.makeText(TopUpBalanceActivity.this, "Баланс пополнен", Toast.LENGTH_SHORT).show();
                    loadCurrentBalance();
                } else {
                    Toast.makeText(TopUpBalanceActivity.this, "Ошибка: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


