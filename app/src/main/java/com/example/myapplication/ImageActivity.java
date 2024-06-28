
package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ImagePagerAdapter imagePagerAdapter;
    private List<String> imageList;
    private int initialPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        viewPager = findViewById(R.id.view_pager);

        imageList = getIntent().getStringArrayListExtra("image_list");
        initialPosition = getIntent().getIntExtra("initial_position", 0);

        imagePagerAdapter = new ImagePagerAdapter(imageList);
        viewPager.setAdapter(imagePagerAdapter);

        // Set the current item after setting the adapter
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(initialPosition, false);
            }
        });
    }
}
