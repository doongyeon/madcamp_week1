package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ViewHolder> {

    private List<String> imageList;
    private ImageInfoListener imageInfoListener;

    public ImagePagerAdapter(List<String> imageList, ImageInfoListener imageInfoListener) {
        this.imageList = imageList;
        this.imageInfoListener = imageInfoListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_full, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imageList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(imagePath)
                .fitCenter()
                .into(holder.photoView);

        holder.photoView.setOnClickListener(v -> {
            imageInfoListener.onImageClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.full_image_view);
        }
    }

    public interface ImageInfoListener {
        void onImageClick(int position);
    }
}
