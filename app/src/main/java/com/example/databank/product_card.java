package com.example.databank;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.databank.Model.PlaceModel;
import com.example.databank.Model.PopularModel;
import com.example.databank.Model.ViewAllModel;

public class product_card extends AppCompatActivity {
    ImageView detailedImg;
    TextView description, name, age, date, place;
    ImageButton backBtn, mapsButton;

    ViewAllModel viewAllModel = null;
    PopularModel popularModel = null;
    PlaceModel placeModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_card);

        // Initialize views
        detailedImg = findViewById(R.id.pro_card_img);
        description = findViewById(R.id.description);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        date = findViewById(R.id.date);
        place = findViewById(R.id.place);
        backBtn = findViewById(R.id.back_btn);

        // Set up back button
        backBtn.setOnClickListener(v -> finish());

        // Set up maps button
        mapsButton.setOnClickListener(v -> {
            String url = "";
            if (viewAllModel != null) {
                url = viewAllModel.getUrl();
            } else if (popularModel != null) {
                url = popularModel.getUrl();
            }
            
            if (!url.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

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
        age.setText(viewAllModel.getAge());
        date.setText(viewAllModel.getData());
        place.setText(viewAllModel.getPlace());
    }

    private void loadPopularModelData() {
        Glide.with(getApplicationContext())
            .load(popularModel.getImg_url())
            .into(detailedImg);
        name.setText(popularModel.getName());
        description.setText(popularModel.getDescription());
        age.setText(popularModel.getAge());
        date.setText(popularModel.getData());
        place.setText(popularModel.getPlace());
    }

    private void loadPlaceModelData() {
        detailedImg.setImageResource(placeModel.getImageResourceId());
        name.setText(placeModel.getName());
        description.setText(placeModel.getDescription());
        age.setText(placeModel.getAge());
        date.setText(placeModel.getData());
        place.setText(placeModel.getPlace());
    }
}