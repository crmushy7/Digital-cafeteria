package com.example.dtcsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class login extends AppCompatActivity {
TextView accountCreate;
Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountCreate=(TextView) findViewById(R.id.accountbtn);
        login=(Button)findViewById(R.id.loginbtn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(login.this,DashBoard.class);
                startActivity(intent);
            }
        });

        accountCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start=new Intent(login.this,Registration.class);
                startActivity(start);
            }
        });

    }
}