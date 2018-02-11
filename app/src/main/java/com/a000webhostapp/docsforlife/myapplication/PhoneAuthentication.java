package com.a000webhostapp.docsforlife.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneAuthentication extends AppCompatActivity {


    private static final String TAG = "PhoneAuthActivity";


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private int verificationType = 1;


    private TextInputLayout textInputLayoutPhoneNumber;
    private Button buttonSubmit;
    private Spinner spinnerCounryList;
    private EditText editTextCountryCode;
    private TextInputEditText editTextPhoneNumber;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);
        textInputLayoutPhoneNumber = findViewById(R.id.phoneAuthTILPhoneNumber);
        editTextCountryCode = findViewById(R.id.PAuthETCountryCode);
        editTextPhoneNumber = findViewById(R.id.phoneAuthETPhoneNumber);
        buttonSubmit = findViewById(R.id.phoneAuthButtonSubmit);
        progressBar = findViewById(R.id.progressBar);





        spinnerCounryList = findViewById(R.id.phoneAuthSpinnerCountryList);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.countryCodes, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCounryList.setAdapter(adapter);

        spinnerCounryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //get the value of item selected in a spinner
                String country = (String) parent.getSelectedItem();
                //Toast.makeText(PhoneAuthentication.this, "Country is " + country, Toast.LENGTH_LONG).show();
                String a = "+";
                //concat '+' and substring only values after ch '+'
                String result = a.concat(country.substring(country.lastIndexOf('+') + 1));
                //Toast.makeText(PhoneAuthentication.this, "Country Code is  " + result, Toast.LENGTH_LONG).show();

                editTextCountryCode.setText(result);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                if (!isValidatePhone(phoneNumber)) {
                    editTextPhoneNumber.setError("Not a valid phone number");

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PhoneAuthentication.this);
                    builder.setTitle("Number Confirmation");
                    builder.setMessage(phoneNumber + "\n\n" + "Is your phone number above correct?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String phone = editTextCountryCode.getText().toString().trim().concat(
                                    editTextPhoneNumber.getText().toString().trim());

                            //progressBar.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(PhoneAuthentication.this,AuthenticationCode.class);
                            intent.putExtra("PHONE_NUMBER",phone);
                            startActivity(intent);
                            finish();


                        }
                    }).setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(PhoneAuthentication.this, "Edit Selected " + phoneNumber, Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.show();
                }
            }
        });

    }


    private boolean isValidatePhone(String phoneNumber) {
        boolean check;

        if (Pattern.matches("[a-zA-z]+", phoneNumber) ||phoneNumber.length()<=0 || phoneNumber.length() < 8 || phoneNumber.length() > 13) {
            check = false;
            Log.e("Error", "Invalid Phone Number");
        } else check = true;

        return check;
    }


}


