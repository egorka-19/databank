package com.example.databank.UI.Users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.databank.R;
import com.example.databank.Utils.QRCodeGenerator;
import com.example.databank.UI.Users.HomeActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;

public class QRScannerActivity extends AppCompatActivity {
    private String childPhone;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        // Получаем номер телефона ребенка
        childPhone = getIntent().getStringExtra("phone");
        if (childPhone == null || childPhone.isEmpty()) {
            Toast.makeText(this, "Ошибка: не получен номер телефона", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadingBar = new ProgressDialog(this);
        
        // Запускаем сканер QR-кода
        startQRScanner();
    }

    private void startQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Наведите камеру на QR-код родителя");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);

        barcodeLauncher.launch(options);
    }

    private final androidx.activity.result.ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Сканирование отменено", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    processQRCode(result.getContents());
                }
            }
    );

    private void processQRCode(String qrData) {
        if (!QRCodeGenerator.isValidConnectionData(qrData)) {
            Toast.makeText(this, "Неверный QR-код. Пожалуйста, отсканируйте QR-код родителя.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String[] connectionData = QRCodeGenerator.parseConnectionData(qrData);
        if (connectionData == null || connectionData.length < 2) {
            Toast.makeText(this, "Ошибка обработки QR-кода", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String parentPhone = connectionData[0];
        String parentName = connectionData[1];

        // Подключаем ребенка к родителю
        connectChildToParent(parentPhone, parentName);
    }

    private void connectChildToParent(String parentPhone, String parentName) {
        loadingBar.setTitle("Подключение к родителю");
        loadingBar.setMessage("Пожалуйста, подождите...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        
        // Обновляем данные ребенка
        HashMap<String, Object> childData = new HashMap<>();
        childData.put("parentPhone", parentPhone);
        childData.put("qrCode", "CONNECTED");

        rootRef.child("Users").child(childPhone).updateChildren(childData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Обновляем данные родителя
                        HashMap<String, Object> parentData = new HashMap<>();
                        parentData.put("childPhone", childPhone);

                        rootRef.child("Users").child(parentPhone).updateChildren(parentData)
                                .addOnCompleteListener(parentTask -> {
                                    loadingBar.dismiss();
                                    
                                    if (parentTask.isSuccessful()) {
                                        Toast.makeText(this, "Успешно подключен к родителю " + parentName, Toast.LENGTH_SHORT).show();
                                        
                                        // Переходим на главный экран
                                        Intent homeIntent = new Intent(QRScannerActivity.this, HomeActivity.class);
                                        homeIntent.putExtra("phone", childPhone);
                                        homeIntent.putExtra("userType", "child");
                                        startActivity(homeIntent);
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Ошибка подключения к родителю", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(this, "Ошибка подключения", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}
