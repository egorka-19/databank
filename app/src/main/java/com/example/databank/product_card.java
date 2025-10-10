package com.example.databank;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.databank.Model.PlaceModel;
import com.example.databank.Model.PopularModel;
import com.example.databank.Model.ViewAllModel;
import com.example.databank.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.MutableData;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import io.paperdb.Paper;

public class product_card extends AppCompatActivity {
    ImageView detailedImg;
    TextView description, name, price;
    ImageButton backBtn;
    Uri filePath;

    ViewAllModel viewAllModel = null;
    PopularModel popularModel = null;
    PlaceModel placeModel = null;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_card);
        // Init Paper and resolve phone (from Intent or Paper fallback)
        Paper.init(getApplicationContext());
        Intent incoming = getIntent();
        phone = incoming != null ? incoming.getStringExtra("phone") : null;
        if (phone == null || phone.isEmpty()) {
            Object storedPhone = Paper.book().read(Prevalent.UserPhoneKey);
            if (storedPhone instanceof String) {
                phone = (String) storedPhone;
            }
        }

        // Initialize views
        detailedImg = findViewById(R.id.pro_card_img);
        description = findViewById(R.id.description);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        backBtn = findViewById(R.id.back_btn);

        // Set up back button
        backBtn.setOnClickListener(v -> finish());

        detailedImg.setOnClickListener(v -> selectImage());

        // Get data from intent
        final Object object = getIntent().getSerializableExtra("detail");
        if (object instanceof ViewAllModel) {
            viewAllModel = (ViewAllModel) object;
        } else if (object instanceof PopularModel) {
            popularModel = (PopularModel) object;
        } else if (object instanceof PlaceModel) {
            placeModel = (PlaceModel) object;
        }

        // Load data into views
        if (viewAllModel != null) {
            loadViewAllModelData();
        } else if (popularModel != null) {
            loadPopularModelData();
        } else if (placeModel != null) {
            loadPlaceModelData();
        }
    }

    private void loadViewAllModelData() {
        Glide.with(getApplicationContext())
            .load(viewAllModel.getImg_url())
            .into(detailedImg);
        name.setText(viewAllModel.getName());
        description.setText(viewAllModel.getDescription());
        // The layout does not include age/date/place fields; omit setting them
    }

    private void loadPopularModelData() {
        Glide.with(getApplicationContext())
            .load(popularModel.getImg_url())
            .into(detailedImg);
        name.setText(popularModel.getName());
        description.setText(popularModel.getDescription());
        // The layout does not include age/date/place fields; omit setting them
        if (popularModel.getCash() != null) {
            price.setText(String.valueOf(popularModel.getCash()));
        }
    }

    private void loadPlaceModelData() {
        detailedImg.setImageResource(placeModel.getImageResourceId());
        name.setText(placeModel.getName());
        description.setText(placeModel.getDescription());
        // The layout does not include age/date/place fields; omit setting them
    }

    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==RESULT_OK && result.getData()!=null && result.getData().getData()!=null){
                        filePath = result.getData().getData();
                        detailedImg.setImageURI(filePath);
                        uploadImage();
                    }
                }
            }
    );

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageActivityResultLauncher.launch(intent);
    }

    private void uploadImage(){
        if (filePath != null) {
            FirebaseStorage.getInstance().getReference().child("Product Images/" + System.currentTimeMillis())
                    .putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            FirebaseStorage.getInstance().getReference()
                                    .child(taskSnapshot.getMetadata().getPath())
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        saveTaskToRealtimeDb(uri.toString());
                                        Toast.makeText(product_card.this, "Фото отправлено на проверку", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(product_card.this, "Ошибка получения URL: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(product_card.this, "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private int getCurrentReward() {
        if (popularModel != null && popularModel.getCash() != null) {
            return popularModel.getCash();
        }
        return 0;
    }

    private void addRewardToSavings(int reward) {
        if (phone == null || phone.isEmpty()) return;
        if (reward <= 0) return;
        DatabaseReference savingsRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(phone).child("savings");
        final int inc = reward;
        savingsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                Integer current = 0;
                Object value = currentData.getValue();
                if (value instanceof Long) current = ((Long) value).intValue();
                else if (value instanceof Integer) current = (Integer) value;
                currentData.setValue(current + inc);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(com.google.firebase.database.DatabaseError error, boolean committed, com.google.firebase.database.DataSnapshot currentData) {
            }
        });
    }

    private void saveTaskToRealtimeDb(String imageUrl) {
        if (phone == null || phone.isEmpty()) {
            Toast.makeText(this, "Не найден номер телефона пользователя", Toast.LENGTH_LONG).show();
            return;
        }
        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(phone).child("tasks");

        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long nextNumber = snapshot.getChildrenCount() + 1;
                DatabaseReference newTaskRef = tasksRef.push();
                Map<String, Object> taskData = new HashMap<>();
                taskData.put("id", newTaskRef.getKey());
                taskData.put("taskNumber", nextNumber);
                taskData.put("status", "pending_review");
                taskData.put("imageUrl", imageUrl);
                if (popularModel != null) {
                    taskData.put("title", popularModel.getName());
                    taskData.put("category", "");
                    if (popularModel.getCash() != null) taskData.put("reward", popularModel.getCash());
                    if (popularModel.getId() != null) taskData.put("sourceId", popularModel.getId());
                }
                taskData.put("createdAt", new Date().getTime());
                taskData.put("updatedAt", new Date().getTime());
                newTaskRef.setValue(taskData, (error, ref) -> {
                    if (error == null) {
                        Toast.makeText(product_card.this, "Задача отправлена на проверку", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(product_card.this, "Ошибка сохранения задачи: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(product_card.this, "Ошибка обращения к БД: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}