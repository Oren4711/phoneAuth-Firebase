package com.a000webhostapp.docsforlife.myapplication;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.awt.font.TextAttribute;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class AuthenticationCode extends AppCompatActivity implements View.OnClickListener {

    private String phoneNumber;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    /******************Views*****************/
    private TextInputEditText authCodeETCode;
    private Button buttonSubmit;
    private TextView authCodeTVTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_code);

        authCodeETCode = findViewById(R.id.authCodeETCode);
        buttonSubmit = findViewById(R.id.authCodeButtonSubmit);
        buttonSubmit.setEnabled(false);
        authCodeTVTimer = findViewById(R.id.authCodeTVTimer);


        phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        Toast.makeText(AuthenticationCode.this, "Phone Number is : " + phoneNumber, Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();


        buttonSubmit.setOnClickListener(this);
        authCodeTVTimer.setOnClickListener(this);

        authCodeETCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isValidateCode();

            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(AuthenticationCode.this, "Invalid Phone Number", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AuthenticationCode.this,PhoneAuthentication.class);
                startActivity(intent);
                finish();


            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                /*super.onCodeSent(s, forceResendingToken);*/

                mVerificationId = verificationId;
                mResendToken = token;
                //authCodeTVTimer.setText("Hey");

                new CountDownTimer(60000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        authCodeTVTimer.setVisibility(View.VISIBLE);
                        authCodeTVTimer.setEnabled(false);
                        authCodeTVTimer.setTypeface(authCodeTVTimer.getTypeface(),Typeface.NORMAL);
                        authCodeTVTimer.setText("Resend Code in 00:" + String.valueOf(millisUntilFinished / 1000));

                    }

                    @Override
                    public void onFinish() {
                        authCodeTVTimer.setEnabled(true);
                        authCodeTVTimer.setPaintFlags(authCodeTVTimer.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                        authCodeTVTimer.setTypeface(authCodeTVTimer.getTypeface(),Typeface.BOLD_ITALIC);
                        authCodeTVTimer.setText("Click here to resend code");

                    }
                }.start();

            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                AuthenticationCode.this,
                mCallbacks
        );


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.authCodeButtonSubmit: {
                String verificationCode = authCodeETCode.getText().toString().trim();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                signInWithPhoneAuthCredential(credential);
                break;
            }
            case R.id.authCodeTVTimer: {
                resendVerificationCode(phoneNumber, mResendToken);
                break;
            }
        }

    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                AuthenticationCode.this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            Intent intent = new Intent(AuthenticationCode.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(AuthenticationCode.this, "The verification code entered was invalid", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void isValidateCode() {
        String code = authCodeETCode.getText().toString().trim();
        if (code.length() < 6) {
            authCodeETCode.setError("Enter minimum 6 digits");
            buttonSubmit.setEnabled(false);
        } else buttonSubmit.setEnabled(true);
    }

}



