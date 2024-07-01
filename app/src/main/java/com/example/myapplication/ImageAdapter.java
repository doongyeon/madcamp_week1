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
import java.util.Set;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<String> imageList;
    private Context context;
    private Set<String> selectedImages;
    private boolean isThreeColumnLayout;
    private boolean isSelectionMode;

    public ImageAdapter(List<String> imageList, Set<String> selectedImages, boolean isThreeColumnLayout, boolean isSelectionMode) {
        this.imageList = imageList;
        this.selectedImages = selectedImages;
        this.isThreeColumnLayout = isThreeColumnLayout;
        this.isSelectionMode = isSelectionMode;
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

        if (isSelectionMode) {
            holder.imageView.setOnClickListener(v -> {
                if (selectedImages.contains(imagePath)) {
                    selectedImages.remove(imagePath);
                    holder.imageView.setAlpha(1.0f); // 선택 해제 시 원래 상태로
                } else {
                    selectedImages.add(imagePath);
                    holder.imageView.setAlpha(0.5f); // 선택 시 투명도 변경
                }
            });

            holder.imageView.setAlpha(selectedImages.contains(imagePath) ? 0.5f : 1.0f);
        } else {
            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ImageActivity.class);
                intent.putStringArrayListExtra("image_list", new ArrayList<>(imageList));
                intent.putExtra("initial_position", position);
                context.startActivity(intent);
            });
        }
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
