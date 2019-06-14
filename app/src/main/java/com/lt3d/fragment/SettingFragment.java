package com.lt3d.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import com.lt3d.MainActivity;
import com.lt3d.R;

import java.util.Arrays;
import java.util.List;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private FirebaseUser currentUser;
    private TextView text_account;
    private TextView text_version;
    private TextView text_copyright;
    private View view;
    private Button btn_disconnect;
    private static final int RC_SIGN_IN = 123;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_setting,container, false);
        init();
        return view;

    }
    public void init(){
        currentUser = ((MainActivity) getActivity()).getCurrentUser();

        text_account=view.findViewById(R.id.account_show);
        text_account.setText(currentUser.getDisplayName());
        text_version=view.findViewById(R.id.version_show);
        text_version.setText("1.0.0");
        text_copyright=view.findViewById(R.id.copyright_show);
        text_copyright.setText("Copyrignt20190613.AllRightsReserved");
        btn_disconnect=view.findViewById(R.id.btn_disconnect);
        btn_disconnect.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {

        AuthUI.getInstance()
                .signOut(getContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                    createFirebaseSignInIntent();
                    }
                });


    }
    public void createFirebaseSignInIntent() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo)
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }
}
