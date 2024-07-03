package com.example.myapplication;

import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ImageActivity extends AppCompatActivity implements ImagePagerAdapter.ImageInfoListener {

    private static final String TAG = "ImageActivity";
    private ViewPager2 viewPager;
    private ImagePagerAdapter imagePagerAdapter;
    private List<String> imageList;
    private int initialPosition;
    private TextView dateInfoTextView;
    private TextView locationInfoTextView;
    private LinearLayout infoLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        viewPager = findViewById(R.id.view_pager);
        dateInfoTextView = findViewById(R.id.dateInfo);
        locationInfoTextView = findViewById(R.id.locationInfo);
        infoLayout = findViewById(R.id.info_layout);

        imageList = getIntent().getStringArrayListExtra("image_list");
        initialPosition = getIntent().getIntExtra("initial_position", 0);

        imagePagerAdapter = new ImagePagerAdapter(imageList, this);
        viewPager.setAdapter(imagePagerAdapter);

        viewPager.post(() -> viewPager.setCurrentItem(initialPosition, false));

        // Initially hide the info layout
        infoLayout.setVisibility(View.GONE);
    }

    @Override
    public void onImageClick(int position) {
        displayImageInfo(position);
    }

    private void displayImageInfo(int position) {
        String imagePath = imageList.get(position);

        new Thread(() -> {
            try {
                ExifInterface exif = new ExifInterface(imagePath);

                String dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME);
                float[] latLong = new float[2];
                boolean hasLatLong = exif.getLatLong(latLong);

                final String location;
                if (hasLatLong) {
                    double latitude = latLong[0];
                    double longitude = latLong[1];
                    location = getLocation(latitude, longitude);
                    Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude + ", Location: " + location);
                } else {
                    location = "-";
                    Log.d(TAG, "No location data available for image: " + imagePath);
                }

                final String finalDateTime = dateTime != null ? dateTime : "-";
                final String finalLocation = location;

                runOnUiThread(() -> {
                    dateInfoTextView.setText(finalDateTime);
                    locationInfoTextView.setText(finalLocation);

                    // Toggle visibility of the info layout
                    if (infoLayout.getVisibility() == View.GONE) {
                        infoLayout.setVisibility(View.VISIBLE);
                    } else {
                        infoLayout.setVisibility(View.GONE);
                    }
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
