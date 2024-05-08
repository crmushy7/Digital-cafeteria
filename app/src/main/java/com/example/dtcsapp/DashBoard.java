package com.example.dtcsapp;

import androidx.annotation.NonNull;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Malipo.MPesa.Mpesa;
import Malipo.MPesa.PataSession;


public class DashBoard extends AppCompatActivity {
    private List<FoodSetGet>foodList=new ArrayList<>();
    public static HistoryAdapter historyAdapter;
    public static RecyclerView myHistoryRecyclerView;
    RecyclerView recyclerView;
    Thread thread;
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
    public static int KIASI_MALIPO=0;
    public static int NAMBA_MALIPO=0;
    Handler handler;
    ProgressDialog progressDialog;
    ProgressBar simple_loader,progressbar_history,feedback_loader;
    public static ProgressDialog progressDialog2;
    public static AlertDialog customDialogue;
    public static String customString="null";
    FoodAdapter adapter;
    public static TextView user_Name,user_Pno,ppUsername,ppUsertopphone,ppUserFname,ppUsersmallphone,ppUserLname;
    Button homeBtn,feedbackBtn,settingsBtn,profileBtn;
//    public static SharedPreferences sharedPreferences1=getSharedPreferences("User_data",MODE_PRIVATE);
    public static Context myContext;
    EditText searchEditText;
    LinearLayout dashBoardlayout,settingsLayout,feedbackLayout,dashbordinsideLayout,profileLayout,myhistoryLayout,navigationLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        myContext=DashBoard.this;
        OurTime.init(getApplicationContext());
        UserDetails.init(getApplicationContext());
        refresh();

        simple_loader=findViewById(R.id.progress_dashboard);
        feedback_loader=findViewById(R.id.progress_dashboard_feedback);
        progressbar_history=findViewById(R.id.progress_dashboard1);
        simple_loader.setVisibility(View.VISIBLE);
        AlertDialog.Builder builder=new AlertDialog.Builder(DashBoard.this);
//        View popupView = LayoutInflater.from(DashBoard.this).inflate(R.layout.coupon_with_qrcode, null);
//        builder.setView(popupView);
        builder.setMessage(customString);
        customDialogue=builder.create();
        user_email=UserDetails.getEmail();
        user_dob=UserDetails.getDob();
        user_gender=UserDetails.getGender();
        user_profilePic=UserDetails.getProfilePic();
        String[] salio=UserDetails.getAmount().split(" ");

        handler=new Handler(Looper.getMainLooper());
        ImageView topProfilePic=findViewById(R.id.db_topProfilepic);
        ImageView cardProfilePic=findViewById(R.id.db_cardProfilepic);
        user_Name=findViewById(R.id.db_userName);
        TextView user_Email=findViewById(R.id.db_userEmail);
        user_Pno=findViewById(R.id.db_userphoneNumber);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        adapter=new FoodAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        meal_clock=(TextView) findViewById(R.id.clocktv);
        meal_status=(TextView) findViewById(R.id.mealStatustv);
       Button breakfast=(Button)findViewById(R.id.breakfastbtn);
       Button lunch=(Button)findViewById(R.id.lunchbtn);
       Button dinner=(Button)findViewById(R.id.dinnerbtn);
        TextView viewBalanance = (TextView) findViewById(R.id.viewBalance);
        TextView accountBalance = (TextView) findViewById(R.id.accountBalance);
       hideBalance = viewBalanance.getText().toString();
        homeBtn = (Button) findViewById(R.id.homeBtn);
        feedbackBtn = (Button) findViewById(R.id.feedbackBtn);
        settingsBtn = (Button) findViewById(R.id.settingsBtn);
        profileBtn = (Button) findViewById(R.id.profileBtn);
        dashBoardlayout = (LinearLayout) findViewById(R.id.dashBoardLayout);
        settingsLayout = (LinearLayout) findViewById(R.id.settingsLayout);
        feedbackLayout = (LinearLayout) findViewById(R.id.feedbackLayout);
        dashbordinsideLayout = (LinearLayout) findViewById(R.id.dashbordInsideLayout);
        profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
        TextView viewBalanance1 = (TextView) findViewById(R.id.viewBalance1);
        TextView accountBalance1 = (TextView) findViewById(R.id.accountBalance1);
        user_Name.setText(fullName+"");
        user_Email.setText(user_email);

