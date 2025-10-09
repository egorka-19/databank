package com.example.databank.UI.Users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.databank.Model.Users;
import com.example.databank.Prevalent.Prevalent;
import com.example.databank.R;
import com.example.databank.UI.LoginActivity;
import com.example.databank.UI.RegisterActivity;
import com.example.databank.UI.Users.views.RoundedRevealView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loadingBar = new ProgressDialog(this);

        Paper.init(this);
        Button parentBtn = findViewById(R.id.button_parent);
        Button childBtn = findViewById(R.id.button_child);
        RoundedRevealView roundedOverlay = findViewById(R.id.rounded_overlay);

        View.OnClickListener selectRole = v -> {
            String parentDbName = (v.getId() == R.id.button_parent) ? "Admins" : "Users";
            Button btn = (Button) v;

            if (roundedOverlay != null) {
                roundedOverlay.setVisibility(View.VISIBLE);
                if (v.getId() == R.id.button_parent) {
                    roundedOverlay.revealFromBottomLeft();
                } else {
                    roundedOverlay.revealFromBottomRight();
                }
            }

            new Handler().postDelayed(() -> {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.putExtra("userType", parentDbName.equals("Admins") ? "parent" : "child");
                startActivity(i);
                finish();
            }, 2000);
        };

        if (parentBtn != null) parentBtn.setOnClickListener(selectRole);
        if (childBtn != null) childBtn.setOnClickListener(selectRole);

        // Отключаем авто-вход на этом экране: переход выполняется только после выбора роли

    }

    private void ValidateUser(String phone, String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(phone).exists()){
                    Users userData = snapshot.child("Users").child(phone).getValue(Users.class);
                    if (userData.getPhone().equals(phone)){
                        if (userData.getPassword().equals(password)){
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(homeIntent);

                        }
                        else{
                            loadingBar.dismiss();
                        }
                    }


                }
                else {
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Aккаунт с номером " + phone + " не существует", Toast.LENGTH_SHORT).show();
                    Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}