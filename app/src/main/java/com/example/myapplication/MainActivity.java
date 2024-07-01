package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int REQUEST_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 안드로이드 13 이상
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION);
            } else {
                setupViewPagerAndTabs();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 안드로이드 6.0 이상 13 미만
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            } else {
                setupViewPagerAndTabs();
            }
        } else {
            // 안드로이드 6.0 미만
            setupViewPagerAndTabs(); // 6.0 미만은 런타임 권한 요청이 필요 없음
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupViewPagerAndTabs();
            } else {
                Toast.makeText(this, "Permission denied. Please grant storage access to use the app.", Toast.LENGTH_SHORT).show();
                // 권한이 거부된 경우 사용자에게 권한을 재요청할 수 있도록 안내
                checkPermissions();
            }
        }
    }

    private void setupViewPagerAndTabs() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setIcon(R.drawable.ic_contact);
                        tab.setText("CONTACT");
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_gallery);
                        tab.setText("GALLERY");
                        break;
                    case 2:
                        tab.setIcon(R.drawable.ic_calendar);
                        tab.setText("CALENDAR");
                        break;
                }
            }
        }).attach();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                setupViewPagerAndTabs();
//            } else {
//                Toast.makeText(this, "Permission denied. Please grant storage access to use the app.", Toast.LENGTH_SHORT).show();
//                // 권한이 거부된 경우 사용자에게 권한을 재요청할 수 있도록 안내
//                checkPermissions();
//            }
//        }
//    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private final Fragment[] fragments = new Fragment[]{
                new ContactFragment(),
                new GalleryFragment(), // SecondFragment를 GalleryFragment로 대체
                new CalendarFragment()
        };

        public ViewPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments[position];
        }

        @Override
        public int getItemCount() {
            return fragments.length;
        }
    }
}
