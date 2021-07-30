package com.example.bank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    EditText usernameEt,passwordEt;
    Button loginBtn, signupBtn;

    Handler handler = new Handler();
    boolean checking = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            defineUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void defineUI() throws IOException {

        usernameEt = findViewById(R.id.userNameEt);
        passwordEt = findViewById(R.id.passwordEt);

        usernameEt.setText(getIntent().getStringExtra("username"));
        passwordEt.setText(getIntent().getStringExtra("password"));

        usernameEt.setText("test");
        passwordEt.setText("test");

        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signUpBtn);
        loginBtn.setOnClickListener(v->{login();});
        signupBtn.setOnClickListener(v->{signUp();});        
    }

    private void signUp() {
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
    }

    private void login() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("SignIn");
        progressDialog.setMessage("Verifying API credentials...");
        progressDialog.show();
        if (TextUtils.isEmpty(usernameEt.getText().toString())) {
            usernameEt.setError("Enter user name");
            usernameEt.requestFocus();
            progressDialog.dismiss();
        } else if (TextUtils.isEmpty(passwordEt.getText().toString())) {
            passwordEt.setError("Enter password");
            passwordEt.requestFocus();
            progressDialog.dismiss();
        } else {
            fetchUser(usernameEt.getText().toString(), passwordEt.getText().toString());
        }
    }

    private void fetchUser(String userName, String password) {
        FirebaseDatabase.getInstance().getReference(Common.USER_REF)
            .orderByChild("username")
            .equalTo(userName)
            .limitToLast(100)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot itemSnapShot : snapshot.getChildren()) {
                            User user = itemSnapShot.getValue(User.class);
                            Common.user = user;
                            if (user.getPassword().equals(password)) {
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try  {
//                                            checking = common.checkApi(MainActivity.this, user.getPKey(), user.getEKey(), user.getSKey());
                                            checking = true;
                                            handler.postDelayed(() -> {
                                                progressDialog.dismiss();
                                                if (checking){
                                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                    finish();
                                                }
                                                else {
                                                    Toast.makeText(MainActivity.this, "You need to enter api key as exactly.", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(MainActivity.this, PhoneVerifyActivity.class));
                                                }
                                            }, 1500);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                thread.start();
                            } else {
                                progressDialog.dismiss();
                                passwordEt.setError("Wrong password");
                                passwordEt.requestFocus();
                            }
                        }
                    } else {
                        progressDialog.dismiss();
                        usernameEt.setError("User name does not exist");
                        usernameEt.requestFocus();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}