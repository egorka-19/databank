package com.example.databank.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.databank.R;
import com.example.databank.Model.ViewAllModel;
import com.example.databank.product_card;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class ViewAllAdapters extends RecyclerView.Adapter<ViewAllAdapters.ViewHolder> {

    Context context;
    List<ViewAllModel> list;

    public ViewAllAdapters(Context context, List<ViewAllModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_all_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list == null || position < 0 || position >= list.size()) {
            return;
        }

        ViewAllModel item = list.get(position);
        if (item == null) {
            return;
        }

        // Safely load image with Glide
        try {
            if (item.getImg_url() != null && !item.getImg_url().isEmpty()) {
                Glide.with(context)
                        .load(item.getImg_url())
                        .error(R.drawable.izo)
                        .placeholder(R.drawable.izo)
                        .into(holder.popImg);
            } else {
                holder.popImg.setImageResource(R.drawable.izo);
            }
        } catch (Exception e) {
            holder.popImg.setImageResource(R.drawable.izo);
        }

        // Safely set text values
        if (item.getName() != null) {
            holder.popName.setText(item.getName());
        } else {
            holder.popName.setText("");
        }

        if (item.getDescription() != null) {
            holder.itemDescription.setText(item.getDescription());
        } else {
            holder.itemDescription.setText("");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item != null) {
                    Intent intent = new Intent(context, product_card.class);
                    intent.putExtra("detail", item);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView popImg;
        ImageView favoriteIcon;
        TextView popName;
        TextView itemDescription;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            popImg = itemView.findViewById(R.id.pop_img);
            popName = itemView.findViewById(R.id.pop_name);
            itemDescription = itemView.findViewById(R.id.item_description);
        }
    }
}
