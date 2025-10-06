package com.example.databank.UI.Admin.bottomnav.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databank.UI.Users.favourite;
import com.example.databank.UI.Users.settings;
import com.example.databank.databinding.FragmentProfileAdminBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AdminProfileFragment extends Fragment {
    public FragmentProfileAdminBinding binding;
    private Uri filePath;
    public String phone;

    //private List<publish> PublishList = new ArrayList<>();

    RecyclerView recyclepublish;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileAdminBinding.inflate(inflater, container, false);

        phone = requireActivity().getIntent().getStringExtra("phone");

        loadUserInfo();


        binding.buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminProfileFragment.this.getActivity(), favourite.class));
            }
        });

        binding.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminProfileFragment.this.getActivity(), settings.class));
            }
        });

        return binding.getRoot();
    }


    private void loadUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("Admins").child(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String username = snapshot.child("name").getValue().toString();
                            binding.usernameTv.setText(username);

                            } else {
                                Toast.makeText(getContext(), "Загрузите свое фото!", Toast.LENGTH_SHORT).show();
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


}

