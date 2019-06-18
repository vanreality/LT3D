package com.lt3d;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lt3d.data.Book;
import com.lt3d.data.Books;
import com.lt3d.data.User;
import com.lt3d.fragment.LibraryFragment;
import com.lt3d.fragment.ScanFragment;
import com.lt3d.fragment.SettingFragment;
import com.lt3d.tools.Alert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    BottomNavigationView navView;
    private static final int RC_SIGN_IN = 123;
    LibraryFragment libraryFragment;
    ScanFragment scanFragment;
    SettingFragment settingFragment;
    private DatabaseReference databaseReference;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        createFirebaseSignInIntent();
    }

    private void init() {
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        libraryFragment = new LibraryFragment();
        scanFragment = new ScanFragment();
        settingFragment = new SettingFragment();

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void createFirebaseSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo)
//                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                currentUser = FirebaseAuth.getInstance().getCurrentUser();

                databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        changeFragment(libraryFragment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

                databaseReference.addValueEventListener(valueEventListener);

            } else {
                Alert.show(this, "Sign in failed, please check your Internet");
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_library:
                    changeFragment(libraryFragment);
                    return true;
                case R.id.navigation_scan:
                    changeFragment(scanFragment);
                    return true;
                case R.id.navigation_setting:
                    changeFragment(settingFragment);
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

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public User getUser() { return user; }

    private void writeData(String userId) {
        List<String> library = new ArrayList<>();
        library.add("0");
        library.add("2");
        User user = new User(library);
        databaseReference.child("users").child(userId).setValue(user);

        List<Book> books = new ArrayList<>();
        books.add(new Book("Quantum mechanics", "0"));
        books.add(new Book("Biology", "1"));
        books.add(new Book("Auto CAD", "2"));
        books.add(new Book("Unity development", "2"));
        Books test = new Books(books);
        databaseReference.child("books").setValue(test);
    }
}
