package com.example.databank.UI.Users;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databank.Model.ViewAllModel;
import com.example.databank.R;
import com.example.databank.adapters.ViewAllAdapters;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAllActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    List<ViewAllModel> viewAllModelList;
    ViewAllAdapters viewAllAdapters;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_all);
        firestore = FirebaseFirestore.getInstance();
        String type = getIntent().getStringExtra("type");
        recyclerView = findViewById(R.id.view_all_rec);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        viewAllModelList = new ArrayList<>();
        viewAllAdapters = new ViewAllAdapters(this, viewAllModelList);
        recyclerView.setAdapter(viewAllAdapters);

        if (type != null && type.equalsIgnoreCase("электроника")){
            firestore.collection("AllProducts").whereEqualTo("type", "электроника").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelList.add(viewAllModel);
                        viewAllAdapters.notifyDataSetChanged();
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("техника")){
            firestore.collection("AllProducts").whereEqualTo("type", "техника").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelList.add(viewAllModel);
                        viewAllAdapters.notifyDataSetChanged();
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("ремонт")){
            firestore.collection("AllProducts").whereEqualTo("type", "ремонт").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelList.add(viewAllModel);
                        viewAllAdapters.notifyDataSetChanged();
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("одежда")){
            firestore.collection("AllProducts").whereEqualTo("type", "одежда").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelList.add(viewAllModel);
                        viewAllAdapters.notifyDataSetChanged();
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("красота")){
            firestore.collection("AllProducts").whereEqualTo("type", "красота").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelList.add(viewAllModel);
                        viewAllAdapters.notifyDataSetChanged();
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("авто")){
            firestore.collection("AllProducts").whereEqualTo("type", "авто").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelList.add(viewAllModel);
                        viewAllAdapters.notifyDataSetChanged();
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("дети")){
            firestore.collection("AllProducts").whereEqualTo("type", "дети").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelList.add(viewAllModel);
                        viewAllAdapters.notifyDataSetChanged();
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("здоровье")){
            firestore.collection("AllProducts").whereEqualTo("type", "здоровье").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelList.add(viewAllModel);
                        viewAllAdapters.notifyDataSetChanged();
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("творчество")){
            firestore.collection("AllProducts").whereEqualTo("type", "творчество").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelList.add(viewAllModel);
                        viewAllAdapters.notifyDataSetChanged();
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("спорт")){
            firestore.collection("AllProducts").whereEqualTo("type", "спорт").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelList.add(viewAllModel);
                        viewAllAdapters.notifyDataSetChanged();
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("продукты питания")){
            firestore.collection("AllProducts").whereEqualTo("type", "продукты питания").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelList.add(viewAllModel);
                        viewAllAdapters.notifyDataSetChanged();
                    }
                }
            });
        }
        if (type != null && type.equalsIgnoreCase("книги")){
            firestore.collection("AllProducts").whereEqualTo("type", "книги").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelList.add(viewAllModel);
                        viewAllAdapters.notifyDataSetChanged();
                    }
                }
            });
        }


    }
}