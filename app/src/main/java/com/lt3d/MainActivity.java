package com.lt3d;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lt3d.data.User;
import com.lt3d.fragment.LibraryFragment;
import com.lt3d.fragment.ScanFragment;
import com.lt3d.fragment.SettingFragment;
import com.lt3d.tools.retrofit.Service;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private User user;
    private FirebaseUser currentUser;
    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        navView = findViewById(R.id.nav_view);
//        user = (User) getIntent().getSerializableExtra("user");
        currentUser = (FirebaseUser) getIntent().getSerializableExtra("user");

        changeFragment(new LibraryFragment());

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_library:
                    changeFragment(new LibraryFragment());
                    return true;
                case R.id.navigation_scan:
                    changeFragment(new ScanFragment());
                    return true;
                case R.id.navigation_setting:
                    changeFragment(new SettingFragment());
                    return true;
            }
            return false;
        }
    };

    private void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public User getUser() {
        return this.user;
    }
}
