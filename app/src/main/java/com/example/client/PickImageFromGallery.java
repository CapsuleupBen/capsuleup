package com.example.client;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PickImageFromGallery extends MyAppCompatActivity
    // Class used to pick image from gallery
{
    public static final int IMAGE_PICK_CODE = 1000;   // Image pick code
    private static final int PERMISSION_CODE = 1001;  // Access code
    private Activity activity;                      // Current activity

    public void changeIVtoImageFromGallery(Activity act) {
        //in: activity
        //out: Handles the imageView change (request permission and pick image)
        if(act!=null) {
            this.activity = act;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    activity.requestPermissions(permissions, PERMISSION_CODE);
                } else
                    pickImageFromGallery();
            } else
                pickImageFromGallery();

        }
    }

    private void pickImageFromGallery() {
        //in: nothing
        //out: picks image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, IMAGE_PICK_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //in: Result of request permissions
        //out: Picks image from gallery if confirmed permissions
        switch(requestCode) {
            case PERMISSION_CODE: {
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    // Permission confirmed
                    pickImageFromGallery();
                else
                    // Permission denied
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }
}

