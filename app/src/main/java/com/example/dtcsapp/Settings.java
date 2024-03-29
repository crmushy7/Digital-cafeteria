package com.example.dtcsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_settings);



        Button homeBtn = (Button) findViewById(R.id.homeBtn);
        Button feedbackBtn = (Button) findViewById(R.id.feedbackBtn);
        Button settingsBtn = (Button) findViewById(R.id.settingsBtn);
        Button profileBtn = (Button) findViewById(R.id.profileBtn);
        ImageView topProfile=findViewById(R.id.sa_topProfilePic);
        ImageView cardProfile=findViewById(R.id.sa_cardProfilePic);
        TextView name=findViewById(R.id.sa_user_Fullname);
        TextView email=findViewById(R.id.sa_user_email);
        TextView pNo=findViewById(R.id.sa_user_phone);

        name.setText(DashBoard.fullName);
        email.setText(DashBoard.user_email);
        pNo.setText(DashBoard.phonenumber);
        File imFile=new File(DashBoard.user_profilePic);
        Bitmap bitmap= BitmapFactory.decodeFile(imFile.getAbsolutePath());
        topProfile.setImageBitmap(bitmap);
        cardProfile.setImageBitmap(bitmap);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsBtn.setTextColor(getResources().getColor(R.color.white));
                settingsBtn.setBackgroundResource(R.drawable.time);
                homeBtn.setTextColor(getResources().getColor(R.color.black));
                homeBtn.setBackgroundResource(R.color.white);
                profileBtn.setTextColor(getResources().getColor(R.color.black));
                profileBtn.setBackgroundResource(R.color.white);
                feedbackBtn.setTextColor(getResources().getColor(R.color.black));
                feedbackBtn.setBackgroundResource(R.color.white);

            }
        });
        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsBtn.setTextColor(getResources().getColor(R.color.black));
                settingsBtn.setBackgroundResource(R.color.white);
                homeBtn.setTextColor(getResources().getColor(R.color.black));
                homeBtn.setBackgroundResource(R.color.white);
                profileBtn.setTextColor(getResources().getColor(R.color.black));
                profileBtn.setBackgroundResource(R.color.white);
                feedbackBtn.setTextColor(getResources().getColor(R.color.white));
                feedbackBtn.setBackgroundResource(R.drawable.time);

            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsBtn.setTextColor(getResources().getColor(R.color.black));
                settingsBtn.setBackgroundResource(R.color.white);
                homeBtn.setTextColor(getResources().getColor(R.color.black));
                homeBtn.setBackgroundResource(R.color.white);
                profileBtn.setTextColor(getResources().getColor(R.color.white));
                profileBtn.setBackgroundResource(R.drawable.time);
                feedbackBtn.setTextColor(getResources().getColor(R.color.black));
                feedbackBtn.setBackgroundResource(R.color.white);
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsBtn.setTextColor(getResources().getColor(R.color.black));
                settingsBtn.setBackgroundResource(R.color.white);
                homeBtn.setTextColor(getResources().getColor(R.color.white));
                homeBtn.setBackgroundResource(R.drawable.time);
                profileBtn.setTextColor(getResources().getColor(R.color.black));
                profileBtn.setBackgroundResource(R.color.white);
                feedbackBtn.setTextColor(getResources().getColor(R.color.black));
                feedbackBtn.setBackgroundResource(R.color.white);
                Intent intent=new Intent(Settings.this,DashBoard.class);
                startActivity(intent);
            }
        });
    }
}