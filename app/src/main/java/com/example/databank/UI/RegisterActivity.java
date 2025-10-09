package com.example.databank.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    public ImageButton registerBtn, swipeButton;
    public EditText usernameInput, phoneInput, passwordInput, lastnameInput, ageInput;
    private ProgressDialog loadingBar;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        swipeButton = (ImageButton)findViewById(R.id.swipe_btn);
        registerBtn = (ImageButton)findViewById(R.id.login_button);
        usernameInput = (EditText)findViewById(R.id.register_username_input);
        phoneInput = (EditText)findViewById(R.id.login_phone_input);
        passwordInput = (EditText)findViewById(R.id.login_password_input);
        lastnameInput = (EditText)findViewById(R.id.register_lastname_input);
        ageInput = (EditText)findViewById(R.id.register_age_input);
        loadingBar = new ProgressDialog(this);

        // Получаем тип пользователя из Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userType")) {
            userType = intent.getStringExtra("userType");
            System.out.println("Тип пользователя для регистрации: " + userType);
        } else {
            // По умолчанию создаем ребенка
            userType = "child";
        }

        swipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                if (userType != null) {
                    homeIntent.putExtra("userType", userType);
                }
                startActivity(homeIntent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String username = usernameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String password = passwordInput.getText().toString();
        String lastname = lastnameInput.getText().toString();
        String age = ageInput.getText().toString();
        String savings = "0";
        String targetAmount = "0";
        String profileImage = "";

        if (TextUtils.isEmpty(username)){
            Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(lastname)){
            Toast.makeText(this, "Введите фамилию", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Введите номер карты", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(age)){
            Toast.makeText(this, "Введите возраст", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Создание аккаунта");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhone(username, phone, password, profileImage, lastname, age, savings, targetAmount);
        }
    }

    private void ValidatePhone(String username, String phone, String password, String profileImage, String age, String lastname, String savings, String targetAmount) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("Users").child(phone).exists())){
                    HashMap <String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone);
                    userDataMap.put("age", age);
                    userDataMap.put("savings", savings);
                    userDataMap.put("targetAmount", targetAmount);
                    userDataMap.put("lastname", lastname);
                    userDataMap.put("username", username);
                    userDataMap.put("password", password);
                    userDataMap.put("profileImage", profileImage);
                    userDataMap.put("userType", userType);

                    RootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                                        Intent loginIntent = new Intent (RegisterActivity.this, LoginActivity.class);
                                        if (userType != null) {
                                            loginIntent.putExtra("userType", userType);
                                        }
                                        startActivity(loginIntent);
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Номер карты " + phone + " уже зарегистрирован", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent loginIntent = new Intent (RegisterActivity.this, LoginActivity.class);
                    if (userType != null) {
                        loginIntent.putExtra("userType", userType);
                    }
                    startActivity(loginIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingBar.dismiss();
                Toast.makeText(RegisterActivity.this, "Ошибка подключения к базе данных", Toast.LENGTH_SHORT).show();
            }
        });
    }
}