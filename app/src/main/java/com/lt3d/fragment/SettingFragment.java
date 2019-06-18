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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import com.lt3d.MainActivity;
import com.lt3d.R;

public class SettingFragment extends Fragment {
    private View view;
    private MainActivity mainActivity;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * Create a view corresponding to the SettingFragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_setting,container, false);
        init();
        return view;

    }

    /**
     * Initializing this view
     */
    public void init(){
        TextView text_version = view.findViewById(R.id.version_show);
        TextView text_copyright = view.findViewById(R.id.copyright_show);
        TextView text_account = view.findViewById(R.id.account_show);
        Button btn_disconnect = view.findViewById(R.id.btn_disconnect);

        mainActivity = (MainActivity) getActivity();


        // If the user logs in anonymously
        if (mainActivity.getCurrentUser() == null) {
            text_account.setText("Anonymous login");
            btn_disconnect.setText("Connect");
            btn_disconnect.setOnClickListener(new View.OnClickListener() {
                /**
                 * Back to the login interface (MainActivity)
                 * @param v
                 */
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
            // Enable logged in users to log out from this interface
            FirebaseUser currentUser = mainActivity.getCurrentUser();
            text_account.setText(currentUser.getDisplayName());
            btn_disconnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });
        }
        text_version.setText("1.0.0");
        text_copyright.setText("Copyright20190613.AllRightsReserved");
    }

    /**
     * Fonction for logging out
     */
    public void signOut() {
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = mainActivity.getIntent();
                        mainActivity.overridePendingTransition(0, 0);
                        mainActivity.finish();
                        mainActivity.overridePendingTransition(0, 0);
                        startActivity(intent);
                    }
                });
    }
}
