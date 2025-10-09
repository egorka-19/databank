package com.example.databank.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.databank.R;
import com.example.databank.Model.publish;

import java.util.List;

public class PublishAdapter extends RecyclerView.Adapter<PublishAdapter.PublishViewHolder> {
    Context context;
    List<publish> publish;

    public PublishAdapter(Context context, List<publish> publish) {
        this.context = context;
        this.publish = publish;
    }

    @NonNull
    @Override
    public PublishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View publishItems = LayoutInflater.from(context).inflate(R.layout.publish, parent, false);
        return new PublishViewHolder(publishItems);
    }

    @Override
    public void onBindViewHolder(@NonNull PublishViewHolder holder, int position) {
        String imageUrl = publish.get(position).getPublish_image();
        if (publish.get(position).getId() == 1) {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.publish_image);
        } else {
            int imageId = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());
            holder.publish_image.setImageResource(imageId);
        }
        holder.publish_title.setText(publish.get(position).getPublish_title());

    }

    @Override
    public int getItemCount() {
        return publish.size();
    }

    public static final class PublishViewHolder extends RecyclerView.ViewHolder {
        TextView publish_title;
        ImageView publish_image;
        public PublishViewHolder(@NonNull View itemView) {
            super(itemView);

            publish_title = itemView.findViewById(R.id.title_publish);
            publish_image = itemView.findViewById(R.id.publish_image);
        }
    }
}
