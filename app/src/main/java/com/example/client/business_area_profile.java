package com.example.client;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Base64;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Queue;

public class business_area_profile extends MyAppCompatActivity implements View.OnClickListener, business_DialogGetDescription.ExampleDialogListener{

    private COM socketThread;       // Communication class (Sending & receiving)
    private String areaName, areaPhone, areaDescription; // Basic details about the area
    private String[] areaImagesStr=new String[5];        // Base64 of the area images
    private boolean[] imagesChanged = new boolean[]{false, false, false, false, false}; // Array that says which image have changed
    private boolean descriptionChanged = false;          // Determines whether the description have changed

    private ImageView[] imvImages;                       // List of the image views
    public static TextView tvAreaDescription;            // Description TextView
    private Button btnSubAreas;                          // Button will be pressed to go into sub areas list
    private boolean editingArea = false;                 // Is the area being edited Right now?
    private Activity activity;                           // Current activity
    private int curImageSelected=0;                      // Index of image that is being picked from gallery Right now
    private FloatingActionButton btnEditArea, applyAreaEdit;    // Floating buttons TO edit/apply area business_profile
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_activity_area_profile);
        MainActivity.currentContext = this;
        activity = this;
        tvAreaDescription = findViewById(R.id.tvAreaDescription);

        imvImages = new ImageView[]{findViewById(R.id.imvAreaPic1), findViewById(R.id.imvAreaPic2), findViewById(R.id.imvAreaPic3), findViewById(R.id.imvAreaPic4), findViewById(R.id.imvAreaPic5)};
        for(int i=1;i< imvImages.length;i++) {
            final int j=i;
            // Apply image-Switch listener
            imvImages[j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Drawable tmp = imvImages[j].getDrawable();
                    imvImages[j].setImageDrawable(imvImages[0].getDrawable());
                    imvImages[0].setImageDrawable(tmp);
                }
            });
        }
        btnEditArea = findViewById(R.id.btnEditArea);
        applyAreaEdit = findViewById(R.id.btnApplyAreaEdit);

        btnSubAreas = findViewById(R.id.btnSubAreas);
        btnSubAreas.setOnClickListener(this);

        socketThread = new COM(sendQueue, this);
        sendQueue.add("344"+ business_myAreas.areaPhoneNum);
    }

    @Override
    public void onClick(View v) {
        //in: View pressed
        //out: handles view press
        if(v==btnSubAreas) {
            // Go to sub - areas screen
            socketThread.setActive(false);
            Intent i = new Intent(this, business_myCapsules.class);
            startActivity(i);
        }
        else if(v==btnEditArea) {
            // Edit area
            editingArea = true;
            // reset area Images
            showArea();
            btnSubAreas.setClickable(false);
            applyAreaEdit.setVisibility(View.VISIBLE);
            findViewById(R.id.btnCancelEditArea).setVisibility(View.VISIBLE);
            btnEditArea.setVisibility(View.INVISIBLE);

            // When image clicked - pick from gallery
            for(int i=0;i< imvImages.length;i++) {
                final int j=i;
                imvImages[j].setOnClickListener(new View.OnClickListener() {
                    PickImageFromGallery pifg = new PickImageFromGallery();
                    @Override
                    public void onClick(View v) {
                        curImageSelected = j;
                        //Choose image from gallery
                        pifg.changeIVtoImageFromGallery(activity);
                    }
                });
                imvImages[j].setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
            }
            tvAreaDescription.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        }
        else if(v==applyAreaEdit) {
            // Apply area - edit
            String str = "";
            applyAreaEdit.setClickable(false);
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutAreaDetails).setVisibility(View.INVISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for(int i=0;i<imvImages.length;i++) {
                    if(!imagesChanged[i])
                        str += "-#";
                    else {
                        Bitmap bitmap = ((BitmapDrawable) imvImages[i].getDrawable()).getBitmap();
                        str += functions.getBitmapToBase64(bitmap) + "#";
                    }
                }
                str = str.substring(0, str.length()-1) + "#";
                if(descriptionChanged)
                    str += tvAreaDescription.getText().toString();
                else
                    str += "-";
                sendQueue.add("372" + areaPhone + "#" + str);
            }
            else
                showDialog("Device SDK is too low to change Profile (<21)", null, this);
                //Toast("Device SDK is too low to change Profile (<21)");
        }
        else if(v==findViewById(R.id.tvAreaDescription)) {
            // Edit area's description
            if(editingArea)
                openDialog();
        }
        else if(v==findViewById(R.id.btnCancelEditArea)) {
            // Cancel edit area
            socketThread.setActive(false);
            Intent i = new Intent(activity, business_area_profile.class);
            startActivity(i);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //in: request code, result code, data
        //out: handles the data in case of Image picked.
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PickImageFromGallery.IMAGE_PICK_CODE) {
            imvImages[curImageSelected].setImageURI(data.getData());
            imagesChanged[curImageSelected] = true;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.currentContext = this;

    }

    public void showArea() {
        //out: shows the area selected.
        final TextView tvAreaName = findViewById(R.id.tvAreaName),
                tvAreaPhone=findViewById(R.id.tvAreaPhone);

        runOnUiThread(new Runnable() {
            public void run() {
                tvAreaName.setText(areaName);
                tvAreaPhone.setText(areaPhone);
                if(!areaDescription.equals("-"))
                    tvAreaDescription.setText(areaDescription);
                // Set images
                for(int i=0;i<5;i++) {
                    if (areaImagesStr[i].equals("-"))
                        imvImages[i].setImageResource(R.drawable.no_image);
                    else {
                            byte[] decodedBytes = Base64.decode(areaImagesStr[i], Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            imvImages[i].setImageBitmap(bitmap);
                    }
                }
                findViewById(R.id.layoutAreaDetails).setVisibility(View.VISIBLE);
                findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            }
        });
    }
    public void openDialog() {
        //Open dialog to get Description Text.
        business_DialogGetDescription exampleDialog = new business_DialogGetDescription();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }
    @Override
    public void applyTexts(String description) {
        // Apply edit Description Text
        //if(functions.isValidMultilineString(description, 8)) {
            tvAreaDescription.setText(description);
            descriptionChanged = true;
        //}
        //else
        //    showDialog("Invalid Description", null, this);
    }

    class COM extends TCP
    {
        COM(Queue<String> sendQueue, Context c)
        //in: Server ip, port of connection, client phone number
        //out: creates The TCP connection and handles it.
        {
            super(sendQueue, c);
        }
        @Override
        public void handleMsg(String msg)
        //in: message received from Server
        //out: returns nothing. handles the Message.
        {
            super.handleMsg(msg);
            int command = getCommand(msg);
            String[] args = getArgs(msg);
            switch (command) {
                case 444:
                    // Received area's details
                    if(args.length==8 && functions.isPhoneNum(args[1])) {
                        areaName = args[0];
                        areaPhone = args[1];
                        areaDescription = args[2];
                        for(int i=0; i<5; i++)
                            areaImagesStr[i] = args[i+3];
                        showArea();
                    }
                    break;
                case 472:
                    //  change area details success
                    if(args.length==0) {
                        active=false;
                        Intent i = new Intent(activity, business_area_profile.class);
                        startActivity(i);
                    }
                    break;
            }
        }
        @Override
        public void error400() {
            // When an Error 400 (Bad request) have occurred
            super.error400();
            applyAreaEdit.setClickable(true);
            setLoadingIcon(View.INVISIBLE);
            runOnUiThread(new Runnable() {
                public void run() {
                    findViewById(R.id.layoutAreaDetails).setVisibility(View.VISIBLE);
                }
            });
        }
    }
}