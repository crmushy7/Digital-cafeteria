package com.example.dtcsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    Button next,next2,next3;
    public static Bitmap userPhoto;
    TextView sigin;
    TextView backbtn,signin,dob,back3;

    DatePicker dobpk;
    Spinner gender;
    public static String fullName;
    public static String uploadedPicID;
    public static String user_email;
    public static String phonenumber;
    public static String userPassword;
    public static String user_dob;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private ImageView imageView;
    private Uri imageUri;
    private AlertDialog dialog;
    public static String userGender="";
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Handler handler;
    ProgressDialog progressDialog;
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";
    Pattern pat = Pattern.compile(emailRegex);
    private static final long TIME_INTERVAL = 2000;
    private long mBackPressed;
    ImageView reg_profile;
    LinearLayout linearLayoutOne,linearLayoutTwo,linearLayoutThree,loginLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        firebaseAuth= FirebaseAuth.getInstance();
        handler=new Handler(Looper.getMainLooper());
        next=(Button) findViewById(R.id.btnNext);
        next2=(Button)findViewById(R.id.btn2Next);
        next3=(Button)findViewById(R.id.registerbtn);
        linearLayoutOne=(LinearLayout)findViewById(R.id.lineraLayout1);
        linearLayoutTwo=(LinearLayout)findViewById(R.id.lineraLayout2);
        linearLayoutThree=(LinearLayout)findViewById(R.id.lineraLayout3);
        LinearLayout createlayout = (LinearLayout) findViewById(R.id.createLayout);
        loginLayout=(LinearLayout)findViewById(R.id.loginLayout) ;
        backbtn=(TextView)findViewById(R.id.back2) ;
        gender=(Spinner)findViewById(R.id.gendersp);
        dobpk=(DatePicker)findViewById(R.id.dobPicker);
        dob=(TextView) findViewById(R.id.dobEt);
        back3=(TextView) findViewById(R.id.btnback3);
        signin=(TextView)findViewById(R.id.sgninTv);
        TextView welcome = (TextView) findViewById(R.id.welcome);
        TextView accountCreate = (TextView) findViewById(R.id.accountbtn);
        Button login = (Button) findViewById(R.id.loginbtn);
        reg_profile=findViewById(R.id.rp_previewImage);
        EditText fName=findViewById(R.id.rp_firstName);
        EditText lName=findViewById(R.id.rp_lastName);
        EditText pNumber=findViewById(R.id.rp_phoneNumber);
        EditText userEmail=findViewById(R.id.rp_email);
        EditText pass=findViewById(R.id.rp_password);
        EditText confPass=findViewById(R.id.rp_confirmPassword);
        EditText loginEmail=findViewById(R.id.rp_signinEmail);
        EditText loginPass=findViewById(R.id.rp_signinPassword);
        TextView dateofBirth=findViewById(R.id.dobEt);


        reg_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(Registration.this);
                View view= LayoutInflater.from(Registration.this).inflate(R.layout.choose_image,null);
                builder.setView(view);

                LinearLayout chooseFromFilebtn=view.findViewById(R.id.upl_choosefromFile);
                LinearLayout takeCamerabtn=view.findViewById(R.id.upl_choosefromCamera);
                Button confirm=view.findViewById(R.id.ci_confirmbtn);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imageUri != null){
                            dialog.dismiss();
                        }else {
                            dialog.dismiss();

                        }
                    }
                });
                imageView = view.findViewById(R.id.upl_previewImage);

                chooseFromFilebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseFromFileManager(v);
                    }
                });
                takeCamerabtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePhoto(v);
                    }
                });
                dialog=builder.create();
                dialog.show();
                reg_profile.setImageBitmap(userPhoto);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=loginEmail.getText().toString().trim();
                String password=loginPass.getText().toString().trim();
                if (email.length()==0){
                    loginEmail.setError("Write your email");
                } else if (password.length()==0) {
                    loginPass.setError("fill password");
                }
                else{

                    handler.post(() -> {
                        progressDialog = new ProgressDialog(Registration.this);
                        progressDialog.setMessage("Loading, Please wait...Make sure you have a stable internet connection!");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    });
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){


//        getting deviceID ili tutume notifications
                                        FirebaseMessaging.getInstance().getToken()
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful() && task2.getResult() != null) {
                                                        String fcmToken = task2.getResult();
//                        sending it to firebase ya user
                                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("All Users")
                                                                .child(FirebaseAuth.getInstance().getUid())
                                                                .child("Details");
                                                        userRef.child("FCM Token").setValue(fcmToken);
                                                    }
                                                });

                                        fetchAndStoreUserData();

                                    }else{
                                        progressDialog.dismiss();
                                        Toast.makeText(Registration.this, "Failed to log in", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }

        });




        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Registration.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dob.setText(dayOfMonth + "/ " + month + " / " + year);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });


        String jinsia[] = {"MALE", "FEMALE"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jinsia);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = jinsia[position];
                userGender=selected;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                userGender="Male";
            }
        });





        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutOne.setVisibility(View.VISIBLE);
                linearLayoutTwo.setVisibility(View.GONE);
                linearLayoutThree.setVisibility(View.GONE);
                backbtn.setVisibility(View.GONE);
                back3.setVisibility(View.GONE);
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutOne.setVisibility(View.GONE);
                linearLayoutTwo.setVisibility(View.GONE);
                linearLayoutThree.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
                createlayout.setVisibility(View.GONE);
                welcome.setVisibility(View.GONE);
            }
        });
        back3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutOne.setVisibility(View.GONE);
                linearLayoutTwo.setVisibility(View.VISIBLE);
                linearLayoutThree.setVisibility(View.GONE);
                backbtn.setVisibility(View.VISIBLE);
                back3.setVisibility(View.GONE);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String first_name=fName.getText().toString();
                String last_name=lName.getText().toString();
                String phone_number=pNumber.getText().toString();
                String email=userEmail.getText().toString();

                if (first_name.isEmpty()){
                    fName.setError("Field required");
                } else if (last_name.isEmpty()) {
                    lName.setError("Field required");
                }else if (phone_number.isEmpty()) {
                    pNumber.setError("Field required");
                }else if (phone_number.trim().length()!=10) {
                    pNumber.setError("10 numbers are required");
                }else if (email.isEmpty()) {
                    userEmail.setError("Field required");
                }else if (!pat.matcher(email).matches()) {
                    userEmail.setError("Please Enter a valid Email");
                    return;
                }else{
                    fullName=first_name+" "+last_name;
                    phonenumber=phone_number;
                    user_email=email;
                    linearLayoutOne.setVisibility(View.GONE);
                    linearLayoutTwo.setVisibility(View.VISIBLE);
                    linearLayoutThree.setVisibility(View.GONE);
                    backbtn.setVisibility(View.VISIBLE);
                    back3.setVisibility(View.GONE);

                }
            }
        });


        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date_birth=dateofBirth.getText().toString();
                String passwd=pass.getText().toString();
                String confp=confPass.getText().toString();

                if (date_birth.isEmpty()){
                    dateofBirth.setError("Field required");
                } else if (passwd.isEmpty()) {
                    pass.setText("Field required");
                } else if (passwd.length()<6) {
                    pass.setError("Must contain atleast 6 characters");
                } else if (confp.isEmpty()) {
                    confPass.setError("Field required");
                } else if (!passwd.equals(confp)) {
                    confPass.setError("Password does not match");
                }else{
                    user_dob=date_birth;
                    userPassword=passwd;
                    linearLayoutOne.setVisibility(View.GONE);
                    linearLayoutTwo.setVisibility(View.GONE);
                    linearLayoutThree.setVisibility(View.VISIBLE);
                    back3.setVisibility(View.VISIBLE);
                    backbtn.setVisibility(View.GONE);
                }


            }
        });
        next3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null){
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("Fullname",fullName);
                    hashMap.put("username",user_email);
                    hashMap.put("PhoneNumber",phonenumber);
                    hashMap.put("Gender",userGender);
                    hashMap.put("Date_of_Birth",user_dob);
                    hashMap.put("Password",userPassword);
                    hashMap.put("Amount","50000 TZs");

                    handler.post(() -> {
                        progressDialog = new ProgressDialog(Registration.this);
                        progressDialog.setMessage("Loading, Please wait...Make sure you have a stable internet connection!");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    });
                    firebaseAuth.createUserWithEmailAndPassword(user_email, userPassword)
                            .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //our database operations here
                                        databaseReference.child("All Users")
                                                .child(firebaseAuth.getUid().toString())
                                                .child("Details")
                                                .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(Registration.this, "Successful", Toast.LENGTH_SHORT).show();
                                                            Toast.makeText(Registration.this, "User Registered!", Toast.LENGTH_LONG).show();
                                                            uploadToFirestore(v);
                                                            linearLayoutOne.setVisibility(View.GONE);
                                                            linearLayoutTwo.setVisibility(View.GONE);
                                                            linearLayoutThree.setVisibility(View.GONE);
                                                            loginLayout.setVisibility(View.VISIBLE);
                                                            createlayout.setVisibility(View.GONE);
                                                            welcome.setVisibility(View.GONE);
                                                            progressDialog.dismiss();
                                                        } else {
                                                            Toast.makeText(Registration.this, "Failed", Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                            Toast.makeText(Registration.this, "Fail! User not registered!", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        progressDialog.dismiss();

                                        // If sign in fails, check the exception and handle specific errors
                                        if (task.getException() != null && task.getException() instanceof FirebaseAuthException) {
                                            FirebaseAuthException firebaseAuthException = (FirebaseAuthException) task.getException();
                                            String errorCode = firebaseAuthException.getErrorCode();
                                            Toast.makeText(Registration.this, "User not registered! "+errorCode, Toast.LENGTH_SHORT).show();
//                                                userEmail.setError("Email already in use");
                                        }
                                    }
                                }
                            });

                }



            }
        });

        accountCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createlayout.setVisibility(View.VISIBLE);
                linearLayoutOne.setVisibility(View.VISIBLE);
                linearLayoutTwo.setVisibility(View.GONE);
                linearLayoutThree.setVisibility(View.GONE);
                backbtn.setVisibility(View.GONE);
                back3.setVisibility(View.GONE);
                loginLayout.setVisibility(View.GONE);
                welcome.setVisibility(View.VISIBLE);
            }
        });
    }


    public void chooseFromFileManager(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void takePhoto(View view) {
        // Check if the camera permission is not granted yet
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request the camera permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, proceed with capturing image
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Image selected from file manager
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            reg_profile.setImageURI(imageUri);
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            // Image captured from camera
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            reg_profile.setImageBitmap(photo);

            // Convert Bitmap to Uri
            imageUri = getImageUri(Registration.this, photo);

        }
    }

    public Uri getImageUri(AppCompatActivity inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void uploadToFirestore(View view) {

        //tu upload firebase kwanza

        if (imageUri != null) {

            handler.post(() -> {
                progressDialog = new ProgressDialog(Registration.this);
                progressDialog.setMessage("Loading, Please wait...Make sure you have a stable internet connection!");
                progressDialog.setCancelable(false);
                progressDialog.show();
            });

            Calendar calendar = Calendar.getInstance();
            String currentdate = DateFormat.getInstance().format(calendar.getTime());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
            String formattedTime = simpleDateFormat.format(new Date());

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child("images/" + FirebaseAuth.getInstance().getUid().toString());

            UploadTask uploadTask = imagesRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                    Toast.makeText(Registration.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                    // Get the download URL
                    imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Image download URL retrieved
                            String imageUrl = uri.toString();
                            DatabaseReference databaseReferenceUpld = FirebaseDatabase.getInstance().getReference().child("All Users")
                                    .child(firebaseAuth.getUid().toString())
                                    .child("Details");
                            databaseReferenceUpld.child("profilePic").setValue(imageUrl);


                            // Save the image URL to Firestore
                            saveImageUrlToFirestore(imageUrl);
                        }
                    });
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Image upload failed
                    progressDialog.dismiss();
                    Toast.makeText(Registration.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        // Add code to save imageUrl to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // For example, you can create a collection named "images" and add imageUrl as a document field
        // You can also add more fields like timestamp, user ID, etc.
        // Replace "collectionName" with your actual collection name
        db.collection("images")
                .add(new ImageModel(imageUrl))
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(Registration.this, "Image URL saved to Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Registration.this, "Error saving image URL to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchAndStoreUserData() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("All Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("Details");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve user details from Firebase snapshot
                    String fullName = snapshot.child("Fullname").getValue(String.class);
                    String phoneNumber = snapshot.child("PhoneNumber").getValue(String.class);
                    String email = snapshot.child("username").getValue(String.class);
                    String gender = snapshot.child("Gender").getValue(String.class);
                    String dob = snapshot.child("Date_of_Birth").getValue(String.class);
                    String password = snapshot.child("Password").getValue(String.class);
                    String FCM_Token = snapshot.child("FCM Token").getValue(String.class);
                    String profilePic = snapshot.child("profilePic").getValue(String.class);
                    String Amount = snapshot.child("Amount").getValue(String.class);
                    uploadedPicID=profilePic;

                    Glide.with(Registration.this)
                            .load(uploadedPicID)
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    Bitmap bitmap=((BitmapDrawable)resource).getBitmap();
                                    File file=new File(Registration.this.getFilesDir(),"profile_image.jpg");
                                    try {
                                        FileOutputStream fileOutputStream=new FileOutputStream(file);
                                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                                        fileOutputStream.flush();
                                        fileOutputStream.close();

                                        String savedImage=file.getAbsolutePath()+"";
                                        // Store user data locally using SharedPreferences
                                        storeUserDataLocally(fullName, phoneNumber, email,gender,password,FCM_Token,savedImage,dob,Amount);
                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });



                } else {
                    // Handle case where user data doesn't exist
                    Toast.makeText(Registration.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(Registration.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeUserDataLocally(String fullName, String phoneNumber, String email,String gender,String password, String FCM_Token,String profilePicture,String dob,String Amount) {
        // Get SharedPreferences object
        SharedPreferences sharedPreferences = getSharedPreferences("User_data", Context.MODE_PRIVATE);

        // Get SharedPreferences editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store user data in SharedPreferences
        editor.putString("full_name", fullName);
        editor.putString("phone_number", phoneNumber);
        editor.putString("email", email);
        editor.putString("gender", gender);
        editor.putString("dob", dob);
        editor.putString("password", password);
        editor.putString("FCM_Token", FCM_Token);
        editor.putString("profilePic", profilePicture);
        editor.putString("Amount", Amount);

        // Commit the changes
        editor.apply();
        // Proceed to main app functionality (e.g., dashboard)
        startDashboardActivity();
    }

    private void startDashboardActivity() {
        // Start your main activity here
        progressDialog.dismiss();
        Toast.makeText(Registration.this, "Successfull", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(Registration.this, DashBoard.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            Intent intent=new Intent(Registration.this, DashBoard.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            mBackPressed = System.currentTimeMillis();
        }
        ;

    }

}