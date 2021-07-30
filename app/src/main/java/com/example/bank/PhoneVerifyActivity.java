package com.example.bank;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class PhoneVerifyActivity extends AppCompatActivity {

    EditText pKey, eKey, sKey;
    Button apiVerify;

    Common common = new Common();
    boolean checking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

        defineUIElement();
    }

    private void defineUIElement() {
        pKey = findViewById(R.id.pkeyEt);
        eKey = findViewById(R.id.ekeyEt);
        sKey = findViewById(R.id.skeyEt);

//        pKey.setText("FLWPUBK-cfe6b9ee056196c2ab6834cf56845d45-X");
//        eKey.setText("9cf9d18c6aca79aaf792c430");
//        sKey.setText("FLWSECK-9cf9d18c6aca865a9322cd765fc32753-X");

        apiVerify = findViewById(R.id.sendBtn);
        apiVerify.setOnClickListener(v->{
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("SignIn");
            progressDialog.setMessage("Verifying API credentials...");
            progressDialog.show();
            if (TextUtils.isEmpty(pKey.getText().toString())) {
                pKey.setError("Enter user name");
                pKey.requestFocus();
                progressDialog.dismiss();
            } else if (TextUtils.isEmpty(eKey.getText().toString())) {
                eKey.setError("Enter password");
                eKey.requestFocus();
                progressDialog.dismiss();
            } else if (TextUtils.isEmpty(sKey.getText().toString())) {
                sKey.setError("Enter password");
                sKey.requestFocus();
                progressDialog.dismiss();
            } else {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try  {
                            checking = common.checkApi(PhoneVerifyActivity.this, String.valueOf(pKey.getText()), String.valueOf(eKey.getText()), String.valueOf(sKey.getText()));
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if (checking){
                                        updateAPI(progressDialog, String.valueOf(pKey.getText()), String.valueOf(eKey.getText()), String.valueOf(sKey.getText()));
                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(PhoneVerifyActivity.this, "Please put correct api.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
    }

    private void updateAPI(ProgressDialog progressDialog, String pKey, String eKey, String sKey) {
        Common.user.setPKey(pKey);
        Common.user.setEKey(eKey);
        Common.user.setSKey(sKey);
        FirebaseDatabase.getInstance().getReference(Common.USER_REF)
            .child(Common.user.getUserId())
            .setValue(Common.user)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(PhoneVerifyActivity.this, "API is correct value, verified.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(PhoneVerifyActivity.this, HomeActivity.class));
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            });
    }
}