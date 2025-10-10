package com.example.databank.adapters;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.databank.R;
import com.example.databank.Model.PopularModel;
import com.example.databank.product_card;
import com.example.databank.Prevalent.Prevalent;
import io.paperdb.Paper;

import java.util.List;

public class PopularAdapters extends RecyclerView.Adapter<PopularAdapters.ViewHolder> {

    private final Context context;
    private List<PopularModel> popularModelList;

    public PopularAdapters(Context context, List<PopularModel> popularModelList) {
        this.context = context;
        this.popularModelList = popularModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int currentPosition = holder.getAdapterPosition(); // Always use getAdapterPosition()

        if (currentPosition != RecyclerView.NO_POSITION) { // Ensure position is valid
            PopularModel currentItem = popularModelList.get(currentPosition);

            // Glide image loading with error handling
            Glide.with(context)
                    .load(currentItem.getImg_url())
                    .error(R.drawable.iphone_pro) // Image to show in case of error
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("GlideError", "Error loading image", e);
                            return false; // Let Glide continue the load process
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.popImg);

            // Set the text for name
            holder.name.setText(currentItem.getName());
            
            // Set the text for cash
            if (currentItem.getCash() != null) {
                holder.price.setText(String.valueOf(currentItem.getCash()));
            } else {
                holder.price.setText("Цена не указана");
            }

            // Set click listener for item
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, product_card.class);
                // Assuming that PopularModel implements Serializable or Parcelable
                intent.putExtra("detail", currentItem); // Use Serializable or Parcelable to pass the object
                // Pass phone for saving tasks/savings
                String phone = null;
                try {
                    if (context instanceof Activity) {
                        phone = ((Activity) context).getIntent().getStringExtra("phone");
                    }
                } catch (Exception ignored) {}
                if (phone == null || phone.isEmpty()) {
                    try {
                        Paper.init(context.getApplicationContext());
                        Object storedPhone = Paper.book().read(Prevalent.UserPhoneKey);
                        if (storedPhone instanceof String) phone = (String) storedPhone;
                    } catch (Exception ignored) {}
                }
                if (phone != null && !phone.isEmpty()) {
                    intent.putExtra("phone", phone);
                }
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return popularModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView popImg;
        TextView name, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            popImg = itemView.findViewById(R.id.pop_img);
            name = itemView.findViewById(R.id.pop_name);
            price = itemView.findViewById(R.id.price_text);
        }
    }
}
