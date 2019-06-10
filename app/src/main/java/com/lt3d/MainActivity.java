package com.lt3d;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lt3d.data.User;
import com.lt3d.fragment.LibraryFragment;
import com.lt3d.fragment.ScanFragment;
import com.lt3d.fragment.SettingFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private User user;
    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        navView = findViewById(R.id.nav_view);
        user = (User) getIntent().getSerializableExtra("user");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new LibraryFragment());
        transaction.commit();

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_library:
                    transaction.replace(R.id.fragment_container, new LibraryFragment());
                    transaction.commit();
                    return true;
                case R.id.navigation_scan:
                    transaction.replace(R.id.fragment_container, new ScanFragment());
                    transaction.commit();
                    return true;
                case R.id.navigation_setting:
                    transaction.replace(R.id.fragment_container, new SettingFragment());
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
