package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<String> imageList;
    private Context context;
    private boolean isThreeColumnLayout;

    public ImageAdapter(List<String> imageList, boolean isThreeColumnLayout) {
        this.imageList = imageList;
        this.isThreeColumnLayout = isThreeColumnLayout;
    }

    public void setThreeColumnLayout(boolean isThreeColumnLayout) {
        this.isThreeColumnLayout = isThreeColumnLayout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imageList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(imagePath)
                .centerCrop()
                .into(holder.imageView);

        // Set the height of the FrameLayout dynamically
        ViewGroup.LayoutParams layoutParams = holder.frameLayout.getLayoutParams();
        int height = isThreeColumnLayout ? 150 : 112; // 150dp for 3 columns, 112dp for 4 columns
        layoutParams.height = (int) (context.getResources().getDisplayMetrics().density * height);
        holder.frameLayout.setLayoutParams(layoutParams);

        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ImageActivity.class);
            intent.putStringArrayListExtra("image_list", new ArrayList<>(imageList));
            intent.putExtra("initial_position", position);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        FrameLayout frameLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            frameLayout = itemView.findViewById(R.id.frame_layout);
        }
    }
}
