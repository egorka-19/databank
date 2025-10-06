package com.example.databank.UI.Users;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.databank.R;

public class Reviews_end_Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_end);

        // Получение данных из Intent
        String category = getIntent().getStringExtra("category");
        String characteristic1 = getIntent().getStringExtra("characteristic1");
        String characteristic2 = getIntent().getStringExtra("characteristic2");
        String characteristic3 = getIntent().getStringExtra("characteristic3");
        int rating1 = getIntent().getIntExtra("rating1", 0);
        int rating2 = getIntent().getIntExtra("rating2", 0);
        int rating3 = getIntent().getIntExtra("rating3", 0);
        int rating = getIntent().getIntExtra("rating", 0);


        // Используй эти данные по своему усмотрению, например, для отображения
        TextView textView = findViewById(R.id.textView);
        textView.setText("Категория: " + category + "\n" +
                "Общее впечатление" + ":" + rating + "\n" +
                characteristic1 + ": " + rating1 + "\n" +
                characteristic2 + ": " + rating2 + "\n" +
                characteristic3 + ": " + rating3);
    }
}