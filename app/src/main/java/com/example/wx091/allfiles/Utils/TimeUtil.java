package com.example.wx091.allfiles.Utils;

/**
 * Created by wx091 on 2015/12/8.
 */
public class TimeUtil {

    public static String intToTime(long i){
        String date="";
        i=i/1000;
        long m=i/60;
        long s=i%60;
        if(m<10){
            date=date+"0";
        }
        date=date+m+":";
        if(s<10){
            date=date+"0";
        }
        date=date+s;
        return date;
    }

    public static String intToTime(String string){
        String date="";
        int i=0;
        try{
            i=Integer.parseInt(string);
        }catch (Exception e){
            return "";
        }
        i=i/1000;
        long m=i/60;
        long s=i%60;
        if(m<10){
            date=date+"0";
        }
        date=date+m+":";
        if(s<10){
            date=date+"0";
        }
        date=date+s;
        return date;
    }
}
