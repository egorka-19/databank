package com.example.databank.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databank.Model.HomeCategory;
import com.example.databank.R;
import com.example.databank.bottomnav.main.ThemainscreenFragment;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    Context context;
    List<HomeCategory> categoryList;
    private int selectedPosition = 0; // Default to "All" category
    private ThemainscreenFragment fragment;

    public HomeAdapter(Context context, List<HomeCategory> categoryList, ThemainscreenFragment fragment) {
        this.context = context;
        this.categoryList = categoryList;
        this.fragment = fragment;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_cat_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(categoryList.get(position).getName());

        // Set background and text color based on selection
        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.indigo));
            holder.name.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.name.setTextColor(context.getResources().getColor(R.color.gray_fortext));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);

                // Filter items based on selected category
                if (selectedPosition == 0) {
                    // Show all items
                    fragment.showAllItems();
                } else {
                    // Filter by selected category
                    fragment.filterItemsByCategory(categoryList.get(selectedPosition).getType());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cat_home_name);
            cardView = (CardView) itemView;
        }
    }
}