        user_Pno.setText(phonenumber);
        myhistoryLayout = (LinearLayout) findViewById(R.id.myhistoryLayout);
        navigationLayout = (LinearLayout) findViewById(R.id.navigationLayout);

        TextView backtoprofile = (TextView) findViewById(R.id.backtoprofiletv);
        ImageView topPic=findViewById(R.id.pp_topProfilePic);
        ImageView smallPic=findViewById(R.id.pp_cardProfilePic);
        ppUsername=findViewById(R.id.pp_userName);
        TextView ppUseremail=findViewById(R.id.pp_userEmail);
        ppUsertopphone=findViewById(R.id.pp_userphone);
        ppUserFname=findViewById(R.id.pp_userFname);
        ppUserLname=findViewById(R.id.pp_userLname);
        ppUsersmallphone=findViewById(R.id.pp_userNewPhone);
        TextView ppUsersmallemail=findViewById(R.id.pp_userNewEmail);
        TextView ppUserdob=findViewById(R.id.pp_userDOB);

        searchEditText = findViewById(R.id.searchbar);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called when the text is changed
                String query = s.toString().trim();
                searchMenu(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text is changed
            }
        });

        EditText feedb=findViewById(R.id.feedback_et);
        Button submtfdbck=findViewById(R.id.submitfeedback);
        submtfdbck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String statement=feedb.getText().toString().trim();
                if (statement.isEmpty()){
                    feedb.setError("Required!");
                }else{
                    feedb.setVisibility(View.GONE);
                    feedback_loader.setVisibility(View.VISIBLE);
                    feedb.setText("");
                    DatabaseReference feedbackRef=FirebaseDatabase.getInstance().getReference().child("Feedback").push();
                    feedbackRef.child("Feedback").setValue(statement).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            feedback_loader.setVisibility(View.GONE);
                            feedb.setVisibility(View.VISIBLE);
                            Toast.makeText(DashBoard.this, "Thank You! Your feedback is received!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        handler.post(() -> {
            progressDialog = new ProgressDialog(DashBoard.this);
            progressDialog.setMessage("Loading, Please wait...Make sure you have a stable internet connection!");
            progressDialog.setCancelable(false);
        });
        handler.post(() -> {
            progressDialog2 = new ProgressDialog(DashBoard.this);
            progressDialog2.setMessage("Loading, Please wait...Make sure you have a stable internet connection!");
            progressDialog2.setCancelable(false);
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
                viewHistoryAll();

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
                LinearLayout deposit=findViewById(R.id.dashboard_deposit);
                deposit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        depositDialogue();
                    }
                });


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
                UserDetails.init(getApplicationContext());
                userBalance=UserDetails.getAmount();
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
                updateUser("Firstname");
            }
        });
        update_lname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser("Lastname");
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
                UserDetails.init(getApplicationContext());
                userBalance=UserDetails.getAmount();
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
                    simple_loader.setVisibility(View.VISIBLE);
                    foodList.clear();
                    DatabaseReference breakfastRef = FirebaseDatabase.getInstance().getReference()
                            .child("MENUS")
                            .child("Breakfast");

                    breakfastRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            foodList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String menuPrice = dataSnapshot.child("price").getValue(String.class);
                                String menuName = dataSnapshot.child("foodName").getValue(String.class);
                                String menuUrl = dataSnapshot.child("menuImage").getValue(String.class);
                                String menuAvailability = dataSnapshot.child("statusMode").getValue(String.class);

                                FoodSetGet foodSetGet=new FoodSetGet(menuPrice+" TZS", menuName,"VIP",menuUrl,menuAvailability);
                                foodList.add(foodSetGet);
                            }
                            adapter.updateData(foodList);
                            Collections.reverse(foodList);
                            adapter.notifyDataSetChanged();
                            simple_loader.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled event if needed
                        }
                    });
                    break;

                case "Lunch":
                    breakfast.setBackgroundResource(R.drawable.viewbalance);
                    breakfast.setTextColor(getResources().getColor(R.color.black));
                    lunch.setBackgroundResource(R.drawable.foodback);
                    lunch.setTextColor(getResources().getColor(R.color.white));
                    dinner.setBackgroundResource(R.drawable.viewbalance);
                    dinner.setTextColor(getResources().getColor(R.color.black));
                    foodList.clear();
                    DatabaseReference lunchref = FirebaseDatabase.getInstance().getReference()
                            .child("MENUS")
                            .child("Lunch");

                    lunchref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            foodList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String menuPrice = dataSnapshot.child("price").getValue(String.class);
                                String menuName = dataSnapshot.child("foodName").getValue(String.class);
                                String menuUrl = dataSnapshot.child("menuImage").getValue(String.class);
                                String menuAvailability = dataSnapshot.child("statusMode").getValue(String.class);

                                FoodSetGet foodSetGet=new FoodSetGet(menuPrice+" TZS", menuName,"VIP",menuUrl,menuAvailability);
                                foodList.add(foodSetGet);
                            }
                            adapter.updateData(foodList);
                            Collections.reverse(foodList);
                            adapter.notifyDataSetChanged();
                            simple_loader.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled event if needed
                        }
                    });
                    break;


                case "Dinner":
                    breakfast.setBackgroundResource(R.drawable.viewbalance);
                    breakfast.setTextColor(getResources().getColor(R.color.black));
                    lunch.setBackgroundResource(R.drawable.viewbalance);
                    lunch.setTextColor(getResources().getColor(R.color.black));
                    dinner.setBackgroundResource(R.drawable.foodback);
                    dinner.setTextColor(getResources().getColor(R.color.white));
                    foodList.clear();
                    DatabaseReference dinnerRef = FirebaseDatabase.getInstance().getReference()
                            .child("MENUS")
                            .child("Dinner");

                    dinnerRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            foodList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String menuPrice = dataSnapshot.child("price").getValue(String.class);
                                String menuName = dataSnapshot.child("foodName").getValue(String.class);
                                String menuUrl = dataSnapshot.child("menuImage").getValue(String.class);
                                String menuAvailability = dataSnapshot.child("statusMode").getValue(String.class);

                                FoodSetGet foodSetGet=new FoodSetGet(menuPrice+" TZS", menuName,"VIP",menuUrl,menuAvailability);
                                foodList.add(foodSetGet);
                            }
                            adapter.updateData(foodList);
                            Collections.reverse(foodList);
                            adapter.notifyDataSetChanged();
                            simple_loader.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled event if needed
                        }
                    });
                    break;


                default:
                    break;

            }
        }

        adapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, FoodSetGet foodSetGet) {
                String text=foodSetGet.getMenuAvailability()+"";
                if (text.equals("Available")){
                    alertdialogBuilder(foodSetGet);
                }else{
                    Toast.makeText(DashBoard.this, foodSetGet.getFoodName()+" not available", Toast.LENGTH_SHORT).show();
                }

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

               simple_loader.setVisibility(View.VISIBLE);
                foodList.clear();

               DatabaseReference breakfastRef = FirebaseDatabase.getInstance().getReference()
                       .child("MENUS")
                       .child("Breakfast");

               breakfastRef.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       foodList.clear();
                       for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                           String menuPrice = dataSnapshot.child("price").getValue(String.class);
                           String menuName = dataSnapshot.child("foodName").getValue(String.class);
                           String menuUrl = dataSnapshot.child("menuImage").getValue(String.class);
                           String menuAvailability = dataSnapshot.child("statusMode").getValue(String.class);

                           FoodSetGet foodSetGet=new FoodSetGet(menuPrice+" TZS", menuName,"VIP",menuUrl,menuAvailability);
                           foodList.add(foodSetGet);
                       }
                       adapter.updateData(foodList);
                       Collections.reverse(foodList);
                       adapter.notifyDataSetChanged();
                       simple_loader.setVisibility(View.GONE);
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {
                       // Handle onCancelled event if needed
                   }
               });
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
               simple_loader.setVisibility(View.VISIBLE);
               foodList.clear();

               DatabaseReference breakfastRef = FirebaseDatabase.getInstance().getReference()
                       .child("MENUS")
                       .child("Lunch");

               breakfastRef.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       foodList.clear();
                       for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                           String menuPrice = dataSnapshot.child("price").getValue(String.class);
                           String menuName = dataSnapshot.child("foodName").getValue(String.class);
                           String menuUrl = dataSnapshot.child("menuImage").getValue(String.class);
                           String menuAvailability = dataSnapshot.child("statusMode").getValue(String.class);

                           FoodSetGet foodSetGet=new FoodSetGet(menuPrice+" TZS", menuName,"VIP",menuUrl,menuAvailability);
                           foodList.add(foodSetGet);
                       }
                       adapter.updateData(foodList);
                       Collections.reverse(foodList);
                       adapter.notifyDataSetChanged();
                       simple_loader.setVisibility(View.GONE);
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {
                       // Handle onCancelled event if needed
                   }
               });
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
               simple_loader.setVisibility(View.VISIBLE);
               foodList.clear();

               DatabaseReference breakfastRef = FirebaseDatabase.getInstance().getReference()
                       .child("MENUS")
                       .child("Dinner");

               breakfastRef.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       foodList.clear();
                       for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                           String menuPrice = dataSnapshot.child("price").getValue(String.class);
                           String menuName = dataSnapshot.child("foodName").getValue(String.class);
                           String menuUrl = dataSnapshot.child("menuImage").getValue(String.class);
                           String menuAvailability = dataSnapshot.child("statusMode").getValue(String.class);

                           FoodSetGet foodSetGet=new FoodSetGet(menuPrice+" TZS", menuName,"VIP",menuUrl,menuAvailability);
                           foodList.add(foodSetGet);
                       }
                       adapter.updateData(foodList);
                       Collections.reverse(foodList);
                       adapter.notifyDataSetChanged();
                       simple_loader.setVisibility(View.GONE);
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {
                       // Handle onCancelled event if needed
                   }
               });
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
                                if(currentHour>=0 && currentHour<12)
                                {
                                    meal_status.setText("BreakFast");
                                }else if(currentHour>=12 && currentHour<16)
                                {
                                    meal_status.setText("Lunch");
                                } else if (currentHour>=16 && currentHour<24) {
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


    public static void newToast(String response){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(DashBoard.myContext, "Your message", Toast.LENGTH_SHORT).show();
//            }
//        });
//        Toast.makeText(myContext, response+"", Toast.LENGTH_SHORT).show();
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
        if (updateType=="Firstname"){
            nametv2.setVisibility(View.GONE);
            name_et2.setVisibility(View.GONE);
            numbertv.setVisibility(View.GONE);
            number_et.setVisibility(View.GONE);
            passwordtv.setVisibility(View.GONE);
            password_et.setVisibility(View.GONE);
        } else if (updateType.equals("Lastname")) {
            nametv.setVisibility(View.GONE);
            name_et.setVisibility(View.GONE);
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
                if (updateType=="Firstname"){
                    String new_userName=name_et.getText().toString();

                    if (new_userName.isEmpty()){
                        progressDialog.dismiss();
                        name_et.setError("Fill this!");
                    }else{
                        String newName=new_userName;
                        updateToFirebase(newName,updateType);
                    }
                } else if (updateType=="Lastname") {
                    String new_userName=name_et2.getText().toString();

                    if (new_userName.isEmpty()){
                        progressDialog.dismiss();
                        name_et2.setError("Fill this!");
                    }else{
                        String newName=new_userName;
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
                    if (updateChild.equals("Firstname")||updateChild.equals("Lastname")){
                        String userExistingName=UserDetails.getFullName();
                        String[] specificN=userExistingName.split(" ");
                        if (updateChild.equals("Firstname")){
                            String newName=newData+" "+specificN[1];
                            userRef.child("Fullname").setValue(newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                            SharedPreferences sharedPreferences=getSharedPreferences("User_data",Context.MODE_PRIVATE);
                                            String amount=sharedPreferences.getString("full_name",null);
                                            SharedPreferences.Editor editor= sharedPreferences.edit();
                                            editor.putString("full_name",newName+"");
                                            editor.apply();
                                            UserDetails.init(getApplicationContext());
                                            refresh();
                                            Toast.makeText(DashBoard.this, "Updated successfully", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                            dialog.dismiss();

                                    }
                                }
                            });
                        }else{
                            String newName=specificN[0]+" "+newData;
                            userRef.child("Fullname").setValue(newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        SharedPreferences sharedPreferences=getSharedPreferences("User_data",Context.MODE_PRIVATE);
                                        String amount=sharedPreferences.getString("full_name",null);
                                        SharedPreferences.Editor editor= sharedPreferences.edit();
                                        editor.putString("full_name",newName+"");
                                        editor.apply();
                                        UserDetails.init(getApplicationContext());
                                        refresh();
                                        Toast.makeText(DashBoard.this, "Updated successfully", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        dialog.dismiss();

                                    }
                                }
                            });
                        }
                    }else {
                        userRef.child(updateChild).setValue(newData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (updateChild == "Password") {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        AuthCredential credential = EmailAuthProvider.getCredential(UserDetails.getEmail(), UserDetails.getPassword());
                                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (user != null) {
                                                        user.updatePassword(newData + "").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(DashBoard.this, "Updated successfully", Toast.LENGTH_LONG).show();
                                                                    SharedPreferences sharedPreferences = getSharedPreferences("User_data", MODE_PRIVATE);
                                                                    String amount = sharedPreferences.getString("password", null);
                                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                    editor.putString("Password", newData + "");
                                                                    editor.apply();
                                                                    progressDialog.dismiss();
                                                                } else {
                                                                    progressDialog.dismiss();
                                                                    Exception exception = task.getException();
                                                                    Log.d("TAG", "Error updating password" + exception);
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(DashBoard.this, "Try again", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    }
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(DashBoard.this, "Password wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } else if (updateChild == "Fullname") {
                                        SharedPreferences sharedPreferences = getSharedPreferences("User_data", Context.MODE_PRIVATE);
                                        String amount = sharedPreferences.getString("full_name", null);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("full_name", newData + "");
                                        editor.apply();
                                        UserDetails.init(getApplicationContext());
                                        refresh();
                                        Toast.makeText(DashBoard.this, "Updated successfully", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                    } else {
                                        SharedPreferences sharedPreferences = getSharedPreferences("User_data", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("phone_number", newData + "");
                                        editor.apply();
                                        UserDetails.init(getApplicationContext());
                                        refresh();
                                        Toast.makeText(DashBoard.this, "Updated successfully", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DashBoard.this, "Update failed,please try again later", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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
        TextView dismissbutton=popupView.findViewById(R.id.ad_dismissbtn);
        TextView alertmessage=popupView.findViewById(R.id.fc_alertMessage);

        alertmessage.setText(foodSetGet.getFoodPrice()+" will be deducted from your account");

        Glide.with(DashBoard.this)
                .load(foodSetGet.getItemImage())
                .into(foodImage);
        foodName.setText(foodSetGet.getFoodName()+"");
        foodprice.setText(foodSetGet.getFoodPrice());

        dismissbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView alertmessageSucces=popupView.findViewById(R.id.fc_foodStatus);

        alertmessageSucces.setText(foodSetGet.getFoodPrice()+" deducted from your account");
        TextView dismissbuttonSucces=popupView.findViewById(R.id.ad_dismisSucces);
        dismissbuttonSucces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView dismissbuttonError=popupView.findViewById(R.id.ad_dismissError);
        dismissbuttonError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


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
                        progressDialog2.show();
                        handler.post(() -> {
                            progressDialog = new ProgressDialog(DashBoard.this);
                            progressDialog.setMessage("Loading, Please wait.....Make sure you have a stable internet connection!");
                            progressDialog.setCancelable(false);
//                            progressDialog.show();
                        });
                        int salioFinal=availableAmount-food_price;


//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                progressDialog2.dismiss();
//                                Toast.makeText(DashBoard.this, "please Check your internet connection", Toast.LENGTH_SHORT).show();
//                            }
//                        },10000);


                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                confirm.setVisibility(View.GONE);
                                success.setVisibility(View.VISIBLE);
                                error.setVisibility(View.GONE);
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("All Users")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("Details");

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            // Retrieve user details from Firebase snapshot
                                            String Amount = snapshot.child("Amount").getValue(String.class);
                                            userRef.child("Amount").setValue(salioFinal+" TZS").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    SharedPreferences sharedPreferences=getSharedPreferences("User_data",MODE_PRIVATE);
                                                    String amount=sharedPreferences.getString("Amount",null);
                                                    SharedPreferences.Editor editor= sharedPreferences.edit();
                                                    editor.putString("Amount",salioFinal+" TZS");
                                                    editor.apply();
                                                    UserDetails.init(getApplicationContext());
                                                    refresh();
                                                    TextView alertmessageSucces=popupView.findViewById(R.id.fc_foodStatus);

                                                    alertmessageSucces.setText(foodSetGet.getFoodPrice()+" deducted from your account");



                                                    userBalance=salioFinal+"";
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    handler.removeCallbacksAndMessages(null);
                                                    coupon.generateCoupon(getApplicationContext(),foodSetGet);
                                                    Toast.makeText(DashBoard.this, "success", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(DashBoard.this, "Failed due to "+e, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else{

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle error
                                    }
                                });

                                progressDialog.dismiss();
                            }
                        },5000);



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
            viewHistoryAll();

            }
        });
        depositbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                confirm.setVisibility(View.VISIBLE);
//                success.setVisibility(View.GONE);
//                error.setVisibility(View.GONE);
                dialog.dismiss();
                depositDialogue();

            }
        });

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void depositDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
        View popupView = LayoutInflater.from(DashBoard.this).inflate(R.layout.deposit, null);

        EditText mobileNumber = popupView.findViewById(R.id.dep_mobilenumber);
        EditText amount = popupView.findViewById(R.id.dep_amount);
        TextView depTitle = popupView.findViewById(R.id.dep_title);
        ImageView imageView=popupView.findViewById(R.id.dep_image);
        Button proceedtoDeposit = popupView.findViewById(R.id.dep_confirm_button);
        LinearLayout airtel=popupView.findViewById(R.id.dep_airtel);
        LinearLayout vodacom=popupView.findViewById(R.id.dep_vodacom);
        LinearLayout halotel=popupView.findViewById(R.id.dep_halotel);
        LinearLayout tigo=popupView.findViewById(R.id.dep_tigo);
        LinearLayout choosemethod=popupView.findViewById(R.id.dep_choosemethod);
        LinearLayout choosenMethod=popupView.findViewById(R.id.dep_choosenMethod);
        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();

        airtel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                choosemethod.setVisibility(View.GONE);
