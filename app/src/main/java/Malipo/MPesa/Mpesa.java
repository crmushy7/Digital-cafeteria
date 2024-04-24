package Malipo.MPesa;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.dtcsapp.DashBoard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.os.Handler;

public class Mpesa {
    Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final String GET_SESSION_URL = "https://staffgenie.co.tz/moja/mpesa/getSession.php";
    private static final String MAKE_PAYMENT_URL = "https://staffgenie.co.tz/moja/mpesa/makePayment.php";
    public void getSession(){
        // Make HTTP request in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(GET_SESSION_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Extract JSON part of the response
                    String jsonResponse = extractJsonFromResponse(response.toString());

                    // Parse JSON response
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    // Extract session key
                    String sessionKey = jsonObject.optString("output_SessionID");
                    Log.d("", "SESSION ID: "+sessionKey);

                    // Now make the payment request using the obtained session key
                    makePayment(sessionKey);
                } catch (IOException | JSONException e) {
                    Log.d("","JSON IMEGOMA KWENYE SESSION");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void makePayment(final String sessionKey) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(MAKE_PAYMENT_URL + "?namba=" + DashBoard.NAMBA_MALIPO + "&kiasi=" + DashBoard.KIASI_MALIPO + "&encryptedKey=" + sessionKey);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Extract JSON part of the response
                    String jsonResponse = extractJsonFromResponse(response.toString());

                    // Parse JSON response
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    // Extract output_ResponseDesc
                    String responseDesc = jsonObject.optString("output_ResponseDesc");

                    // Log the response
                    if (responseDesc.trim().equals("Request processed successfully")){
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("All Users")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child("Details");

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // Retrieve user details from Firebase snapshot
                                    String Amount = snapshot.child("Amount").getValue(String.class);
                                    String[] kiasi=Amount.split(" ");
                                    int salioFinal=Integer.parseInt(kiasi[0])+DashBoard.KIASI_MALIPO;
                                    userRef.child("Amount").setValue(salioFinal+" TZS").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Context context1=DashBoard.myContext;
                                            SharedPreferences sharedPreferences=context1.getSharedPreferences("User_data",Context.MODE_PRIVATE);
                                            String amount=sharedPreferences.getString("Amount",null);
                                            SharedPreferences.Editor editor= sharedPreferences.edit();
                                            editor.putString("Amount",salioFinal+" TZS");
                                            editor.apply();

                                            DashBoard.userBalance=salioFinal+"";
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            DashBoard.progressDialog2.dismiss();
                                            Toast.makeText(DashBoard.myContext, "success", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            DashBoard.progressDialog2.dismiss();
                                            Toast.makeText(DashBoard.myContext, "Failed due to "+e, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(DashBoard.myContext, "User does not exist "+"", Toast.LENGTH_SHORT).show();
                                    DashBoard.progressDialog2.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error
                                Toast.makeText(DashBoard.myContext, "failed due to "+error+"", Toast.LENGTH_SHORT).show();
                                DashBoard.progressDialog2.dismiss();
                            }
                        });
                    }else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("response",responseDesc+"");
                                Toast.makeText(DashBoard.myContext, "Transaction failed due to "+responseDesc+"", Toast.LENGTH_LONG).show();
                                DashBoard.progressDialog2.dismiss();
                            }
                        });

//                        DashBoard.newToast(response+"");
                    }

                    // Parse and handle payment response here
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    // Method to extract JSON part of the response
    private String extractJsonFromResponse(String fullResponse) {
        int startIndex = fullResponse.indexOf("{");
        int endIndex = fullResponse.lastIndexOf("}") + 1;
        return fullResponse.substring(startIndex, endIndex);
    }
}

