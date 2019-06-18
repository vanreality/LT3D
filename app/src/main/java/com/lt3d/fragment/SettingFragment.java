package com.lt3d.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseUser;
import com.lt3d.MainActivity;
import com.lt3d.R;

import java.util.Objects;

public class SettingFragment extends Fragment {
    private View view;
    private MainActivity mainActivity;


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
    private void init(){
        TextView text_version = view.findViewById(R.id.version_show);
        TextView text_copyright = view.findViewById(R.id.copyright_show);
        TextView text_account = view.findViewById(R.id.account_show);
        Button btn_disconnect = view.findViewById(R.id.btn_disconnect);

        mainActivity = (MainActivity) getActivity();


        if (mainActivity.getCurrentUser() == null) {
            text_account.setText("Anonymous login");
            btn_disconnect.setText("Connect");
            btn_disconnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = mainActivity.getIntent();
                    mainActivity.overridePendingTransition(0, 0);
                    mainActivity.finish();
                    mainActivity.overridePendingTransition(0, 0);
                    startActivity(intent);
                }
            });
        } else {
            FirebaseUser currentUser = mainActivity.getCurrentUser();
            text_account.setText(currentUser.getDisplayName());
            btn_disconnect.setOnClickListener(v -> signOut());
        }
        text_version.setText("1.0.0");
        text_copyright.setText("Copyrignt20190613.AllRightsReserved");
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(Objects.requireNonNull(getContext()))
                .addOnCompleteListener(task -> {
                    Intent intent = mainActivity.getIntent();
                    mainActivity.overridePendingTransition(0, 0);
                    mainActivity.finish();
                    mainActivity.overridePendingTransition(0, 0);
                    startActivity(intent);
                });
    }
}
