package com.example.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Queue;

public class private_area_profile extends MyAppCompatActivity implements View.OnClickListener{
    // Displays the area's profile activity

    private COM socketThread;                            // Communication class (Sending & receiving)
    private String areaName, areaDescription, areaPhone; // Basic details about the area
    private String[] areaImagesStr=new String[5];        // Base64 of the area images
    private ImageView[] imvImages;                       // List of the image views

    private Button btnSubAreas;                          // Button will be pressed to go into sub areas list
    private Activity activity;                           // Current activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_activity_area_profile);
        MainActivity.currentContext = this;
        activity = this;

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

        btnSubAreas = findViewById(R.id.btnSubAreas);
        btnSubAreas.setOnClickListener(this);

        socketThread = new COM(sendQueue, this);
        sendQueue.add("344"+ private_areasList.areaPhoneNum);
    }

    @Override
    public void onClick(View v) {
        //in: View (after there was a click)
        //out: returns nothing. checks which button pressed etc..
        if(v==btnSubAreas) {
            socketThread.setActive(false);
            Intent i = new Intent(this, private_capsulesList.class);
            startActivity(i);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.currentContext = this;
    }

    public void showArea() {
        // Displays the area Details
        final TextView tvAreaName = findViewById(R.id.tvAreaName),
                tvAreaPhone=findViewById(R.id.tvAreaPhone),
                tvAreaDescription=findViewById(R.id.tvAreaDescription);

        runOnUiThread(new Runnable() {
            public void run() {
                tvAreaName.setText(areaName);
                tvAreaPhone.setText(areaPhone);
                if(!areaDescription.equals("-"))
                    tvAreaDescription.setText(areaDescription);

                for(int i=0;i<5;i++) {
                    if (areaImagesStr[i].equals("-"))
                        imvImages[i].setImageResource(R.drawable.no_image);
                    else {
                            byte[] decodedBytes = android.util.Base64.decode(areaImagesStr[i], android.util.Base64.DEFAULT);//Base64.getDecoder().decode(areaImagesStr[i]);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            imvImages[i].setImageBitmap(bitmap);
                    }
                }
                findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
                findViewById(R.id.layoutAreaDetails).setVisibility(View.VISIBLE);
            }
        });
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
                    // Received area details
                    if(args.length==8 && functions.isPhoneNum(args[1])) {
                        areaName = args[0];
                        areaPhone = args[1];
                        areaDescription = args[2];
                        for(int i=0; i<5; i++)
                            areaImagesStr[i] = args[i+3];
                        showArea();
                    }
                    else {
                        sendQueue.add("344"+ private_areasList.areaPhoneNum);
                        showDialog("Error", null, context);
                    }
                    break;
            }
        }
    }
}