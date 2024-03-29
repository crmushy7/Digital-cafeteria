package com.example.dtcsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Interpolator;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashBoard extends AppCompatActivity {
    private List<FoodSetGet>foodList=new ArrayList<>();
    private List<HistorySetGet>historylist=new ArrayList<>();

    RecyclerView recyclerView,myHistoryRecyclerView;
    private AlertDialog dialog;
    TextView meal_clock,meal_status;
    public static String timeStatus="BreakFast";
    public static String hideBalance="";
    public static String fullName;
    public static String user_email;
    public static String phonenumber;
    public static String userBalance;
    public static String userPassword;
    public static String user_gender;
    public static String user_profilePic;
    public static String user_dob;
    Handler handler;
    ProgressDialog progressDialog;
 FoodAdapter adapter;
 HistoryAdapter historyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        OurTime.init(getApplicationContext());
        UserDetails.init(getApplicationContext());
        fullName=UserDetails.getFullName();
        user_email=UserDetails.getEmail();
        user_dob=UserDetails.getDob();
        phonenumber=UserDetails.getPhoneNumber();
        user_gender=UserDetails.getGender();
        user_profilePic=UserDetails.getProfilePic();
        String[] salio=UserDetails.getAmount().split(" ");
//        userBalance=salio[0];
        handler=new Handler(Looper.getMainLooper());
        ImageView topProfilePic=findViewById(R.id.db_topProfilepic);
        ImageView cardProfilePic=findViewById(R.id.db_cardProfilepic);
        TextView user_Name=findViewById(R.id.db_userName);
        TextView user_Email=findViewById(R.id.db_userEmail);
        TextView user_Pno=findViewById(R.id.db_userphoneNumber);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        adapter=new FoodAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        meal_clock=(TextView) findViewById(R.id.clocktv);
        meal_status=(TextView) findViewById(R.id.mealStatustv);
       Button breakfast=(Button)findViewById(R.id.breakfastbtn);
       Button lunch=(Button)findViewById(R.id.lunchbtn);
       Button dinner=(Button)findViewById(R.id.dinnerbtn);
       Button futari=(Button)findViewById(R.id.futaribtn);
        TextView viewBalanance = (TextView) findViewById(R.id.viewBalance);
        TextView accountBalance = (TextView) findViewById(R.id.accountBalance);
       hideBalance = viewBalanance.getText().toString();
        Button homeBtn = (Button) findViewById(R.id.homeBtn);
        Button feedbackBtn = (Button) findViewById(R.id.feedbackBtn);
        Button settingsBtn = (Button) findViewById(R.id.settingsBtn);
        Button profileBtn = (Button) findViewById(R.id.profileBtn);
        LinearLayout dashBoardlayout = (LinearLayout) findViewById(R.id.dashBoardLayout);
        LinearLayout settingsLayout = (LinearLayout) findViewById(R.id.settingsLayout);
        LinearLayout feedbackLayout = (LinearLayout) findViewById(R.id.feedbackLayout);
        LinearLayout dashbordinsideLayout = (LinearLayout) findViewById(R.id.dashbordInsideLayout);
        LinearLayout profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
        TextView viewBalanance1 = (TextView) findViewById(R.id.viewBalance1);
        TextView accountBalance1 = (TextView) findViewById(R.id.accountBalance1);
        user_Name.setText(fullName);
        user_Email.setText(user_email);

        user_Pno.setText(phonenumber);
        LinearLayout myhistoryLayout = (LinearLayout) findViewById(R.id.myhistoryLayout);
        LinearLayout navigationLayout = (LinearLayout) findViewById(R.id.navigationLayout);

        TextView backtoprofile = (TextView) findViewById(R.id.backtoprofiletv);
        ImageView topPic=findViewById(R.id.pp_topProfilePic);
        ImageView smallPic=findViewById(R.id.pp_cardProfilePic);
        TextView ppUsername=findViewById(R.id.pp_userName);
        TextView ppUseremail=findViewById(R.id.pp_userEmail);
        TextView ppUsertopphone=findViewById(R.id.pp_userphone);
        TextView ppUserFname=findViewById(R.id.pp_userFname);
        TextView ppUserLname=findViewById(R.id.pp_userLname);
        TextView ppUsersmallphone=findViewById(R.id.pp_userNewPhone);
        TextView ppUsersmallemail=findViewById(R.id.pp_userNewEmail);
        TextView ppUserdob=findViewById(R.id.pp_userDOB);
        handler.post(() -> {
            progressDialog = new ProgressDialog(DashBoard.this);
            progressDialog.setMessage("Loading, Please wait...Make sure you have a stable internet connection!");
            progressDialog.setCancelable(false);
        });

        if (user_profilePic != null){
            File imFile=new File(user_profilePic);
            if (imFile.exists()){
                Bitmap bitmap= BitmapFactory.decodeFile(imFile.getAbsolutePath());
                topProfilePic.setImageBitmap(bitmap);
//                cardProfilePic.setImageBitmap(bitmap);
                topPic.setImageBitmap(bitmap);
//                smallPic.setImageBitmap(bitmap);
            }else {

            }
        }
        String[] parts= fullName.split(" ");
        ppUsername.setText(fullName);
        ppUseremail.setText(user_email);
        ppUsertopphone.setText(phonenumber);
        ppUserFname.setText(parts[0]);
        ppUserLname.setText(parts[1]);
        ppUsersmallphone.setText(phonenumber);
        ppUsersmallemail.setText(user_email);
        ppUserdob.setText(user_dob);

        if (FirebaseAuth.getInstance().getUid()==null){
            startActivity(new Intent(DashBoard.this, Registration.class));
        }else {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("All Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("Details");

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            // Retrieve user details from Firebase snapshot
                                            String Amount = snapshot.child("Amount").getValue(String.class);
                                            userBalance=Amount;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle error
                                    }
                                });

            }

