package com.example.dtcsapp;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDetails {
    private static final String SHARED_PREF_NAME = "User_data";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DOB = "dob";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_PROFILEPIC = "profilePic";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FCM_TOKEN = "FCM_Token";
    private static final String KEY_Amount = "Amount";
    private static String fullName;
    private static String password;
    private static String phoneNumber;
    private static String email;
    private static String dob;
    private static String gender;
    private static String profilePic;
    private static String FCM_Token;
    private static String amount;
    public static void init(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        fullName = sharedPreferences.getString(KEY_FULL_NAME, null);
        phoneNumber = sharedPreferences.getString(KEY_PHONE_NUMBER, null);
        email = sharedPreferences.getString(KEY_EMAIL, null);
        password = sharedPreferences.getString(KEY_PASSWORD, null);
        dob = sharedPreferences.getString(KEY_DOB, null);
        gender = sharedPreferences.getString(KEY_GENDER, null);
        profilePic = sharedPreferences.getString(KEY_PROFILEPIC, null);
        FCM_Token = sharedPreferences.getString(KEY_FCM_TOKEN, null);
        amount = sharedPreferences.getString(KEY_Amount, null);
    }
    public static String getFullName() {
        return fullName;
    }
    public static String getAmount() {
        return amount;
    }
    public static String getDob() {
        return dob;
    }
    public static String getProfilePic() {
        return profilePic;
    }
    public static String getPassword() {
        return password;
    }
    public static String getGender() {
        return gender;
    }
    public static String getPhoneNumber() {
        return phoneNumber;
    }
    public static String getEmail() {
        return email;
    }
    public static String getFCM_Token() {
        return FCM_Token;
    }
}
