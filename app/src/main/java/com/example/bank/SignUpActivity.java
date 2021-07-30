package com.example.bank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    Button sendBtn, resendBtn, signupBtn;
    EditText username, email, password, phoneNum, verifyCode;
    CountryCodePicker ccp;

    String phoneNumber, mVerificationId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        defineUIElement();

    }

    private void defineUIElement() {

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        ccp = findViewById(R.id.ccp);

        username = findViewById(R.id.userNameEt);
        email = findViewById(R.id.emailEt);
        password = findViewById(R.id.passwordEt);
        phoneNum = findViewById(R.id.phoneEt);
        verifyCode = findViewById(R.id.verifyEt);

//        username.setText("stest");
//        email.setText("stest@gmail.com");
//        password.setText("stest");
//        phoneNum.setText("2097394219");
//        verifyCode.setText("123456");

        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(view->{
            progressDialog.setTitle("Phone Verification");
            progressDialog.setMessage("Sending code");
            progressDialog.show();
            if (TextUtils.isEmpty(phoneNum.getText().toString().trim())) {
                phoneNum.setError("Enter phone number");
                phoneNum.requestFocus();
                progressDialog.dismiss();
            } else {
                phoneNumber = ccp.getSelectedCountryCodeWithPlus() + phoneNum.getText().toString().trim();
                initVerification();
            }
        });
        resendBtn = findViewById(R.id.resendBtn);
        resendBtn.setEnabled(false);
        signupBtn = findViewById(R.id.signUpBtn);
        signupBtn.setOnClickListener(view->{
            if (TextUtils.isEmpty(username.getText().toString().trim())) {
                Toast.makeText(this,"Please enter user name.",  Toast.LENGTH_LONG).show();
                username.setError("Please enter user name");
                username.requestFocus();
            }  else if ( TextUtils.isEmpty(email.getText().toString().trim())) {
                Toast.makeText(this,"Please enter email.",  Toast.LENGTH_LONG).show();
                email.setError("Please enter email");
                email.requestFocus();
            } else if ( TextUtils.isEmpty(password.getText().toString().trim()) ) {
                Toast.makeText(this,"Please enter password.",  Toast.LENGTH_LONG).show();
                password.setError("Please enter password");
                password.requestFocus();
            } else if ( TextUtils.isEmpty(verifyCode.getText().toString().trim()) || mVerificationId == null ) {
                Toast.makeText(this,"Please enter verify code.",  Toast.LENGTH_LONG).show();
                verifyCode.setError("Please enter verify code");
                verifyCode.requestFocus();
            } else {
                signupUser();
            }
        });
    }

    private void initVerification() {
        PhoneAuthOptions options =
            PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                        String code = "123456";
//                        verifyCode.setText(code);
//                        verifyCodeValue(code);
                        String code = phoneAuthCredential.getSmsCode();
                        if (code != null) {
                            verifyCode.setText(code);
                            verifyCodeValue(code);
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressDialog.dismiss();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        Toast.makeText(SignUpActivity.this, "Verify Code is Sent.", Toast.LENGTH_SHORT).show();
                        sendBtn.setEnabled(false);
                        signupBtn.setEnabled(true);
                        resendBtn.setEnabled(true);
                        phoneNum.setEnabled(false);
                        verifyCode.setEnabled(true);
                        mVerificationId = verificationId;
                        mResendToken = forceResendingToken;
                        progressDialog.dismiss();
                    }
                })          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signupUser() {
        User current = new User();
        current.setUsername(username.getText().toString().trim());
        current.setEmail(email.getText().toString().trim());
        current.setPassword(password.getText().toString().trim());
        current.setPhone(phoneNumber);
        Common.user = current;
        checkValue();
    }

    private void checkValue() {
        FirebaseDatabase.getInstance().getReference(Common.USER_REF)
            .orderByChild("username")
            .equalTo(Common.user.getUsername())
            .limitToLast(1)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if ( snapshot.exists() ) {
                        Toast.makeText(SignUpActivity.this,"Already exist, input other username.",Toast.LENGTH_LONG).show();
                        username.setError("Please enter other user name.");
                        username.requestFocus();
                    }
                    else {
                        verifyCodeValue(verifyCode.getText().toString());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SignUpActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void verifyCodeValue(String code) {
        progressDialog.dismiss();
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, (OnCompleteListener<AuthResult>) task -> {
                if (task.isSuccessful()) {
                    progressDialog.setTitle("Account Registration");
                    progressDialog.setMessage("Registering user..");
                    progressDialog.show();
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser firebaseUser = (task.getResult()).getUser();
                    Common.user.setPhone(phoneNumber);
                    Common.user.setUserId(Objects.requireNonNull(firebaseUser).getUid());
                    Common.user.setIsVerified("Yes");
                    Common.user.setPKey("");
                    Common.user.setSKey("");
                    Common.user.setEKey("");
                    uploadToFirebase();
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.getException() instanceof FirebaseException)
                        Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void uploadToFirebase() {

        FirebaseDatabase.getInstance().getReference(Common.USER_REF)
            .child(Common.user.getUserId())
            .setValue(Common.user)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Sign Up as successfully, Please login", Toast.LENGTH_LONG).show();
                    Intent logIntent = new Intent(SignUpActivity.this, MainActivity.class);
                    logIntent.putExtra("username",Common.user.getUsername());
                    logIntent.putExtra("password",Common.user.getPassword());
                    startActivity(logIntent);
                    finish();
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