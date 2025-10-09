package com.example.databank.bottomnav.main;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

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
import androidx.constraintlayout.widget.ConstraintLayout;
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
        progressBar = view.findViewById(R.id.progressbar);
        phone = requireActivity().getIntent().getStringExtra("phone");

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

        ////////////Search View

        recyclerViewSearch = view.findViewById(R.id.search_rec);
        search_box = view.findViewById(R.id.serach_box);
        viewAllModelList = new ArrayList<>();
        viewAllAdapters = new ViewAllAdapters(getContext(), viewAllModelList);
        recyclerViewSearch.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerViewSearch.setAdapter(viewAllAdapters);
        recyclerViewSearch.setHasFixedSize(true);
        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                popularRec.setVisibility(VISIBLE);
                homeCatRec.setVisibility(VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                popularRec.setVisibility(VISIBLE);
                homeCatRec.setVisibility(VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {
                popularRec.setVisibility(VISIBLE);
                homeCatRec.setVisibility(VISIBLE);
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

    private void loadAllEvents() {
        db.collection("events")
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
                            progressBar.setVisibility(View.GONE);
                        } else {
                            System.out.println("Error" + task.getException());
                            Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                        }
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

    private void searchProduct(String type) {
        if (!type.isEmpty()) {
            // Show search results
            db.collection("events")
                    .whereEqualTo("type", type)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                viewAllModelList.clear();
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                    ViewAllModel viewAllModel = doc.toObject(ViewAllModel.class);
                                    viewAllModelList.add(viewAllModel);
                                }
                                viewAllAdapters.notifyDataSetChanged();

                                // Hide category and popular items, show search results
                                popularRec.setVisibility(INVISIBLE);
                                homeCatRec.setVisibility(INVISIBLE);
                                recyclerViewSearch.setVisibility(VISIBLE);
                            }
                        }
                    });
        } else {
            // When search is cleared
            viewAllModelList.clear();
            viewAllAdapters.notifyDataSetChanged();
            recyclerViewSearch.setVisibility(INVISIBLE);

            // Show category and popular items based on selected category
            popularRec.setVisibility(VISIBLE);
            homeCatRec.setVisibility(VISIBLE);

            // Reload items based on current category selection
            if (homeAdapter != null && homeAdapter.getSelectedPosition() == 0) {
                // If "All" is selected
                loadAllEvents();
            } else if (homeAdapter != null && categoryList != null && homeAdapter.getSelectedPosition() < categoryList.size()) {
                // If specific category is selected
                filterItemsByCategory(categoryList.get(homeAdapter.getSelectedPosition()).getType());
            }
        }
    }



}
