package com.example.databank.bottomnav.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AnalyticsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(requireContext());
        textView.setText("Аналитика");
        textView.setTextSize(20f);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);
        return textView;
    }
}


