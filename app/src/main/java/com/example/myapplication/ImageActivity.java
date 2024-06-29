package com.example.myapplication;

import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ImageActivity extends AppCompatActivity {

    private static final String TAG = "ImageActivity";
    private ViewPager2 viewPager;
    private ImagePagerAdapter imagePagerAdapter;
    private List<String> imageList;
    private int initialPosition;
    private TextView dateInfoTextView;
    private TextView locationInfoTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });

        viewPager = findViewById(R.id.view_pager);
        dateInfoTextView = findViewById(R.id.dateInfo);
        locationInfoTextView = findViewById(R.id.locationInfo);

        imageList = getIntent().getStringArrayListExtra("image_list");
        initialPosition = getIntent().getIntExtra("initial_position", 0);

        imagePagerAdapter = new ImagePagerAdapter(imageList);
        viewPager.setAdapter(imagePagerAdapter);

        // Set the current item after setting the adapter
        viewPager.post(() -> {
            viewPager.setCurrentItem(initialPosition, false);
            displayImageInfo(initialPosition);
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                displayImageInfo(position);
            }
        });
    }

    private void displayImageInfo(int position) {
        String imagePath = imageList.get(position);

        String[] location = { "-" };

        new Thread(() -> {
            try {
                ExifInterface exif = new ExifInterface(imagePath);

                String dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME);
                float[] latLong = new float[2];
                boolean hasLatLong = exif.getLatLong(latLong);

                if (hasLatLong) {
                    double latitude = latLong[0];
                    double longitude = latLong[1];
                    location[0] = getLocation(latitude, longitude);
                    Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude + ", Location: " + location);
                } else {
                    Log.d(TAG, "No location data available for image: " + imagePath);
                }

                String imageInfo = "Date & Time: " + (dateTime != null ? dateTime : "-") + "\n"
                        + "Location: " + location[0];

                runOnUiThread(() -> {
                    dateInfoTextView.setText(dateTime != null ? dateTime : "-");
                    locationInfoTextView.setText(location[0] != null ? location[0] : "-");
                });
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error reading Exif data", e);
                runOnUiThread(() -> {
                    dateInfoTextView.setText("Unable to retrieve image info.");
                    locationInfoTextView.setText("Unable to retrieve image info.");
                });
            }
        }).start();
    }

    private String getLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to get location from coordinates", e);
        }
        return "Unknown";
    }
}

