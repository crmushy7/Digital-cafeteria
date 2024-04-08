package com.example.dtcsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Deposit {


    public  AlertDialog dialog;

    public void depositDialogue(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View popupView = LayoutInflater.from(context).inflate(R.layout.deposit, null);

        EditText mobileNumber = popupView.findViewById(R.id.dep_mobilenumber);
        EditText amount = popupView.findViewById(R.id.dep_amount);
        TextView passwordtv = popupView.findViewById(R.id.dep_title);
        ImageView imageView=popupView.findViewById(R.id.dep_image);
        Button proceedtoUpdate = popupView.findViewById(R.id.dep_confirm_button);
        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }
}
