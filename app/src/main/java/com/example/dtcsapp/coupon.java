package com.example.dtcsapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class coupon  {






    public static void generateCoupon(Context context, FoodSetGet foodSetGet) {
        String uniqueID = UniqueIDGenerator.generateUniqueID();
        DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference()
                .child("Coupons")
                .child(FirebaseAuth.getInstance().getUid())
                .push(); // Generate a unique key for the coupon

        couponRef.child("Menu Name").setValue(foodSetGet.getFoodName());
        couponRef.child("Menu Time").setValue(OurTime.getOrderTime());
        couponRef.child("Menu Price").setValue(foodSetGet.getFoodPrice());
        couponRef.child("Status").setValue("pending");
        couponRef.child("Reference Number").setValue(uniqueID);
    }




}