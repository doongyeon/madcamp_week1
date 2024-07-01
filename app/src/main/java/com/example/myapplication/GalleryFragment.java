package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.media.ExifInterface;

public class GalleryFragment extends Fragment {

    private static final int REQUEST_CODE_SELECT_IMAGES = 1;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<String> imageList;
    private Set<String> selectedImages;
    private boolean isThreeColumnLayout = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        selectedImages = new HashSet<>();

        ImageButton toggleLayoutButton = view.findViewById(R.id.toggle_layout_button);
        toggleLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isThreeColumnLayout) {
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
                    toggleLayoutButton.setImageResource(R.drawable.ic_3x3);
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                    toggleLayoutButton.setImageResource(R.drawable.ic_4x4);
                }
                isThreeColumnLayout = !isThreeColumnLayout;
            }
        });

        ImageButton selectImagesButton = view.findViewById(R.id.select_images_button);
        selectImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SelectImagesActivity.class);
                intent.putStringArrayListExtra("selected_images", new ArrayList<>(selectedImages));
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGES);
            }
        });

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED) {
            imageList = new ArrayList<>(); // 초기에는 빈 리스트
            imageAdapter = new ImageAdapter(imageList, selectedImages, isThreeColumnLayout, false);
            recyclerView.setAdapter(imageAdapter);
        } else {
            Toast.makeText(getContext(), "Storage permission is required to display images.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGES && resultCode == getActivity().RESULT_OK && data != null) {
            ArrayList<String> selectedImagePaths = data.getStringArrayListExtra("selected_images");
            if (selectedImagePaths != null) {
                selectedImages.clear();
                selectedImages.addAll(selectedImagePaths);
                imageList.clear();
                imageList.addAll(selectedImagePaths);
                sortImagesByDate(imageList); // 날짜 기준으로 정렬
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    private void sortImagesByDate(List<String> images) {
        Collections.sort(images, new Comparator<String>() {
            @Override
            public int compare(String image1, String image2) {
                String date1 = getImageDate(image1);
                String date2 = getImageDate(image2);
                if (date1 == null && date2 == null) return 0;
                if (date1 == null) return 1;
                if (date2 == null) return -1;
                return date2.compareTo(date1);
            }
        });
    }

    private String getImageDate(String imagePath) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            return exif.getAttribute(ExifInterface.TAG_DATETIME);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
