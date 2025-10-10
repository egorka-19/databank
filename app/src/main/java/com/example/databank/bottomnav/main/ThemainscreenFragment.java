package com.example.databank.bottomnav.main;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.databank.Model.HomeCategory;
import com.example.databank.Model.PopularModel;
import com.example.databank.R;
import com.example.databank.adapters.HomeAdapter;
import com.example.databank.adapters.PopularAdapters;
import com.example.databank.databinding.FragmentMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class ThemainscreenFragment extends Fragment {
    ProgressBar progressBar;
    ScrollView scrollView;
    private FragmentMainBinding binding;
    FirebaseFirestore db;
    private Uri filePath;
    
    // Элементы прогресс-бара
    private ProgressBar savingsProgressBar;
    private TextView progressText;
    private TextView goalNameValue;
    
    // Данные для прогресса
    private static final int DEFAULT_TARGET_AMOUNT = 30000; // Цель по умолчанию - 30 000 рублей
    private int currentAmount = 0; // Текущая накопленная сумма
    private int targetAmount = DEFAULT_TARGET_AMOUNT; // Цель накоплений (загружается из Firebase)

    private ImageButton nextButton, allCategoryBtn;
    RecyclerView popularRec, homeCatRec;

    private CheckBox low12, bow12;
    PopularAdapters popularAdapters;
    List<PopularModel> popularModelList;

    List<HomeCategory> categoryList;
    HomeAdapter homeAdapter;

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
        progressBar = view.findViewById(R.id.progressbar);
        goalNameValue = view.findViewById(R.id.goal_name_value);
        phone = requireActivity().getIntent().getStringExtra("phone");
        String userType = requireActivity().getIntent().getStringExtra("userType");
        
        // Инициализация/скрытие прогресс-бара накоплений для родителей
        if ("parent".equals(userType)) {
            View progressContainer = view.findViewById(R.id.progress_container);
            if (progressContainer != null) {
                progressContainer.setVisibility(View.GONE);
            }
            View createBtn = view.findViewById(R.id.create_task_btn);
            if (createBtn != null) {
                createBtn.setVisibility(View.VISIBLE);
                createBtn.setOnClickListener(v -> {
                    Intent i = new Intent(requireContext(), com.example.databank.UI.Users.CreateParentTaskActivity.class);
                    i.putExtra("phone", phone);
                    startActivity(i);
                });
            }
        } else {
            savingsProgressBar = view.findViewById(R.id.progress_bar);
            progressText = view.findViewById(R.id.progress_text);
            // Загружаем данные о накоплениях пользователя
            loadSavingsData();
            loadGoalName();
        }

        progressBar.setVisibility(VISIBLE);

        popularRec.setLayoutManager(new GridLayoutManager(getContext(), 1));
        popularModelList = new ArrayList<>();
        popularAdapters = new PopularAdapters(getActivity(), popularModelList);
        popularRec.setAdapter(popularAdapters);

        // Load all events initially
        loadAllEvents();

        homeCatRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryList = new ArrayList<>();

        // Add "All" category as the first item
        categoryList.add(new HomeCategory("Все", "all"));

        homeAdapter = new HomeAdapter(getActivity(), categoryList, this);
        homeCatRec.setAdapter(homeAdapter);

        db.collection("HomeCategory")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HomeCategory homeCategory = document.toObject(HomeCategory.class);
                                categoryList.add(homeCategory);
                                homeAdapter.notifyDataSetChanged();
                            }
                        } else {
                            System.out.println("Error" + task.getException());
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
                            }catch(IOException e){
                                e.printStackTrace();
                            }


                        }
                    }
                }
        );


        return view;
    }

    private void loadGoalName() {
        if (phone == null || phone.isEmpty() || goalNameValue == null) return;
        com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
                .child("Users").child(phone).child("goalName")
                .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        String name = snapshot.getValue(String.class);
                        if (name == null || name.trim().isEmpty()) name = "—";
                        goalNameValue.setText(name);
                    }

                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) { }
                });
    }

    

    private void loadAllEvents() {
        db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    System.out.println("Error" + task.getException());
                    Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                    return;
                }

                // First read user's tasks to know which events to hide
                FirebaseDatabase.getInstance().getReference().child("Users").child(phone).child("tasks")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                java.util.HashSet<String> hiddenSourceIds = new java.util.HashSet<>();
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    Object statusObj = child.child("status").getValue();
                                    Object sourceIdObj = child.child("sourceId").getValue();
                                    String status = statusObj == null ? null : String.valueOf(statusObj);
                                    String sourceId = sourceIdObj == null ? null : String.valueOf(sourceIdObj);
                                    if (sourceId != null && ("pending_review".equals(status) || "completed".equals(status))) {
                                        hiddenSourceIds.add(sourceId);
                                    }
                                }

                                popularModelList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    PopularModel popularModel = document.toObject(PopularModel.class);
                                    popularModel.setId(document.getId());
                                    if (!hiddenSourceIds.contains(popularModel.getId())) {
                                        popularModelList.add(popularModel);
                                    }
                                }
                                popularAdapters.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                popularModelList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    PopularModel popularModel = document.toObject(PopularModel.class);
                                    popularModel.setId(document.getId());
                                    popularModelList.add(popularModel);
                                }
                                popularAdapters.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

    public void showAllItems() {
        loadAllEvents();
    }

    public void filterItemsByCategory(String categoryType) {
        db.collection("events")
                .whereEqualTo("type", categoryType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            popularModelList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PopularModel popularModel = document.toObject(PopularModel.class);
                                popularModelList.add(popularModel);
                            }
                            popularAdapters.notifyDataSetChanged();
                        } else {
                            System.out.println("Error" + task.getException());
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void loadSavingsData() {
        // Загружаем данные о накоплениях из Firebase Database
        if (phone != null && !phone.isEmpty()) {
            System.out.println("Loading savings for phone: " + phone);
            
            // Загружаем текущую сумму накоплений
            FirebaseDatabase.getInstance().getReference().child("Users").child(phone).child("savings")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            System.out.println("Savings snapshot exists: " + snapshot.exists());
                            System.out.println("Savings snapshot value: " + snapshot.getValue());
                            
                            if (snapshot.exists()) {
                                Object value = snapshot.getValue();
                                if (value instanceof Integer) {
                                    currentAmount = (Integer) value;
                                } else if (value instanceof Long) {
                                    currentAmount = ((Long) value).intValue();
                                } else if (value instanceof String) {
                                    String stringValue = (String) value;
                                    if (stringValue.isEmpty()) {
                                        currentAmount = 0;
                                    } else {
                                        try {
                                            currentAmount = Integer.parseInt(stringValue);
                                        } catch (NumberFormatException e) {
                                            currentAmount = 0;
                                        }
                                    }
                                } else {
                                    currentAmount = 0;
                                }
                            } else {
                                currentAmount = 0;
                            }
                            
                            System.out.println("Current amount set to: " + currentAmount);
                            loadTargetAmount();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("Savings Firebase error: " + error.getMessage());
                            currentAmount = 0;
                            loadTargetAmount();
                        }
                    });
        } else {
            System.out.println("Phone is null or empty");
            currentAmount = 0;
            targetAmount = DEFAULT_TARGET_AMOUNT;
            updateProgressBar();
        }
    }

    private void loadTargetAmount() {
        // Загружаем цель накоплений из Firebase Database
        if (phone != null && !phone.isEmpty()) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(phone).child("targetAmount")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            System.out.println("Target snapshot exists: " + snapshot.exists());
                            System.out.println("Target snapshot value: " + snapshot.getValue());
                            
                            if (snapshot.exists()) {
                                Object value = snapshot.getValue();
                                if (value instanceof Integer) {
                                    targetAmount = (Integer) value;
                                } else if (value instanceof Long) {
                                    targetAmount = ((Long) value).intValue();
                                } else if (value instanceof String) {
                                    String stringValue = (String) value;
                                    if (stringValue.isEmpty()) {
                                        targetAmount = DEFAULT_TARGET_AMOUNT;
                                    } else {
                                        try {
                                            targetAmount = Integer.parseInt(stringValue);
                                        } catch (NumberFormatException e) {
                                            targetAmount = DEFAULT_TARGET_AMOUNT;
                                        }
                                    }
                                } else {
                                    targetAmount = DEFAULT_TARGET_AMOUNT;
                                }
                            } else {
                                targetAmount = DEFAULT_TARGET_AMOUNT;
                            }
                            
                            System.out.println("Target amount set to: " + targetAmount);
                            updateProgressBar();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("Target Firebase error: " + error.getMessage());
                            targetAmount = DEFAULT_TARGET_AMOUNT;
                            updateProgressBar();
                        }
                    });
        } else {
            targetAmount = DEFAULT_TARGET_AMOUNT;
            updateProgressBar();
        }
    }

    private void updateProgressBar() {
        System.out.println("updateProgressBar called");
        System.out.println("savingsProgressBar: " + savingsProgressBar);
        System.out.println("progressText: " + progressText);
        
        if (savingsProgressBar == null || progressText == null) {
            System.out.println("One of the UI elements is null, returning");
            return;
        }

        // Вычисляем процент
        double percentageDouble = (currentAmount * 100.0) / targetAmount;
        int percentage = (int) Math.round(percentageDouble);
        if (percentage > 100) percentage = 100;
        
        System.out.println("Percentage calculation: " + currentAmount + " * 100.0 / " + targetAmount + " = " + percentageDouble + " -> " + percentage);

        System.out.println("Setting progress to: " + percentage + "%");
        System.out.println("Current amount: " + currentAmount);
        System.out.println("Target amount: " + targetAmount);

        // Обновляем прогресс-бар
        savingsProgressBar.setProgress(percentage);

        // Обновляем текст с процентами
        progressText.setText(percentage + "%");

        System.out.println("Progress bar updated successfully");
    }

    // Метод для обновления накоплений (можно вызывать из других частей приложения)
    public void updateSavings(int newAmount) {
        currentAmount = newAmount;
        updateProgressBar();
        
        // Сохраняем в Firebase
        if (phone != null && !phone.isEmpty()) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(phone)
                    .child("savings").setValue(currentAmount);
        }
    }

    // Метод для обновления цели накоплений
    public void updateTargetAmount(int newTargetAmount) {
        targetAmount = newTargetAmount;
        updateProgressBar();
        
        // Сохраняем в Firebase
        if (phone != null && !phone.isEmpty()) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(phone)
                    .child("targetAmount").setValue(targetAmount);
        }
    }

}