//                choosenMethod.setVisibility(View.VISIBLE);
//                depTitle.setText("Deposit via Airtel Money");
//                Glide.with(DashBoard.this)
//                        .load(R.drawable.airtelmoney)
//                        .into(imageView);
                Toast.makeText(DashBoard.this, "Coming soon, please use vodacom to deposit", Toast.LENGTH_LONG).show();
            }
        });
        vodacom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosemethod.setVisibility(View.GONE);
                choosenMethod.setVisibility(View.VISIBLE);
                depTitle.setText("Deposit via Mpesa");
                Glide.with(DashBoard.this)
                        .load(R.drawable.mpesa)
                        .into(imageView);
            }
        });
        halotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                choosemethod.setVisibility(View.GONE);
//                choosenMethod.setVisibility(View.VISIBLE);
//                depTitle.setText("Deposit via Halopesa");
//                Glide.with(DashBoard.this)
//                        .load(R.drawable.halopesa)
//                        .into(imageView);
                Toast.makeText(DashBoard.this, "Coming soon, please use vodacom to deposit", Toast.LENGTH_LONG).show();
            }
        });
        tigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                choosemethod.setVisibility(View.GONE);
//                choosenMethod.setVisibility(View.VISIBLE);
//                depTitle.setText("Deposit via Tigo Pesa");
//                Glide.with(DashBoard.this)
//                        .load(R.drawable.tigopesa)
//                        .into(imageView);
                Toast.makeText(DashBoard.this, "Coming soon, please use vodacom to deposit", Toast.LENGTH_LONG).show();
            }
        });
        proceedtoDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number=mobileNumber.getText().toString();
                String kiasi=amount.getText().toString();
                if (number.isEmpty()){
                    mobileNumber.setError("Required");
                } else if (number.length()<10) {
                    mobileNumber.setError("Must be 10 numbers");
                } else if (kiasi.isEmpty()) {
                    amount.setError("Required");
                }else{
                    String nambampya=number.substring(1);
                    int finalAmount = Integer.parseInt(kiasi);
                    int finalNumber=Integer.parseInt(nambampya);
                    if (finalAmount<1000){
                        amount.setError("amount must start from 1000");
                    }else {
                        KIASI_MALIPO=finalAmount;
                        NAMBA_MALIPO=finalNumber;
                        progressDialog2.show();
                        Mpesa mpesa=new Mpesa();
                        mpesa.getSession();
                        dialog.dismiss();







//
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                Toast.makeText(DashBoard.this, Deposit.FINALRESP+"", Toast.LENGTH_SHORT).show();


                            }
                        }, 10000);
                    }
                }
            }
        });

    }
    public void threadDestroy(){
        thread.interrupt();
    }

    public void refresh(){
        userBalance=UserDetails.getAmount();
        ppUsername=findViewById(R.id.pp_userName);
        ppUsertopphone=findViewById(R.id.pp_userphone);
        ppUserFname=findViewById(R.id.pp_userFname);
        ppUserLname=findViewById(R.id.pp_userLname);
        ppUsersmallphone=findViewById(R.id.pp_userNewPhone);
        user_Name=findViewById(R.id.db_userName);
        user_Pno=findViewById(R.id.db_userphoneNumber);
        fullName=UserDetails.getFullName();
        String[] parts= fullName.split(" ");
        phonenumber= UserDetails.getPhoneNumber();
        user_Name.setText(fullName+"");
        user_Pno.setText(phonenumber+"");
        ppUsername.setText(fullName);
        ppUsertopphone.setText(phonenumber);
        ppUserFname.setText(parts[0]);
        ppUserLname.setText(parts[1]);
        ppUsersmallphone.setText(phonenumber);
    }

    public void couponHistory(Context context) {

        progressbar_history.setVisibility(View.VISIBLE);

        historyAdapter = new HistoryAdapter(new ArrayList<>());
        myHistoryRecyclerView.setAdapter(historyAdapter);

        DatabaseReference couponHistoryRef = FirebaseDatabase.getInstance().getReference()
                .child("Coupons")
                .child(FirebaseAuth.getInstance().getUid());

        couponHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HistorySetGet> historyList = new ArrayList<>();
                int totalSpent=0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String menuName = dataSnapshot.child("Menu Name").getValue(String.class);
                    String menuDate = dataSnapshot.child("Menu Time").getValue(String.class);
                    String menuPrice = dataSnapshot.child("Menu Price").getValue(String.class);
                    String menuReference = dataSnapshot.getKey().toString();
                    String menuStatus = dataSnapshot.child("Status").getValue(String.class);
                    String menuServetime = dataSnapshot.child("Served Time").getValue(String.class);
                    String couponNumber = dataSnapshot.child("Coupon Number").getValue(String.class);

                    if (menuPrice !=null){
                        String[] amount=menuPrice.split(" ");
                        int actualAmount=Integer.parseInt(amount[0]);
                        totalSpent=totalSpent+actualAmount;
                    }

                    HistorySetGet historySetGet = new HistorySetGet(menuName, menuPrice, menuReference, menuDate,menuStatus,menuServetime,couponNumber);
                    historyList.add(historySetGet);


                }
                Collections.reverse(historyList); // Reverse the list after updating
                historyAdapter.updateData(historyList);
                DatabaseReference userDb=FirebaseDatabase.getInstance().getReference().child("All Users")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child("Details");
                userDb.child("Used Amount").setValue("TZS "+totalSpent);

                progressbar_history.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event if needed
            }
        });

        historyAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, HistorySetGet historySetGet) {
                if (historySetGet.getCoupon_status().equals("pending")){
                    AlertDialog.Builder builder=new AlertDialog.Builder(DashBoard.this);
                    View popupView = LayoutInflater.from(DashBoard.this).inflate(R.layout.coupon_with_qrcode, null);
                    builder.setView(popupView);


                    TextView couponID=popupView.findViewById(R.id.cwq_couponID);
                    TextView dismissbtn=popupView.findViewById(R.id.cwq_dismissbtn);
                    ImageView qrcodeImage=popupView.findViewById(R.id.cwq_qrCode);

                    couponID.setText("ID: "+historySetGet.getCoupon_reference_Number());


                    Bitmap qrCode=coupon.generateQRCodeBitmap(historySetGet);

                    Glide.with(DashBoard.this)
                            .load(qrCode)
                            .into(qrcodeImage);

                    dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                    dismissbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }else {
                    Toast.makeText(context, historySetGet.getCoupon_status()+"!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public void viewHistoryAll(){
        progressbar_history.setVisibility(View.VISIBLE);
        // Set colors and backgrounds for buttons
        settingsBtn.setTextColor(getResources().getColor(R.color.black));
        settingsBtn.setBackgroundResource(R.color.white);
        homeBtn.setTextColor(getResources().getColor(R.color.black));
        homeBtn.setBackgroundResource(R.color.white);
        profileBtn.setTextColor(getResources().getColor(R.color.white));
        profileBtn.setBackgroundResource(R.drawable.time);
        feedbackBtn.setTextColor(getResources().getColor(R.color.black));
        feedbackBtn.setBackgroundResource(R.color.white);

        // Hide other layouts and show the coupon history layout
        dashbordinsideLayout.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.GONE);
        feedbackLayout.setVisibility(View.GONE);
        dashBoardlayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.GONE);
        myhistoryLayout.setVisibility(View.VISIBLE);
        navigationLayout.setVisibility(View.GONE);



        // Call the couponHistory method to populate the RecyclerView with coupon history
        myHistoryRecyclerView =findViewById(R.id.recyclerviewHistory);
        myHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(DashBoard.this));
        couponHistory(getApplicationContext());
        TextView totalSpent=findViewById(R.id.my_totalSpends);
        TextView totalbalance=findViewById(R.id.mh_mybalance);
        DatabaseReference userDb=FirebaseDatabase.getInstance().getReference().child("All Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("Details");
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String spent=snapshot.child("Used Amount").getValue(String.class);
                String balance=snapshot.child("Amount").getValue(String.class);
                totalSpent.setText(spent+".00");
                totalbalance.setText(balance+"");
                progressbar_history.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void searchMenu(String query) {
        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference().child("MENUS");

        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                foodList.clear();
                boolean foundMatch = false;
                for (DataSnapshot mealSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot menuItemSnapshot : mealSnapshot.getChildren()) {
                        // Retrieve data from Firebase
                        String menuName = menuItemSnapshot.child("foodName").getValue(String.class);
                        String menuPrice = menuItemSnapshot.child("price").getValue(String.class);
                        String menuImage = menuItemSnapshot.child("menuImage").getValue(String.class);
                        String menuStatus = menuItemSnapshot.child("statusMode").getValue(String.class);
                        String menuID = menuItemSnapshot.getKey();

                        // Check if menu name matches the query
                        if (menuName != null && menuName.toLowerCase().contains(query.toLowerCase())) {
                            foundMatch = true;
                            foodList.clear();
                            FoodSetGet foodSetGet = new FoodSetGet(menuPrice + " TZS", menuName, "VIP", menuImage,menuStatus);
                            foodList.add(foodSetGet);
                        }
                    }

                }
                    // Update RecyclerView with search results
                    if (foodList.isEmpty()) {
                        // No matching items found
                        adapter.setClickable(false);
                        foodList.clear();
                        showNoMatchingItemsMessage();
                        // Make adapter unclickable
                    } else {
                        adapter.updateData(foodList);
                        Collections.reverse(foodList);
                        adapter.setClickable(true); // Make adapter clickable
                        adapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    private void showNoMatchingItemsMessage() {

//        recyclerView.setVisibility(View.GONE);
        // Display a toast message indicating no matching items found
        Toast.makeText(DashBoard.this, "Item does not exist!", Toast.LENGTH_SHORT).show();
    }



}