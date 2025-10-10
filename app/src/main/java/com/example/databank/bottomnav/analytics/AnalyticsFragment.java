package com.example.databank.bottomnav.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.databank.Prevalent.Prevalent;
import com.example.databank.adapters.AnalyticsLogAdapter;
import com.example.databank.databinding.FragmentAnalyticsBinding;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.paperdb.Paper;

public class AnalyticsFragment extends Fragment {
    private FragmentAnalyticsBinding binding;
    private String phone;
    private final AnalyticsLogAdapter logAdapter = new AnalyticsLogAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);

        // Retrieve phone from Intent, fallback to Paper
        phone = requireActivity().getIntent() != null ? requireActivity().getIntent().getStringExtra("phone") : null;
        if (phone == null || phone.isEmpty()) {
            Object storedPhone = Paper.book().read(Prevalent.UserPhoneKey);
            if (storedPhone instanceof String) phone = (String) storedPhone;
        }

        setupChart(binding.pieChart);

        binding.logRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.logRecycler.setAdapter(logAdapter);

        loadData();
        return binding.getRoot();
    }

    private void setupChart(PieChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setUsePercentValues(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(55f);
        chart.setTransparentCircleRadius(60f);
        chart.setRotationEnabled(true); // allow rotating by touch
        chart.setDragDecelerationFrictionCoef(0.95f);

        Legend legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
    }

    private void loadData() {
        if (phone == null || phone.isEmpty()) return;

        // Aggregate by task status for chart; and build a transaction-like log
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(phone).child("tasks")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int inProgress = 0, pending = 0, completed = 0, rejected = 0;
                        List<AnalyticsLogAdapter.LogItem> logs = new ArrayList<>();

                        for (DataSnapshot child : snapshot.getChildren()) {
                            String status = String.valueOf(child.child("status").getValue());
                            Object rewardObj = child.child("reward").getValue();
                            int reward = 0;
                            if (rewardObj instanceof Long) reward = ((Long) rewardObj).intValue();
                            else if (rewardObj instanceof Integer) reward = (Integer) rewardObj;
                            long updatedAt = 0L;
                            Object ts = child.child("updatedAt").getValue();
                            if (ts instanceof Long) updatedAt = (Long) ts;
                            String title = String.valueOf(child.child("title").getValue());

                            if ("completed".equals(status)) {
                                completed++;
                                logs.add(new AnalyticsLogAdapter.LogItem("Пополнение", "Задача: " + title, Math.max(reward, 0), updatedAt));
                            } else if ("pending_review".equals(status)) {
                                pending++;
                                logs.add(new AnalyticsLogAdapter.LogItem("Отправлено на проверку", "Задача: " + title, 0, updatedAt));
                            } else if ("rejected".equals(status)) {
                                rejected++;
                                logs.add(new AnalyticsLogAdapter.LogItem("Отклонено", "Задача: " + title, 0, updatedAt));
                            } else {
                                inProgress++;
                            }
                        }

                        // Optionally, add withdrawals as negative entries if present in DB in the future

                        Collections.sort(logs, new Comparator<AnalyticsLogAdapter.LogItem>() {
                            @Override
                            public int compare(AnalyticsLogAdapter.LogItem o1, AnalyticsLogAdapter.LogItem o2) {
                                return Long.compare(o2.timestamp, o1.timestamp);
                            }
                        });
                        logAdapter.submitList(logs);

                        ArrayList<PieEntry> entries = new ArrayList<>();
                        if (inProgress > 0) entries.add(new PieEntry(inProgress, "В процессе"));
                        if (pending > 0) entries.add(new PieEntry(pending, "На проверке"));
                        if (completed > 0) entries.add(new PieEntry(completed, "Завершено"));
                        if (rejected > 0) entries.add(new PieEntry(rejected, "Отклонено"));

                        PieDataSet dataSet = new PieDataSet(entries, "Статусы задач");
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        dataSet.setSliceSpace(2f);
                        dataSet.setValueTextSize(12f);

                        PieData data = new PieData(dataSet);
                        binding.pieChart.setData(data);
                        binding.pieChart.animateY(600, Easing.EaseInOutQuad);
                        binding.pieChart.invalidate();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}


