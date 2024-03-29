package com.example.dtcsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Profile extends AppCompatActivity {
public static String hideBalance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        TextView viewBalanance = (TextView) findViewById(R.id.viewBalance);
        TextView accountBalance = (TextView) findViewById(R.id.accountBalance);
        hideBalance = viewBalanance.getText().toString();
        viewBalanance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hideBalance =="View Balance") {
                    accountBalance.setText("200,000.00");
                    viewBalanance.setText("Hide Balance");
                    hideBalance = viewBalanance.getText().toString();
                }
                else
                {
                    accountBalance.setText("*************");
                    viewBalanance.setText("View Balance");
                    hideBalance =viewBalanance.getText().toString();
                }

            }
        });

    }
}