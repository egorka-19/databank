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

import com.example.databank.Model.Users;
import com.example.databank.Prevalent.Prevalent;
import com.example.databank.R;
import com.example.databank.UI.Users.HomeActivity;
import com.example.databank.UI.Users.CreateGoalActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private ImageButton loginButton, swipeButton;
    private EditText loginPhoneInput, loginPasswordInput;
    private ProgressDialog loadingBar;
    private CheckBox checkBoxRememberMe;
    private String expectedUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        swipeButton = (ImageButton)findViewById(R.id.swipe_btn);
        loginButton = (ImageButton)findViewById(R.id.login_button);
        loginPasswordInput = (EditText) findViewById(R.id.login_password_input);
        loginPhoneInput = (EditText) findViewById(R.id.login_phone_input);
        loadingBar = new ProgressDialog(this);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.login_checkbox);
        
        Paper.init(this);

        // Получаем тип пользователя из Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userType")) {
            expectedUserType = intent.getStringExtra("userType");
            System.out.println("Ожидаемый тип пользователя: " + expectedUserType);
        }

        swipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                if (expectedUserType != null) {
                    homeIntent.putExtra("userType", expectedUserType);
                }
                startActivity(homeIntent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String phone = loginPhoneInput.getText().toString();
        String password = loginPasswordInput.getText().toString();
        
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Введите номер договора", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Вход в приложение");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUser(phone, password);
        }
    }

    private void ValidateUser(String phone, String password) {
        if(checkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phone = loginPhoneInput.getText().toString();

                if (snapshot.child("Users").child(phone).exists()){
                    Users usersData = snapshot.child("Users").child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getPassword().equals(password)){
                            // Проверяем тип пользователя
                            String actualUserType = usersData.getUserType();
                            if (expectedUserType != null && !expectedUserType.equals(actualUserType)) {
                                loadingBar.dismiss();
                                String expectedTypeText = "parent".equals(expectedUserType) ? "родителя" : "ребенка";
                                String actualTypeText = "parent".equals(actualUserType) ? "родителя" : "ребенка";
                                Toast.makeText(LoginActivity.this, "Этот аккаунт принадлежит " + actualTypeText + ", а вы выбрали вход как " + expectedTypeText, Toast.LENGTH_LONG).show();
                                return;
                            }
                            
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                            
                            // Проверяем targetAmount только для детей
                            if ("child".equals(actualUserType)) {
                                Integer targetAmount = usersData.getTargetAmount();
                                if (targetAmount == null || targetAmount == 0) {
                                    // Если цель не установлена, переходим на экран создания цели
                                    Intent createGoalIntent = new Intent(LoginActivity.this, CreateGoalActivity.class);
                                    createGoalIntent.putExtra("phone", phone);
                                    startActivity(createGoalIntent);
                                } else {
                                    // Если цель уже установлена, переходим на главный экран
                                    Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                    homeIntent.putExtra("phone", phone);
                                    homeIntent.putExtra("userType", actualUserType);
                                    startActivity(homeIntent);
                                }
                            } else {
                                // Для родителей сразу переходим на главный экран
                                Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                homeIntent.putExtra("phone", phone);
                                homeIntent.putExtra("userType", actualUserType);
                                startActivity(homeIntent);
                            }
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Аккаунт с номером карты " + phone + " не существует", Toast.LENGTH_SHORT).show();
                    Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this, "Ошибка подключения к базе данных", Toast.LENGTH_SHORT).show();
            }
        });
    }
}