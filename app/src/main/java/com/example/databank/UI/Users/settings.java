package com.example.databank.UI.Users;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.databank.R;
import com.example.databank.UI.LoginActivity;
import com.example.databank.Prevalent.Prevalent;
import com.example.databank.UI.Users.views.RoundedRevealView;

import io.paperdb.Paper;

public class settings extends AppCompatActivity {
    ImageButton but_logout, but_help, but_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            but_help = findViewById(R.id.help);
            but_logout = findViewById(R.id.logout);
            but_back = findViewById(R.id.back);
            but_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/hatoms"));
                    startActivity(intent);
                }
            });
            but_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Очищаем сохранённые данные "Запомнить меня"
                    Paper.init(settings.this);
                    Paper.book().delete(Prevalent.UserPhoneKey);
                    Paper.book().delete(Prevalent.UserPasswordKey);

                    // Запускаем анимацию заливки и по завершении уходим на Login
                    RoundedRevealView reveal = findViewById(R.id.revealView);
                    if (reveal != null) {
                        reveal.startSequenceFromBottomRight(1500, () -> {
                            Intent intent = new Intent(settings.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        Intent intent = new Intent(settings.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });

            but_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(settings.this, HomeActivity.class);
                    startActivity(intent);
                }
            });

            return insets;
        });
    }
}