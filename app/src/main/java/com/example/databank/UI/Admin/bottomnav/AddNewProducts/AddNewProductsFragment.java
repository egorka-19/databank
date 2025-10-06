package com.example.databank.UI.Admin.bottomnav.AddNewProducts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.databank.databinding.FragmentAddNewProductsBinding;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class AddNewProductsFragment extends Fragment {
    public FragmentAddNewProductsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddNewProductsBinding.inflate(inflater, container, false);
        FragmentAddNewProductsBinding.inflate(inflater, container, false);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(1, 1),
                new DataPoint(2, 2),
                new DataPoint(3, 2.2),
                new DataPoint(4, 4),
                new DataPoint(5, 3.3),
                new DataPoint(6, 4),
                new DataPoint(7, 5),
                new DataPoint(10, 4),
                new DataPoint(12, 5),
        });
        binding.graph.getViewport().setScalable(true);
        binding.graph.getViewport().setScrollable(true);
        binding.graph.getGridLabelRenderer().setNumHorizontalLabels(12);
        binding.graph.getGridLabelRenderer().setNumVerticalLabels(5);
        binding.graph.addSeries(series);
        binding.graph.getViewport().setMinX(1);
        binding.graph.getViewport().setMaxX(12);
        binding.graph.getViewport().setXAxisBoundsManual(true);
        binding.graph.getViewport().setMinY(1);
        binding.graph.getViewport().setMaxY(5);
        binding.graph.getViewport().setYAxisBoundsManual(true);

        // Линейный график 2

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(1, 3),
                new DataPoint(2, 1),
                new DataPoint(3, 4),
                new DataPoint(4, 2),
                new DataPoint(5, 5),
                new DataPoint(6, 3),
                new DataPoint(7, 2),
                new DataPoint(10, 5),
                new DataPoint(12, 3),
        });
        binding.graph2.getViewport().setScalable(true);
        binding.graph2.getViewport().setScrollable(true);
        binding.graph2.getGridLabelRenderer().setNumHorizontalLabels(12);
        binding.graph2.getGridLabelRenderer().setNumVerticalLabels(5);
        binding.graph2.addSeries(series2);
        binding.graph2.getViewport().setMinX(1);
        binding.graph2.getViewport().setMaxX(12);
        binding.graph2.getViewport().setXAxisBoundsManual(true);
        binding.graph2.getViewport().setMinY(1);
        binding.graph2.getViewport().setMaxY(5);
        binding.graph2.getViewport().setYAxisBoundsManual(true);

        // Линейный график 3
        GraphView graph3 = new GraphView(getContext());
        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(1, 2),
                new DataPoint(2, 4),
                new DataPoint(3, 1),
                new DataPoint(4, 3),
                new DataPoint(5, 2),
                new DataPoint(6, 5),
                new DataPoint(7, 4),
                new DataPoint(10, 2),
                new DataPoint(12, 4),
        });
        graph3.getViewport().setScalable(true);
        graph3.getViewport().setScrollable(true);
        graph3.getGridLabelRenderer().setNumHorizontalLabels(12);
        graph3.getGridLabelRenderer().setNumVerticalLabels(5);
        graph3.addSeries(series3);
        graph3.getViewport().setMinX(1);
        graph3.getViewport().setMaxX(12);
        graph3.getViewport().setXAxisBoundsManual(true);
        graph3.getViewport().setMinY(1);
        graph3.getViewport().setMaxY(5);
        graph3.getViewport().setYAxisBoundsManual(true);

        // Добавление третьего графика в LinearLayout

        binding.addNewProductsLine.addView(graph3);



        return binding.getRoot();
    }
}
