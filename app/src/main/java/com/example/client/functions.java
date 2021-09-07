package com.example.client;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class functions {
    // Class includes Basic static functions

    public static boolean isPhoneNum(String str)
    //in: String
    //out: returns True if its a valid phone number (000-0000000 syntax)
    {
        return str.length()==11 && isInteger(str.substring(0,3)) && isInteger(str.substring(4)) && str.charAt(3) == '-';
    }
    public static boolean isValidString(String str) {
    //in: String
    //out: returns True if its valid string (without special characters)
        return str.matches("^[ A-Za-z0-9]+$") && !(str.split(" ").length == 0);
    }
    public static boolean isValidMultilineString(String str, int maxLines) {
    //in: String
    //out: returns True if its Valid String with valid amount of lines
        boolean flag = true;
        String[] tmp = str.split("\n");
        if(tmp.length > maxLines)
            flag = false;
        for(int i=0;i<tmp.length && flag;i++)
            if(!isValidString(tmp[i]))
                flag = false;
        return flag;
    }
    public static boolean isDate(String str)
    //in: String
    //out: True if its a valid date
    {
        boolean flag = false;
        String [] tmp = str.split("\\.");
        if(tmp.length == 3 && isInteger(tmp[0]) && isInteger(tmp[1]) && isInteger(tmp[2]))
        {
            int day=Integer.parseInt(tmp[0]), month = Integer.parseInt(tmp[1]), year = Integer.parseInt(tmp[2]);
            if(0 < day && day < 32 && 0 < month && month < 13 && 2020 < year && year < 2100)
                flag = true;
        }
        return flag;
    }
    public static boolean isTime(String str)
    //in: String
    //out: True if its valid time
    {
        boolean flag=false;
        String [] tmp = str.split(":");
        if(tmp.length == 2 && isInteger(tmp[0]) && isInteger(tmp[1])) {
            int hour = Integer.parseInt(tmp[0]), minute = Integer.parseInt(tmp[1]);
            if(0 < hour && hour < 24 && 0 <= minute && minute < 60)
                flag = true;
        }
        return flag;
    }
    public static boolean isIp(String str)
    //in: String
    //out: returns True if its Ip
    {
        boolean ret = true;
        String[] tmp = str.split("\\.");
        if(tmp.length == 4) {
            for(int i=0;i<4 && ret;i++)
                if(!isInteger(tmp[i]) || Integer.parseInt(tmp[i]) > 255 || Integer.parseInt(tmp[i]) < 0)
                    ret = false;
        }
        else
            ret = false;
        return ret;
    }
    public static boolean isInteger(String str)
    //in: String
    //out: returns True if its Integer
    {
        return str.matches("-?\\d+(\\d+)?");
    }
    public static byte[] appendByteArrays(byte[] arr1, byte[] arr2) {
        //in: two byte arrays
        //out: returns arr1 + arr2 (new byte array)
        byte[] ret = new byte[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, ret, 0, arr1.length);
        System.arraycopy(arr2, 0, ret, arr1.length, arr2.length);
        return ret;
    }
    public static boolean isNumber(byte[]arr) {
        //in: byte array
        //out: returns True if its number under 2,147,483,647

        boolean ret = ((int)arr[0]-48) == 0;
        for(int i=0;i<arr.length && ret;i++)
            if((int)arr[i] < 48 || (int)arr[i] > 57)
                ret = false;
        return ret;
    }
    public static int byteArrToInt(byte[]arr) {
        //in: byte array
        //out: returns the number it represents
        int num = 0;
        for(int i=1;i<arr.length;i++)
            num += (int)Math.pow(10, arr.length-i-1)*(arr[i]-48);
        return num;
    }
    public static String[][] strToArr(String str)
    //in: String
    //out: returns the Parameters behind it.
    {
        String[]arr = str.split("@");
        String[][] areas_arr = {};
        for(int i=0;i<arr.length;i++) {
            String[][]tmp = new String[areas_arr.length+1][];
            for(int j=0;j<areas_arr.length;j++)
                tmp[j]= areas_arr[j];
            tmp[areas_arr.length] = arr[i].split("&");
            areas_arr = tmp;
        }
        return areas_arr;
    }

    public static String[] getIndexInArray(String[][] arr, int j)
    //in: String [][] array, index
    //out: returns new array that includes all of the Index received
    {
        String[] brr = new String[arr.length];
        for(int i=0; i<brr.length; i++)
            brr[i] = arr[i][j];
        return brr;
    }
    public static String getBitmapToBase64(Bitmap bmp)
    //in: bitmap
    //out: returns the image inside the bitmap (base64)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return android.util.Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
    public static String arrToStr(String[] arr)
    //in: String array
    //out: Returns the String array as a String to send via sockets.
    {
        String newstr = "";
        for(int i=0;i<arr.length;i++) {
            newstr+=arr[i] + "%";
        }
        return newstr.substring(0, newstr.length()-1);
    }
}
