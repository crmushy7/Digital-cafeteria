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
        // Generate QR code bitmap with the data to be stored
        Bitmap qrCodeBitmap = generateQRCodeBitmap(foodSetGet);

        // Upload QR code image to Firebase Storage
        if (qrCodeBitmap != null) {
            uploadQRCodeImageToFirebaseStorage(context, qrCodeBitmap, foodSetGet);
        }
    }

    private static Bitmap generateQRCodeBitmap(FoodSetGet foodSetGet) {
        // Construct data string for QR code
        String data = "Menu Name: " + foodSetGet.getFoodName() +
                ", Menu Time: " + OurTime.getOrderTime() +
                ", Menu Price: " + foodSetGet.getFoodPrice() +
                ", Status: pending" +
                ", Reference Number: " + uniqueID;

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

    private static void uploadQRCodeImageToFirebaseStorage(final Context context, Bitmap bitmap, final FoodSetGet foodSetGet) {
        // Create a reference to the QR code image
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("qr_code_images").child(FirebaseAuth.getInstance().getUid()).child(uniqueID);

        // Convert bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload the image
        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image uploaded successfully
                // Get the download URL for the uploaded image
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Store the download URL along with the data in Firebase Realtime Database
                        String downloadUrl = uri.toString();
                        storeDataInFirebaseDatabase(foodSetGet, downloadUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle errors during image upload
                // Show error message
                Toast.makeText(context, "Failed to upload QR code image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void storeDataInFirebaseDatabase(FoodSetGet foodSetGet, String qrCodeUrl) {
        // Store the data along with the QR code URL in Firebase Realtime Database
        DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference()
                .child("Coupons")
                .child(FirebaseAuth.getInstance().getUid())
                .push(); // Generate a unique key for the coupon

        couponRef.child("Menu Name").setValue(foodSetGet.getFoodName());
        couponRef.child("Menu Time").setValue(OurTime.getOrderTime());
        couponRef.child("Menu Price").setValue(foodSetGet.getFoodPrice());
        couponRef.child("Status").setValue("pending");
        couponRef.child("Reference Number").setValue(uniqueID);
        couponRef.child("QR Code URL").setValue(qrCodeUrl);
    }
}