//        Thread thread3=new Thread() {
//            @Override
//            public void run() {
//                try {
//                    while (!isInterrupted()) {
//                        Thread.sleep(30);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (FirebaseAuth.getInstance().getUid()==null){
//                                    startActivity(new Intent(DashBoard.this,Registration.class));
//                                    finish();
//                                }
//
//
//                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("All Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("Details");
//
//                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        if (snapshot.exists()) {
//                                            // Retrieve user details from Firebase snapshot
//                                            String Amount = snapshot.child("Amount").getValue(String.class);
//                                            userBalance=Amount;
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//                                        // Handle error
//                                    }
//                                });
//
//                            }
//                        });
//                    }
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//        };
//        thread3.start();


        backtoprofile.setOnClickListener(new View.OnClickListener() {
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
                FirebaseAuth.getInstance().signOut();
                dashbordinsideLayout.setVisibility(View.GONE);
                settingsLayout.setVisibility(View.GONE);
                feedbackLayout.setVisibility(View.GONE);
                dashBoardlayout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.VISIBLE);
                myhistoryLayout.setVisibility(View.GONE);
                navigationLayout.setVisibility(View.VISIBLE);

            }
        });

        TextView historyView = (TextView) findViewById(R.id.historyTv);

        historyView.setOnClickListener(new View.OnClickListener() {
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
                dashbordinsideLayout.setVisibility(View.GONE);
                settingsLayout.setVisibility(View.GONE);
                feedbackLayout.setVisibility(View.GONE);
                dashBoardlayout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.GONE);
                myhistoryLayout.setVisibility(View.VISIBLE);
                navigationLayout.setVisibility(View.GONE);

                myHistoryRecyclerView=(RecyclerView) findViewById(R.id.recyclerviewHistory);
                myHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(DashBoard.this));
                historyAdapter=new HistoryAdapter(new ArrayList<>());
                myHistoryRecyclerView.setAdapter(historyAdapter);
                for(int i=0;i<100;i++)
                {
                    HistorySetGet historySetGet=new HistorySetGet( "Wali makange kuku","4000 TZS","4567897654567","18/2/2024 11:12 Am","pending");
                    historylist.add(historySetGet);
                    historyAdapter.updateData(historylist);
                    Collections.reverse(historylist);
                    historyAdapter.notifyDataSetChanged();
                }
                historyAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, HistorySetGet historySetGet) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(DashBoard.this);
                        View popupView = LayoutInflater.from(DashBoard.this).inflate(R.layout.activity_coupon, null);
                        builder.setView(popupView);

                        dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        });

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
               dashBoardlayout.setVisibility(View.GONE);
               settingsLayout.setVisibility(View.VISIBLE);
               feedbackLayout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.GONE);
                myhistoryLayout.setVisibility(View.GONE);
                ImageView topProfile=findViewById(R.id.sa_topProfilePic);
                ImageView cardProfile=findViewById(R.id.sa_cardProfilePic);
                TextView name=findViewById(R.id.sa_user_Fullname);
                TextView email=findViewById(R.id.sa_user_email);
                TextView pNo=findViewById(R.id.sa_user_phone);
                LinearLayout logout=findViewById(R.id.se_logout);
                logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
                        builder.setTitle("Logout")
                                .setMessage("You are about to log out, are you sure?")
                                .setCancelable(false)
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Sign out the user
                                        FirebaseAuth.getInstance().signOut();

                                        // Initialize AuthStateListener
                                        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
                                            @Override
                                            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                                if (user == null) {
                                                    // User is signed out, redirect to sign-in activity
                                                    Intent intent = new Intent(DashBoard.this, Registration.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        };

                                        // Add AuthStateListener
                                        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });


                navigationLayout.setVisibility(View.VISIBLE);
                name.setText(DashBoard.fullName);
                email.setText(DashBoard.user_email);
                pNo.setText(DashBoard.phonenumber);
                File imFile=new File(DashBoard.user_profilePic);
                Bitmap bitmap= BitmapFactory.decodeFile(imFile.getAbsolutePath());
                topProfile.setImageBitmap(bitmap);
                cardProfile.setImageBitmap(bitmap);
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
        dashbordinsideLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.GONE);
        feedbackLayout.setVisibility(View.VISIBLE);
        dashBoardlayout.setVisibility(View.VISIBLE);
        profileLayout.setVisibility(View.GONE);
        myhistoryLayout.setVisibility(View.GONE);
        navigationLayout.setVisibility(View.VISIBLE);
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
        dashbordinsideLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.GONE);
        feedbackLayout.setVisibility(View.GONE);
        dashBoardlayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.VISIBLE);
        myhistoryLayout.setVisibility(View.GONE);
        navigationLayout.setVisibility(View.VISIBLE);
        viewBalanance1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hideBalance =="View Balance") {
                    accountBalance1.setText("waiting for connection!");
                    if (userBalance == null){

                    }else{
                        String[] Amount=userBalance.split(" ");
                        if (Amount[0].trim().equals("null")){
                            accountBalance1.setText("waiting for connection!");
                        }else{
                            accountBalance1.setText(Amount[0]+".00");
                        }
                    }

                    viewBalanance1.setText("Hide Balance");
                    hideBalance = viewBalanance.getText().toString();
                }
                else
                {
                    accountBalance1.setText("*************");
                    viewBalanance1.setText("View Balance");
                    hideBalance =viewBalanance1.getText().toString();
                }

            }
        });


        TextView update_fname=findViewById(R.id.pp_firstNameUpdate);
        TextView update_lname=findViewById(R.id.pp_lastNameUpdate);
        TextView update_phone=findViewById(R.id.pp_phoneNumberUpdate);
        TextView update_password=findViewById(R.id.pp_passwordUpdate);
        update_fname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser("Fullname");
            }
        });
        update_lname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser("Fullname");
            }
        });
        update_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser("PhoneNumber");
            }
        });
        update_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser("Password");
            }
        });

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
        dashBoardlayout.setVisibility(View.VISIBLE);
        settingsLayout.setVisibility(View.GONE);
        feedbackLayout.setVisibility(View.GONE);
        dashbordinsideLayout.setVisibility(View.VISIBLE);
        profileLayout.setVisibility(View.GONE);
        myhistoryLayout.setVisibility(View.GONE);

        navigationLayout.setVisibility(View.VISIBLE);
    }
});
        viewBalanance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hideBalance =="View Balance") {
                    if (userBalance == null){
                        accountBalance.setText("waiting for connection!");
                    }else{
                        String[] Amount=userBalance.split(" ");
                        if (Amount[0].trim().equals("null")){
                            accountBalance.setText("waiting for connection!");
                        }else{
                            accountBalance.setText(Amount[0]+".00");
                        }
                    }


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
       timeStatus=OurTime.getTimeStatus();
        if(timeStatus!=null)
        {
            switch (timeStatus)
            {
                case "BreakFast":
                    breakfast.setBackgroundResource(R.drawable.foodback);
                    breakfast.setTextColor(getResources().getColor(R.color.white));
                    lunch.setBackgroundResource(R.drawable.viewbalance);
                    lunch.setTextColor(getResources().getColor(R.color.black));
                    dinner.setBackgroundResource(R.drawable.viewbalance);
                    dinner.setTextColor(getResources().getColor(R.color.black));
                    futari.setBackgroundResource(R.drawable.viewbalance);
                    futari.setTextColor(getResources().getColor(R.color.black));
                    break;

                case "Lunch":
                    breakfast.setBackgroundResource(R.drawable.viewbalance);
                    breakfast.setTextColor(getResources().getColor(R.color.black));
                    lunch.setBackgroundResource(R.drawable.foodback);
                    lunch.setTextColor(getResources().getColor(R.color.white));
                    dinner.setBackgroundResource(R.drawable.viewbalance);
                    dinner.setTextColor(getResources().getColor(R.color.black));
                    futari.setBackgroundResource(R.drawable.viewbalance);
                    futari.setTextColor(getResources().getColor(R.color.black));
                    break;


                case "Dinner":
                    breakfast.setBackgroundResource(R.drawable.viewbalance);
                    breakfast.setTextColor(getResources().getColor(R.color.black));
                    lunch.setBackgroundResource(R.drawable.viewbalance);
                    lunch.setTextColor(getResources().getColor(R.color.black));
                    dinner.setBackgroundResource(R.drawable.foodback);
                    dinner.setTextColor(getResources().getColor(R.color.white));
                    futari.setBackgroundResource(R.drawable.viewbalance);
                    futari.setTextColor(getResources().getColor(R.color.black));
                    break;

                case "Ngano":
                    breakfast.setBackgroundResource(R.drawable.viewbalance);
                    breakfast.setTextColor(getResources().getColor(R.color.black));
                    lunch.setBackgroundResource(R.drawable.viewbalance);
                    lunch.setTextColor(getResources().getColor(R.color.black));
                    dinner.setBackgroundResource(R.drawable.viewbalance);
                    dinner.setTextColor(getResources().getColor(R.color.black));
                    futari.setBackgroundResource(R.drawable.foodback);
                    futari.setTextColor(getResources().getColor(R.color.white));
                    break;

                default:
                    break;

            }
        }

        adapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, FoodSetGet foodSetGet) {
                alertdialogBuilder(foodSetGet);
            }
        });
       breakfast.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               breakfast.setBackgroundResource(R.drawable.foodback);
               breakfast.setTextColor(getResources().getColor(R.color.white));
               lunch.setBackgroundResource(R.drawable.viewbalance);
               lunch.setTextColor(getResources().getColor(R.color.black));
               dinner.setBackgroundResource(R.drawable.viewbalance);
               dinner.setTextColor(getResources().getColor(R.color.black));
               futari.setBackgroundResource(R.drawable.viewbalance);
               futari.setTextColor(getResources().getColor(R.color.black));

               for(int i=0;i<100;i++)
               {
                   FoodSetGet foodSetGet=new FoodSetGet("4000 TZS", "Wali makange kuku","VIP","data:image/jpeg;base64,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");
                   foodList.remove(foodSetGet);
                   foodList.add(foodSetGet);
                   adapter.updateData(foodList);
                   Collections.reverse(foodList);
                   adapter.notifyDataSetChanged();
               }
           }
       });
       lunch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               breakfast.setBackgroundResource(R.drawable.viewbalance);
               breakfast.setTextColor(getResources().getColor(R.color.black));
               lunch.setBackgroundResource(R.drawable.foodback);
               lunch.setTextColor(getResources().getColor(R.color.white));
               dinner.setBackgroundResource(R.drawable.viewbalance);
               dinner.setTextColor(getResources().getColor(R.color.black));
               futari.setBackgroundResource(R.drawable.viewbalance);
               futari.setTextColor(getResources().getColor(R.color.black));

               for(int i=0;i<9;i++)
               {
                   FoodSetGet foodSetGet=new FoodSetGet("4000 TZS", "Wali makange kuku","VIP","data:image/jpeg;base64,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");
                   foodList.remove(foodSetGet);
                   foodList.add(foodSetGet);
                   adapter.updateData(foodList);
                   Collections.reverse(foodList);
                   adapter.notifyDataSetChanged();
               }
           }
       });
       dinner.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               breakfast.setBackgroundResource(R.drawable.viewbalance);
               breakfast.setTextColor(getResources().getColor(R.color.black));
               lunch.setBackgroundResource(R.drawable.viewbalance);
               lunch.setTextColor(getResources().getColor(R.color.black));
               dinner.setBackgroundResource(R.drawable.foodback);
               dinner.setTextColor(getResources().getColor(R.color.white));
               futari.setBackgroundResource(R.drawable.viewbalance);
               futari.setTextColor(getResources().getColor(R.color.black));

               for(int i=0;i<6;i++)
               {
                   FoodSetGet foodSetGet=new FoodSetGet("4000 TZS", "Wali makange kuku","VIP","data:image/jpeg;base64,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");
                   foodList.remove(foodSetGet);
                   foodList.add(foodSetGet);
                   adapter.updateData(foodList);
                   Collections.reverse(foodList);
                   adapter.notifyDataSetChanged();
               }
           }
       });
       futari.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               breakfast.setBackgroundResource(R.drawable.viewbalance);
               breakfast.setTextColor(getResources().getColor(R.color.black));
               lunch.setBackgroundResource(R.drawable.viewbalance);
               lunch.setTextColor(getResources().getColor(R.color.black));
               dinner.setBackgroundResource(R.drawable.viewbalance);
               dinner.setTextColor(getResources().getColor(R.color.black));
               futari.setBackgroundResource(R.drawable.foodback);
               futari.setTextColor(getResources().getColor(R.color.white));

               for(int i=0;i<3;i++)
               {
                   FoodSetGet foodSetGet=new FoodSetGet("4000 TZS", "Wali makange kuku","VIP","data:image/jpeg;base64,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");
                   foodList.remove(foodSetGet);
                   foodList.add(foodSetGet);
                   adapter.updateData(foodList);
                   Collections.reverse(foodList);
                   adapter.notifyDataSetChanged();
               }
           }
       });

        Thread thread=new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Calendar calendar = Calendar.getInstance();
                                String currentdate = DateFormat.getInstance().format(calendar.getTime());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                                String formattedTime = simpleDateFormat.format(new Date());

                                meal_clock.setText(formattedTime);

                                int currentHour=calendar.get(Calendar.HOUR_OF_DAY);
                                if(currentHour>=6 && currentHour<12)
                                {
                                    meal_status.setText("BreakFast");
                                }else if(currentHour>=12 && currentHour<16)
                                {
                                    meal_status.setText("Lunch");
                                } else if (currentHour>=16 && currentHour<22) {
                                    meal_status.setText("Dinner");
                                }else{
                                    meal_status.setText("Ngano");
                                }

                            }
                        });
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        };
        thread.start();

    }


    private void updateUser(String updateType){

        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
        View popupView = LayoutInflater.from(DashBoard.this).inflate(R.layout.profile_update, null);

        EditText name_et = popupView.findViewById(R.id.profile_nameet);
        EditText name_et2 = popupView.findViewById(R.id.profile_nameet2);
        EditText number_et = popupView.findViewById(R.id.profile_phoneet);
        EditText password_et = popupView.findViewById(R.id.profile_passwordet);
        TextView nametv = popupView.findViewById(R.id.profile_nametv);
        TextView nametv2 = popupView.findViewById(R.id.profile_nametv2);
        TextView numbertv = popupView.findViewById(R.id.profile_phonetv);
        TextView passwordtv = popupView.findViewById(R.id.profile_passwordtv);
        Button proceedtoUpdate = popupView.findViewById(R.id.profile_updateButton);
        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
        if (updateType=="Fullname"){
            numbertv.setVisibility(View.GONE);
            number_et.setVisibility(View.GONE);
            passwordtv.setVisibility(View.GONE);
            password_et.setVisibility(View.GONE);
        } else if (updateType=="Password") {
            numbertv.setVisibility(View.GONE);
            number_et.setVisibility(View.GONE);
            nametv.setVisibility(View.GONE);
            nametv2.setVisibility(View.GONE);
            name_et.setVisibility(View.GONE);
            name_et2.setVisibility(View.GONE);
        } else if (updateType=="PhoneNumber") {
            nametv.setVisibility(View.GONE);
            nametv2.setVisibility(View.GONE);
            name_et.setVisibility(View.GONE);
            name_et2.setVisibility(View.GONE);
            passwordtv.setVisibility(View.GONE);
            password_et.setVisibility(View.GONE);

        }
        proceedtoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (updateType=="Fullname"){
                    String new_userName=name_et.getText().toString();
                    String new_userName2=name_et2.getText().toString();
                    if (new_userName.isEmpty()){
                        progressDialog.dismiss();
                        name_et.setError("Fill this!");
                    } else if (new_userName2.isEmpty()) {
                        progressDialog.dismiss();
                        name_et2.setError("Fill this!");
                    } else{
                        String newName=new_userName+" "+new_userName2;
                        updateToFirebase(newName,updateType);
                    }
                } else if (updateType=="Password") {
                    String new_password=password_et.getText().toString();
                    if (new_password.isEmpty()){
                        progressDialog.dismiss();
                        password_et.setError("Fill this!");
                    } else if (new_password.length()<=5) {
                        progressDialog.dismiss();
                        password_et.setError("password too short(must be atleast 6 characters)!");

                    } else{
                        updateToFirebase(new_password,updateType);
                    }
                } else if (updateType=="PhoneNumber") {
                    String new_phonenumber=number_et.getText().toString();
                    if (new_phonenumber.isEmpty()){
                        progressDialog.dismiss();
                        number_et.setError("Fill this!");
                    } else if (new_phonenumber.length()<10) {
                        progressDialog.dismiss();
                        number_et.setError("numbers must be 10!");
                    } else{
                        updateToFirebase(new_phonenumber,updateType);
                    }
                }
            }
        });
    }
    private void updateToFirebase(String newData,String updateChild){
        DatabaseReference userRef=FirebaseDatabase.getInstance().getReference().child("All Users")
                .child(FirebaseAuth.getInstance().getUid().toString())
                .child("Details");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userRef.child(updateChild).setValue(newData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                if (updateChild=="Password"){


                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    AuthCredential credential = EmailAuthProvider.getCredential(UserDetails.getEmail(), UserDetails.getPassword());
                                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                if (user !=null) {
                                                    user.updatePassword(newData + "").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(DashBoard.this, "Updated successfully,log in again", Toast.LENGTH_LONG).show();
                                                                FirebaseAuth.getInstance().signOut();
                                                                Intent intent = new Intent(DashBoard.this, Registration.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }else{
                                                                progressDialog.dismiss();
                                                                Exception exception = task.getException();
                                                                Log.d("TAG", "Error updating password"+exception);
                                                            }
                                                        }
                                                    });
                                                }else {
                                                    Toast.makeText(DashBoard.this, "Try again", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            }else{
                                                progressDialog.dismiss();
                                                Toast.makeText(DashBoard.this, "Password wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }else{
                                    Toast.makeText(DashBoard.this, "Updated successfully,log in again", Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(DashBoard.this, Registration.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DashBoard.this, "Update failed,please try again later", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(DashBoard.this, "Database error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void alertdialogBuilder(FoodSetGet foodSetGet){
        AlertDialog.Builder builder=new AlertDialog.Builder(DashBoard.this);
        View popupView = LayoutInflater.from(DashBoard.this).inflate(R.layout.alert_dialogue, null);
        builder.setView(popupView);

        LinearLayout confirm=popupView.findViewById(R.id.ad_confirm_layout);
        LinearLayout error=popupView.findViewById(R.id.ad_error_layout);
        LinearLayout success=popupView.findViewById(R.id.ad_success_layout);
        Button confirmbtn=popupView.findViewById(R.id.ad_confirm_button);
        Button depositbtn=popupView.findViewById(R.id.ad_deposit_button);
        Button viewCouponbtn=popupView.findViewById(R.id.ad_viewCoupon_button);
        ImageView foodImage=popupView.findViewById(R.id.fc_foodImage);
        TextView foodName=popupView.findViewById(R.id.fc_foodName);
        TextView foodprice=popupView.findViewById(R.id.fc_foodPrice);

        Glide.with(DashBoard.this)
                .load(foodSetGet.getItemImage())
                .into(foodImage);
        foodName.setText(foodSetGet.getFoodName()+"");
        foodprice.setText(foodSetGet.getFoodPrice());

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (userBalance==null){
                   Toast.makeText(DashBoard.this, "Stable internet connection is required to complete this!", Toast.LENGTH_SHORT).show();
               }else{
                   String[] foodp=foodSetGet.getFoodPrice().split(" ");
                   String[] amount=userBalance.split(" ");
                   int availableAmount=Integer.parseInt(amount[0]);
                   int food_price=Integer.parseInt(foodp[0]);
                   if (availableAmount >= food_price){
                       handler.post(() -> {
                           progressDialog = new ProgressDialog(DashBoard.this);
                           progressDialog.setMessage("Loading, Please wait.....Make sure you have a stable internet connection!");
                           progressDialog.setCancelable(false);
                           progressDialog.show();
                       });
                       int salioFinal=availableAmount-food_price;
                       DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("All Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("Details");

                       userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               if (snapshot.exists()) {
                                   // Retrieve user details from Firebase snapshot
                                   String Amount = snapshot.child("Amount").getValue(String.class);
                                   userRef.child("Amount").setValue(salioFinal+" TZS").addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           progressDialog.dismiss();
                                           confirm.setVisibility(View.GONE);
                                           success.setVisibility(View.VISIBLE);
                                           error.setVisibility(View.GONE);
                                           userBalance=salioFinal+"";
                                       }
                                   });
                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {
                               // Handle error
                           }
                       });

                   }else{

                       confirm.setVisibility(View.GONE);
                       success.setVisibility(View.GONE);
                       error.setVisibility(View.VISIBLE);
                   }

               }
            }
        });
        viewCouponbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
dialog.dismiss();
            }
        });
        depositbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                confirm.setVisibility(View.VISIBLE);
//                success.setVisibility(View.GONE);
//                error.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }


}