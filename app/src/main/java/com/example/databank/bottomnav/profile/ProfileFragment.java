package com.example.databank.bottomnav.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.databank.Prevalent.Prevalent;
import com.example.databank.R;
import com.example.databank.UI.Users.MainActivity;
import com.example.databank.UI.Users.QRScannerActivity;
import com.example.databank.UI.Users.QRCodeDisplayActivity;
import com.example.databank.Utils.QRCodeGenerator;
import com.example.databank.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import io.paperdb.Paper;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private Uri filePath;
    public String phone;
    private String userType;
    private String username;

    //private List<publish> PublishList = new ArrayList<>();

    RecyclerView recyclepublish;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Инициализация Paper для локального чтения сохранённых данных
        if (getContext() != null) {
            Paper.init(getContext());
        }

        // Пытаемся получить телефон из Intent
        phone = requireActivity().getIntent() != null ? requireActivity().getIntent().getStringExtra("phone") : null;
        // Фолбэк: берём из локального хранилища, если в Intent нет
        if (phone == null || phone.isEmpty()) {
            Object storedPhone = Paper.book().read(Prevalent.UserPhoneKey);
            if (storedPhone instanceof String) {
                phone = (String) storedPhone;
            }
        }

        loadUserInfo();

        binding.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                Toast.makeText(getContext(), "Дождитесь загрузки фото, не выходите из приложения!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null) return;
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });



        return binding.getRoot();
    }

    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==Activity.RESULT_OK && result.getData()!=null && result.getData().getData()!=null){
                        filePath = result.getData().getData();

                        try{
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(
                                            requireContext().getContentResolver(),
                                            filePath
                                    );
                            binding.profileImageView.setImageBitmap(bitmap);
                        }catch(IOException e){
                            e.printStackTrace();
                        }

                        uploadImage();
                    }
                }
            }
    );

    private void loadUserInfo() {
        // Если номер телефона отсутствует, не делаем запрос и показываем подсказку
        if (phone == null || phone.isEmpty()) {
            Toast.makeText(getContext(), "Не удалось определить пользователя. Войдите заново.", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("Users").child(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            username = snapshot.child("username").getValue(String.class);
                            String profileImage = snapshot.child("profileImage").getValue(String.class);
                            userType = snapshot.child("userType").getValue(String.class);
                            String phoneValue = snapshot.child("phone").getValue(String.class);
                            Integer savings = snapshot.child("savings").getValue(Integer.class);
                            Integer targetAmount = snapshot.child("targetAmount").getValue(Integer.class);
                            String goalName = snapshot.child("goalName").getValue(String.class);
                            
                            if (username != null) {
                                binding.usernameTv.setText(username);
                            }

                            // Телефон
                            if (phoneValue != null) {
                                binding.phoneTv.setText(phoneValue);
                            }

                            // Баланс
                            if (savings != null) {
                                binding.balanceTv.setText(String.valueOf(savings) + " ₽");
                            }

                            // Текущая цель
                            if (goalName != null) {
                                binding.currentGoalTv.setText(goalName);
                            }

                            // Прогресс цели
                            if (savings != null && targetAmount != null && targetAmount > 0) {
                                int percent = Math.min(100, (int) Math.round(savings * 100.0 / targetAmount));
                                binding.goalProgressBar.setProgress(percent);
                                binding.progressPercentage.setText(percent + "%");
                            }

                            if (profileImage != null && !profileImage.isEmpty()) {
                                // Очищаем кеш Glide перед загрузкой нового изображения
                                Glide.with(getContext())
                                        .load(profileImage)
                                        .placeholder(R.drawable.loggg)
                                        .skipMemoryCache(true)  // Пропускаем кеш памяти
                                        .into(binding.profileImageView);
                            } else {
                                Toast.makeText(getContext(), "Загрузите свое фото!", Toast.LENGTH_SHORT).show();
                            }
                            
                            // Добавляем функциональность в зависимости от типа пользователя
                            setupUserSpecificFeatures();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Обработка ошибок базы данных
                    }
                });
    }



//    private void setPublishRecycler() {
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
//        recyclepublish = binding.recyclepublish;
//        recyclepublish.setLayoutManager(layoutManager);
//
//        PublishAdapter publishAdapter = new PublishAdapter(getContext(), PublishList);
//        recyclepublish.setAdapter(publishAdapter);
//    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageActivityResultLauncher.launch(intent);
    }

    private void uploadImage(){
        if (filePath != null) {
            // Загрузка изображения в Firebase Storage
            FirebaseStorage.getInstance().getReference().child("Product Images/" + phone)
                    .putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Фото загружено успешно", Toast.LENGTH_SHORT).show();

                            // Получаем URL загруженного изображения
                            FirebaseStorage.getInstance().getReference().child("Product Images/" + phone).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Обновляем URL изображения в базе данных
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(phone)
                                                    .child("profileImage").setValue(uri.toString())
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            // Очищаем кеш Glide для обновления изображения
                                                            Glide.with(getContext())
                                                                    .load(uri)
                                                                    .placeholder(R.drawable.down_splash_citek)
                                                                    .skipMemoryCache(true)  // Пропускаем кеш памяти
                                                                    .into(binding.profileImageView);
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
        }
    }

    private void setupUserSpecificFeatures() {
        if ("parent".equals(userType)) {
            // Скрыть кнопки целей и достижений
            binding.goalsBtn.setVisibility(View.GONE);
            binding.achievementsBtn.setVisibility(View.GONE);

            // Для родителей: подключить ребенка (показываем QR код родителя)
            binding.qrCodeButton.setVisibility(View.VISIBLE);
            binding.qrCodeButton.setText("Подключить ребенка");
            binding.qrCodeButton.setOnClickListener(v -> showQRCode());

            // Показать кнопку задач ребёнка
            binding.parentTasksButton.setVisibility(View.VISIBLE);
            binding.parentTasksButton.setOnClickListener(v -> {
                if (phone == null || phone.isEmpty()) return;
                // childPhone хранится у родителя в Users/{parent}/childPhone
                FirebaseDatabase.getInstance().getReference().child("Users").child(phone)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String childPhone = snapshot.child("childPhone").getValue(String.class);
                                if (childPhone != null && !childPhone.isEmpty()) {
                                    Intent i = new Intent(getContext(), com.example.databank.UI.Users.ParentTasksActivity.class);
                                    i.putExtra("childPhone", childPhone);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(getContext(), "Ребёнок не привязан", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
            });
        } else if ("child".equals(userType)) {
            // Для детей добавляем кнопку сканирования QR-кода
            binding.qrCodeButton.setVisibility(View.VISIBLE);
            binding.qrCodeButton.setText("Подключиться к родителю");
            binding.qrCodeButton.setOnClickListener(v -> startQRScanner());
        }
    }

    private void showQRCode() {
        if (username != null && phone != null) {
            Intent qrIntent = new Intent(getContext(), QRCodeDisplayActivity.class);
            qrIntent.putExtra("phone", phone);
            qrIntent.putExtra("username", username);
            startActivity(qrIntent);
        }
    }

    private void startQRScanner() {
        Intent scannerIntent = new Intent(getContext(), QRScannerActivity.class);
        scannerIntent.putExtra("phone", phone);
        startActivity(scannerIntent);
    }

}
