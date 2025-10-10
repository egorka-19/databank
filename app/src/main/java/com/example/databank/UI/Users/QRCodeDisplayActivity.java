package com.example.databank.UI.Users;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.databank.R;
import com.example.databank.Utils.QRCodeGenerator;

public class QRCodeDisplayActivity extends AppCompatActivity {
    private ImageView qrCodeImageView;
    private TextView instructionText;
    private Button refreshButton;
    private String phone;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_display);

        // Получаем данные пользователя
        phone = getIntent().getStringExtra("phone");
        username = getIntent().getStringExtra("username");

        if (phone == null || username == null) {
            Toast.makeText(this, "Ошибка: не получены данные пользователя", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        qrCodeImageView = findViewById(R.id.qr_code_image);
        instructionText = findViewById(R.id.instruction_text);
        refreshButton = findViewById(R.id.refresh_button);

        // Генерируем и отображаем QR-код
        generateAndDisplayQRCode();

        // Кнопка обновления QR-кода
        refreshButton.setOnClickListener(v -> generateAndDisplayQRCode());
    }

    private void generateAndDisplayQRCode() {
        String qrData = QRCodeGenerator.generateConnectionData(phone, username);
        
        // Генерируем QR-код
        Bitmap qrBitmap = QRCodeGenerator.generateQRCode(qrData, 400, 400);
        
        if (qrBitmap != null) {
            qrCodeImageView.setImageBitmap(qrBitmap);
            instructionText.setText("Покажите этот QR-код ребенку для подключения");
        } else {
            Toast.makeText(this, "Ошибка генерации QR-кода", Toast.LENGTH_SHORT).show();
        }
    }
}
