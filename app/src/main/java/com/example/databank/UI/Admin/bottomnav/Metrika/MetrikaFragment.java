package com.example.databank.UI.Admin.bottomnav.Metrika;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.databank.UI.Admin.HomeAdminActivity;
import com.example.databank.databinding.FragmentMetrikaBinding;

public class MetrikaFragment extends Fragment {
    public FragmentMetrikaBinding binding;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMetrikaBinding.inflate(inflater, container, false);
        FragmentMetrikaBinding.inflate(inflater, container, false);
        binding.autoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MetrikaFragment.this.getActivity(), HomeAdminActivity.class);
                intent.putExtra("type", "авто");
                startActivity(intent);
            }
        });
        binding.electronicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MetrikaFragment.this.getActivity(), HomeAdminActivity.class);
                intent.putExtra("type", "электроника");
                startActivity(intent);
            }
        });
        binding.technicsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MetrikaFragment.this.getActivity(), HomeAdminActivity.class);
                intent.putExtra("type", "техника");
                startActivity(intent);
            }
        });
        binding.sportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MetrikaFragment.this.getActivity(), HomeAdminActivity.class);
                intent.putExtra("type", "спорт");
                startActivity(intent);
            }
        });
        binding.clothesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MetrikaFragment.this.getActivity(), HomeAdminActivity.class);
                intent.putExtra("type", "одежда");
                startActivity(intent);
            }
        });
        binding.creativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MetrikaFragment.this.getActivity(), HomeAdminActivity.class);
                intent.putExtra("type", "творчество");
                startActivity(intent);
            }
        });
        binding.homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MetrikaFragment.this.getActivity(), HomeAdminActivity.class);
                intent.putExtra("type", "дом");
                startActivity(intent);
            }
        });
        binding.kidsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MetrikaFragment.this.getActivity(), HomeAdminActivity.class);
                intent.putExtra("type", "дети");
                startActivity(intent);
            }
        });
        binding.beautyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MetrikaFragment.this.getActivity(), HomeAdminActivity.class);
                intent.putExtra("type", "красота");
                startActivity(intent);
            }
        });
        binding.bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MetrikaFragment.this.getActivity(), HomeAdminActivity.class);
                intent.putExtra("type", "книги");
                startActivity(intent);
            }
        });
        binding.healthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MetrikaFragment.this.getActivity(), HomeAdminActivity.class);
                intent.putExtra("type", "здоровье");
                startActivity(intent);
            }
        });
        binding.buildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MetrikaFragment.this.getActivity(), HomeAdminActivity.class);
                intent.putExtra("type", "ремонт");
                startActivity(intent);
            }
        });






        return binding.getRoot();
    }

}
