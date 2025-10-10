package com.example.databank.UI.Users;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.databank.R;
import com.example.databank.bottomnav.analytics.AnalyticsFragment;
import com.example.databank.bottomnav.main.ThemainscreenFragment;
import com.example.databank.bottomnav.profile.ProfileFragment;
import com.example.databank.databinding.ActivityHomeBinding;
import com.example.databank.product_card;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Paper.init(this);
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        System.out.println("home" + intent.getStringExtra("phone"));

        String phone = intent.getStringExtra("phone");
        String userType = intent.getStringExtra("userType");
        new Intent(HomeActivity.this, product_card.class);
        intent.putExtra("phone", phone);

        getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), new ThemainscreenFragment()).commit();

        binding.bottomNav.setSelectedItemId(R.id.main);
        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.analytics, new AnalyticsFragment());
        fragmentMap.put(R.id.profile, new ProfileFragment());
        fragmentMap.put(R.id.main, new ThemainscreenFragment());
        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = fragmentMap.get(item.getItemId());
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), fragment).commit();
            }
            return true;
        });
    }
}