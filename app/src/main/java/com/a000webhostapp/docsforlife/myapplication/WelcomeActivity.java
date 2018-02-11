package com.a000webhostapp.docsforlife.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private TextView welcome_Activity_TextView_Intro;
    private Button welcome_Activity_Button_Next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcome_Activity_TextView_Intro = findViewById(R.id.welcome_Activity_TextView_Intro);
        welcome_Activity_Button_Next = findViewById(R.id.welcome_Activity_Button_Next);

        welcome_Activity_Button_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneAuthIntent = new Intent(WelcomeActivity.this,PhoneAuthentication.class);
                startActivity(phoneAuthIntent);
            }
        });

    }
}
