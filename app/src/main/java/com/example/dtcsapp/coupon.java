package com.example.dtcsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dtcsapp.FoodSetGet;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class coupon {
    public static String uniqueID="";
    public static String couponNumber="";

    public static void generateCoupon(Context context, FoodSetGet foodSetGet) {
        uniqueID=UniqueIDGenerator.generateUniqueID();
        Calendar calendar = Calendar.getInstance();
        String currentdate = DateFormat.getInstance().format(calendar.getTime());
        String[] dateSeparation=currentdate.split(" ");
        String dateOnlyFull=dateSeparation[0]+"";
        String[] tarehe=dateOnlyFull.split("/");
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Adding 1 because January is represented as 0
        int year = calendar.get(Calendar.YEAR);
        String dateOnly=day+"-"+month+"-"+year;

        DatabaseReference couponNumberRef = FirebaseDatabase.getInstance().getReference().child("Coupons")
                .child("Coupons Used")
                .child(dateOnly);
        couponNumberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String usedtoday=snapshot.child("Used Today").getValue(String.class);
                String usedtotal=snapshot.child("Used Total").getValue(String.class);
                if (snapshot.exists()) {
                        if (usedtoday==null){
                            couponNumberRef.child("Used Today").setValue("1 sold");
                            couponNumberRef.child("Used Total").setValue("1 sold");
                            couponNumber="1";
                        }else{
                            String[] usedtodayString=usedtoday.split(" ");
                            String[] usedtotalString=usedtotal.split(" ");
                            int newCount_today=Integer.parseInt(usedtodayString[0])+1;
                            int newCount_total=Integer.parseInt(usedtotalString[0])+1;
                            couponNumber=newCount_today+"";
                            couponNumberRef.child("Used Today").setValue(newCount_today+" sold").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference()
                                            .child("Coupons")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .push(); // Generate a unique key for the coupon

                                    couponRef.child("Menu Name").setValue(foodSetGet.getFoodName());
                                    couponRef.child("Menu Time").setValue(currentdate+"Hrs");
                                    couponRef.child("Menu Price").setValue(foodSetGet.getFoodPrice());
                                    couponRef.child("Status").setValue("pending");
                                    couponRef.child("Reference Number").setValue(uniqueID);
                                    couponRef.child("Served Time").setValue("Not served");
                                    couponRef.child("Coupon Number").setValue(couponNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            DatabaseReference soldNumberRef = FirebaseDatabase.getInstance().getReference().child("Coupons")
                                                    .child("Coupons Used")
                                                    .child(dateOnly).child(foodSetGet.getFoodName());
                                            soldNumberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        String usedtoday1=snapshot.getValue(String.class);
                                                        String[] usedtodayString1=usedtoday1.split(" ");
                                                        int newCount_today1=Integer.parseInt(usedtodayString1[0])+1;
                                                        String[] bei=foodSetGet.getFoodPrice().split(" ");
                                                        int beimpya=Integer.parseInt(bei[0]);
                                                        int finalbei=beimpya*newCount_today1;
                                                        couponNumberRef.child(foodSetGet.getFoodName()).setValue(newCount_today1+" "+finalbei+" sold").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                DatabaseReference cupounused=FirebaseDatabase.getInstance().getReference().child("Coupons Used").child(dateOnly);
                                                                cupounused.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()){
                                                                            String zote=snapshot.child("Total Today").getValue(String.class);
                                                                            String[] number_zote=zote.split(" ");
                                                                            int namba_pekee=Integer.parseInt(number_zote[0])+1;
                                                                            cupounused.child("Total Today").setValue(namba_pekee+" sold").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    DashBoard.progressDialog2.dismiss();
                                                                                }
                                                                            });
                                                                        }else{
                                                                            cupounused.child("Total Today").setValue("1 sold").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    DashBoard.progressDialog2.dismiss();
                                                                                }
                                                                            });
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }else{
                                                        String[] bei=foodSetGet.getFoodPrice().split(" ");
                                                        couponNumberRef.child(foodSetGet.getFoodName()).setValue("1 "+bei[0]+ " sold").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                DatabaseReference cupounused=FirebaseDatabase.getInstance().getReference().child("Coupons Used").child(dateOnly);
                                                                cupounused.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()){
                                                                            String zote=snapshot.child("Total Today").getValue(String.class);
                                                                            String[] number_zote=zote.split(" ");
                                                                            int namba_pekee=Integer.parseInt(number_zote[0])+1;
                                                                            cupounused.child("Total Today").setValue(namba_pekee+" sold").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    DashBoard.progressDialog2.dismiss();
                                                                                }
                                                                            });
                                                                        }else{
                                                                            cupounused.child("Total Today").setValue("1 sold").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    DashBoard.progressDialog2.dismiss();
                                                                                }
                                                                            });
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                            }
                                                        });
                                                        DashBoard.progressDialog2.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        }
                                    });
                                }
                            });

                        }



                }else{

                    couponNumberRef.child("Used Today").setValue("1 sold");
                    couponNumberRef.child("Used Total").setValue("1 sold");
                    couponNumber="1";
                    DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference()
                            .child("Coupons")
                            .child(FirebaseAuth.getInstance().getUid())
                            .push(); // Generate a unique key for the coupon

                    couponRef.child("Menu Name").setValue(foodSetGet.getFoodName());
                    couponRef.child("Menu Time").setValue(currentdate+"Hrs");
                    couponRef.child("Menu Price").setValue(foodSetGet.getFoodPrice());
                    couponRef.child("Status").setValue("pending");
                    couponRef.child("Reference Number").setValue(uniqueID);
                    couponRef.child("Served Time").setValue("Not served");
                    couponRef.child("Coupon Number").setValue(couponNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            DatabaseReference soldNumberRef = FirebaseDatabase.getInstance().getReference().child("Coupons")
                                    .child("Coupons Used")
                                    .child(dateOnly).child(foodSetGet.getFoodName());
                            soldNumberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String usedtoday1=snapshot.getValue(String.class);
                                        String[] usedtodayString1=usedtoday1.split(" ");
                                        int newCount_today1=Integer.parseInt(usedtodayString1[0])+1;
                                        couponNumberRef.child(foodSetGet.getFoodName()).setValue(newCount_today1+" sold").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                DatabaseReference cupounused=FirebaseDatabase.getInstance().getReference().child("Coupons Used").child(dateOnly);
                                                cupounused.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()){
                                                            String zote=snapshot.child("Total Today").getValue(String.class);
                                                            String[] number_zote=zote.split(" ");
                                                            int namba_pekee=Integer.parseInt(number_zote[0])+1;
                                                            cupounused.child("Total Today").setValue(namba_pekee+" sold").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    DashBoard.progressDialog2.dismiss();
                                                                }
                                                            });
                                                        }else{
                                                            cupounused.child("Total Today").setValue("1 sold").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    DashBoard.progressDialog2.dismiss();
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });

                                    }else{
                                        String[] bei=foodSetGet.getFoodPrice().split(" ");
                                        couponNumberRef.child(foodSetGet.getFoodName()).setValue("1 "+bei[0]+ " sold").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                DatabaseReference cupounused=FirebaseDatabase.getInstance().getReference().child("Coupons Used").child(dateOnly);
                                                cupounused.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()){
                                                            String zote=snapshot.child("Total Today").getValue(String.class);
                                                            String[] number_zote=zote.split(" ");
                                                            int namba_pekee=Integer.parseInt(number_zote[0])+1;
                                                            cupounused.child("Total Today").setValue(namba_pekee+" sold").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    DashBoard.progressDialog2.dismiss();
                                                                }
                                                            });
                                                        }else{
                                                            cupounused.child("Total Today").setValue("1 sold").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    DashBoard.progressDialog2.dismiss();
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
    public static Bitmap generateQRCodeBitmap(HistorySetGet historySetGet) {
        // Construct data string for QR code
        String data =", Reference Number: " + historySetGet.getCoupon_reference_Number()+
                ", UID: "+FirebaseAuth.getInstance().getUid();

        // Generate QR code bitmap
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 512, 512, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
