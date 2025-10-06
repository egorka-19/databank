package com.example.databank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.databank.Model.PopularModel;
import com.example.databank.Model.ViewAllModel;
import com.example.databank.UI.Users.add_response;

public class product_card extends AppCompatActivity {
    ImageView detailedImg;
    TextView price, description, name;

    ImageButton get_reviews;

    ViewAllModel viewAllModel = null;
    PopularModel popularModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_card);


        get_reviews = findViewById(R.id.get_reviews);





        get_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(product_card.this, add_response.class);

                String phone2 = intent.getStringExtra("phone");




                if (viewAllModel != null){
                    intent.putExtra("img_url", viewAllModel.getImg_url());
                    intent.putExtra("type", viewAllModel.getType());
                    intent.putExtra("name", viewAllModel.getName());
                    intent.putExtra("phone", viewAllModel.getPhone());
                    System.out.println("product" + intent.putExtra("phone", viewAllModel.getPhone()));
                }
                if (popularModel != null){
                    intent.putExtra("img_url", popularModel.getImg_url());
                    intent.putExtra("type", popularModel.getType());
                    intent.putExtra("name", popularModel.getName());
                    intent.putExtra("phone", popularModel.getPhone());
                }
                intent.putExtra("phone", phone2);
                startActivity(intent);
            }
        });
        final Object object = getIntent().getSerializableExtra("detail");
        if (object instanceof ViewAllModel){
            viewAllModel = (ViewAllModel) object;

        }
        if (object instanceof PopularModel){
            popularModel = (PopularModel) object;

        }

        detailedImg = findViewById(R.id.pro_card_img);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        name = findViewById(R.id.name);

        if (viewAllModel != null){
            Glide.with(getApplicationContext()).load(viewAllModel.getImg_url()).into(detailedImg);
            price.setText(viewAllModel.getPrice());
            description.setText(viewAllModel.getDescription());
            name.setText(viewAllModel.getName());
        }
        if (popularModel != null){
            Glide.with(getApplicationContext()).load(popularModel.getImg_url()).into(detailedImg);
            price.setText(popularModel.getPrice());
            description.setText(popularModel.getDescription());
            name.setText(popularModel.getName());
        }


    }
}