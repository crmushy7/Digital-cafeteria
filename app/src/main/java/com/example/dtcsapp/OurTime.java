package com.example.dtcsapp;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class OurTime {
    private static String timeStatus="";
    public static void init(Context context)
    {
        Calendar calendar = Calendar.getInstance();
        String currentdate = DateFormat.getInstance().format(calendar.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        String formattedTime = simpleDateFormat.format(new Date());


        int currentHour=calendar.get(Calendar.HOUR_OF_DAY);
        if(currentHour>=6 && currentHour<12)
        {
            timeStatus="BreakFast";
        }else if(currentHour>=12 && currentHour<16)
        {
            timeStatus="Lunch";
        } else if (currentHour>=16 && currentHour<22) {
            timeStatus="Dinner";
        }else{
            timeStatus="Ngano";}


    }
    public static String getTimeStatus(){
        return timeStatus;
    }
}
