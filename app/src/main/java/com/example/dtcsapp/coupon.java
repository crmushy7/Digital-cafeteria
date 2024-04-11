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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.HashMap;
import java.util.Map;

public class coupon {
    public static String uniqueID="";

    public static void generateCoupon(Context context, FoodSetGet foodSetGet) {
        uniqueID=UniqueIDGenerator.generateUniqueID();
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
    public static Bitmap generateQRCodeBitmap(HistorySetGet historySetGet) {
        // Construct data string for QR code
        String data = "Menu Name: " + historySetGet.getFood_name() +
                ", Menu Time: " + historySetGet.getCoupon_date() +
                ", Menu Price: " + historySetGet.getFood_price() +
                ", Status: pending" +
                ", Reference Number: " + historySetGet.getCoupon_reference_Number();

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
