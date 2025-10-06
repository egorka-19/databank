package com.example.databank.UI.Users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.databank.R;
import com.example.databank.adapters.response;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class add_response extends AppCompatActivity {
    ImageView photo_card;
    TextView name;
    SeekBar ratingSeekBar;
    TextView ratingValue;
    ImageButton sendBtn;
    HashMap<String, List<String>> productCategories;

    response responseAdapter;
    RecyclerView recycle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_response);

        photo_card = findViewById(R.id.photo_card);
        name = findViewById(R.id.name1);
        Intent intent = getIntent();
        String namee = intent.getStringExtra("name");
        String imgUrl = intent.getStringExtra("img_url");
        String phone3 = intent.getStringExtra("phone");
        String type = intent.getStringExtra("type");
        System.out.println("add" + phone3);

        name.setText(namee);
        // Загрузите изображение с помощью Glide
        Glide.with(this).load(imgUrl).into(photo_card);

        recycle = findViewById(R.id.recycle);

        recycle.setLayoutManager(new LinearLayoutManager(this));
        List<String> responses = new ArrayList<>();
        // Пример данных для списка
        responses.add(type);
        responseAdapter = new response(responses, productCategories);
        recycle.setAdapter(responseAdapter);
//        List<String> responses1 = new ArrayList<>();
//        // Пример данных для списка
//        responses1.add(phone3);
//        responseAdapter = new response(responses1, productCategories);
//        recycle.setAdapter(responseAdapter);

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private List<String> createCharacteristicsList(String... characteristics) {
        List<String> list = new ArrayList<>();
        for (String characteristic : characteristics) {
            list.add(characteristic);
        }
        return list;
    }

    public List<String> getCharacteristics(String category) {
        return productCategories.get(category);
    }

    // Метод для получения всех категорий
    public HashMap<String, List<String>> getAllCategories() {
        return productCategories;
    }
}