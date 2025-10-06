package com.example.databank.bottomnav.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.databank.Model.HomeCategory;
import com.example.databank.Model.PopularModel;
import com.example.databank.Model.ViewAllModel;
import com.example.databank.R;
import com.example.databank.adapters.HomeAdapter;
import com.example.databank.adapters.PopularAdapters;
import com.example.databank.adapters.ViewAllAdapters;
import com.example.databank.databinding.FragmentMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    ScrollView scrollView;
    ProgressBar progressBar;
    private FragmentMainBinding binding;
    FirebaseFirestore db;
    private Uri filePath;

    private ImageButton nextButton, allCategoryBtn;
    RecyclerView popularRec, homeCatRec;

    private CheckBox low12, bow12;
    PopularAdapters popularAdapters;
    List<PopularModel> popularModelList;

    List<HomeCategory> categoryList;
    HomeAdapter homeAdapter;
    EditText search_box;
    private List<ViewAllModel> viewAllModelList;
    private RecyclerView recyclerViewSearch;
    private ViewAllAdapters viewAllAdapters;

    public String phone;

    private int age;
    String welcome_text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        db = FirebaseFirestore.getInstance();
        popularRec = view.findViewById(R.id.pop_rec);
        homeCatRec = view.findViewById(R.id.exp_rec);
        scrollView = view.findViewById(R.id.scroll_view);
        progressBar = view.findViewById(R.id.progressbar);
        phone = requireActivity().getIntent().getStringExtra("phone");
        loadUserInfo();


        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        popularRec.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        popularRec.setLayoutManager(new GridLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        popularModelList = new ArrayList<>();
        popularAdapters = new PopularAdapters(getActivity(), popularModelList);
        popularRec.setAdapter(popularAdapters);

        db.collection("PopularProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PopularModel popularModel= document.toObject(PopularModel.class);
                                popularModelList.add(popularModel);
                                popularAdapters.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                                scrollView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            System.out.println("Error" + task.getException());
                            binding.welcomeText.setText("E " + task.getException());

                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

        homeCatRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryList = new ArrayList<>();
        homeAdapter = new HomeAdapter(getActivity(), categoryList);
        homeCatRec.setAdapter(homeAdapter);

        db.collection("HomeCategory")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HomeCategory homeCategory= document.toObject(HomeCategory.class);
                                categoryList.add(homeCategory);
                                homeAdapter.notifyDataSetChanged();
                            }
                        } else {
                            System.out.println("Error" + task.getException());
                            binding.welcomeText.setText("E " + task.getException());

                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

        ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode()== Activity.RESULT_OK && result.getData()!=null && result.getData().getData()!=null){
                            filePath = result.getData().getData();

                            try{
                                Bitmap bitmap = MediaStore.Images.Media
                                        .getBitmap(
                                                requireContext().getContentResolver(),
                                                filePath
                                        );
                                binding.avatarIv.setImageBitmap(bitmap);
                            }catch(IOException e){
                                e.printStackTrace();
                            }

                            uploadImage();
                        }
                    }
                }
        );

        ////////////Search View

        recyclerViewSearch = view.findViewById(R.id.search_rec);
        search_box = view.findViewById(R.id.serach_box);
        viewAllModelList = new ArrayList<>();
        viewAllAdapters = new ViewAllAdapters(getContext(), viewAllModelList);
        recyclerViewSearch.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewSearch.setAdapter(viewAllAdapters);
        recyclerViewSearch.setHasFixedSize(true);
        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()){
                    viewAllModelList.clear();
                    viewAllAdapters.notifyDataSetChanged();
                }else{
                    searchProduct(s.toString());
                }

            }
        });

        return view;
    }

    private void searchProduct(String type) {
        if (!type.isEmpty()){
            db.collection("AllProducts").whereEqualTo("type", type).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null){
                                viewAllModelList.clear();
                                viewAllAdapters.notifyDataSetChanged();
                                for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                    ViewAllModel viewAllModel = doc.toObject(ViewAllModel.class);
                                    viewAllModelList.add(viewAllModel);
                                    viewAllAdapters.notifyDataSetChanged();
                                }

                            }
                        }
                    });
        }


    }

    private void loadUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String profileImage = snapshot.child("profileImage").getValue().toString();

                            if (!profileImage.isEmpty()) {
                                // Очищаем кеш Glide перед загрузкой нового изображения
                                Glide.with(getContext())
                                        .load(profileImage)
                                        .placeholder(R.drawable.loggg)
                                        .skipMemoryCache(true)  // Пропускаем кеш памяти
                                        .into(binding.avatarIv);
                            } else {
                                Toast.makeText(getContext(), "Загрузите свое фото!", Toast.LENGTH_SHORT).show();
                            }
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
                                                                    .into(binding.avatarIv);
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
        }
    }
}
