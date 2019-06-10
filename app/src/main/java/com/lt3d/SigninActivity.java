package com.lt3d;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lt3d.data.Setting;
import com.lt3d.data.User;
import com.lt3d.tools.Alert;
import com.lt3d.tools.LocalDataProcessor;
import com.lt3d.tools.retrofit.HashData;
import com.lt3d.tools.retrofit.Service;
import com.lt3d.tools.retrofit.ServiceFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninActivity extends AppCompatActivity {
    Button btn_signin;
    TextView txt_signup;
    EditText edt_pseudo, edt_password;
    Setting setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        init();
    }

    private void init() {
        btn_signin = findViewById(R.id.btn_signin);
        txt_signup = findViewById(R.id.txt_signup);
        edt_pseudo = findViewById(R.id.edt_pseudo);
        edt_password = findViewById(R.id.edt_password);
        setting = LocalDataProcessor.readPreference(this);



        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignupActivity();
            }
        });

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pseudo = edt_pseudo.getText().toString();
                final String password = edt_password.getText().toString();

                if (pseudo.isEmpty()) {
                    Alert.show(SigninActivity.this, "Please enter your username");
                    return;
                }

                if (password.isEmpty()) {
                    Alert.show(SigninActivity.this, "Please enter your password");
                    return;
                }

                if (setting.hasUser(pseudo)) {
                    User user = setting.getUser(pseudo);
                    if (user.verify(password)) {
                        openMainActivity(user);
                    } else {
                        Alert.show(SigninActivity.this, "Incorrect username or password");
                    }
                } else {
                    Service service = ServiceFactory.createService(setting.getUrl(), Service.class);

                    Call<HashData> call = service.authenticate(pseudo, password);

                    call.enqueue(new Callback<HashData>() {
                        @Override
                        public void onResponse(Call<HashData> call, Response<HashData> response) {
                            if (response.isSuccessful()) {
                                User user = new User(pseudo, password, response.body().getHash());
                                setting.addUser(user);
                                LocalDataProcessor.savePreference(setting, SigninActivity.this);
                                openMainActivity(user);
                            } else {
                                Alert.show(SigninActivity.this, "Incorrect username or password");
                            }
                        }

                        @Override
                        public void onFailure(Call<HashData> call, Throwable t) {
                            Alert.show(SigninActivity.this, "No Internet connection");
                        }
                    });
                }
            }
        });

        if (!setting.isEmpty()) {
            edt_pseudo.setText(setting.getRecentUser().getPseudo());
            edt_password.setText(setting.getRecentUser().getPassword());
            btn_signin.performClick();
        }
    }

    private void openMainActivity(User user) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", user);
        startActivity(i);
        overridePendingTransition(0, 0);
    }

    private void openSignupActivity() {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
