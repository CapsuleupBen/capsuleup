package com.example.client;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Queue;

public class business_addCapsule extends MyAppCompatActivity implements View.OnClickListener {

    private COM socketThread;       // Communication class (Sending & receiving)
    private boolean imageChanged = false;   // Determines if the Capsule image have changed
    private Button btnAccept;       // Button to cancel add capsule
    private ImageView imgCapsule;   // ImageView of the capsule image
    private Activity activity;      // This activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_activity_add_capsule);
        MainActivity.currentContext = this;

        socketThread = new COM(sendQueue, this);

        btnAccept = findViewById(R.id.btnSubmitCapsule);
        btnAccept.setOnClickListener(this);
        activity = this;

        imgCapsule = findViewById(R.id.imgCapsule);
        imgCapsule.setOnClickListener(new View.OnClickListener() {
            PickImageFromGallery pifg = new PickImageFromGallery();
            @Override
            public void onClick(View v) {
                //Choose image from gallery
                pifg.changeIVtoImageFromGallery(activity);
            }
        });
        imgCapsule.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
    }

    @Override
    public void onClick(View v) {
        //in: View pressed
        //out: handles view press
        EditText etCapsuleName=findViewById(R.id.etCapsuleName), etCapsuleDescription=findViewById(R.id.etCapsuleDescription), etCapsuleCapacity=findViewById(R.id.etInvitationsAmount);
        if(v == btnAccept) {
            closeKeyboard();
            String capsuleName=etCapsuleName.getText().toString(), capsuleDescription=etCapsuleDescription.getText().toString(), capsuleCapacity=etCapsuleCapacity.getText().toString();
            if(functions.isValidString(capsuleName)) {
                if(functions.isValidMultilineString(capsuleDescription, 2)) {
                    if(functions.isInteger(capsuleCapacity)) {
                        btnAccept.setClickable(false);
                        setLoadingIcon(View.VISIBLE);
                        String capsuleImg = "-";
                        if (imageChanged) {
                            Bitmap bitmap = ((BitmapDrawable) imgCapsule.getDrawable()).getBitmap();
                            capsuleImg = functions.getBitmapToBase64(bitmap);
                        }
                        socketThread.sendQueue.add("324" + business_myAreas.areaPhoneNum + "#" + capsuleName + "#" + capsuleCapacity + "#" + capsuleDescription + "#" + capsuleImg);
                    }
                    else
                        showDialog("Invalid Capacity", null, this);
                }
                else
                    showDialog("Invalid Description", null, this);
            }
            else
                showDialog("Invalid Name", null, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PickImageFromGallery.IMAGE_PICK_CODE) {
            // Image was picked
            imgCapsule.setImageURI(data.getData());
            imageChanged = true;
        }
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
                case 424:
                    // Add capsule success
                    setLoadingIcon(View.INVISIBLE);
                    btnAccept.setClickable(true);
                    socketThread.setActive(false);
                    Intent i = new Intent(MainActivity.currentContext, business_myCapsules.class);
                    startActivity(i);
                    break;
                case 425:
                    // Capsule already exist
                    setLoadingIcon(View.INVISIBLE);
                    btnAccept.setClickable(true);
                    showDialog("Capsule already Exist", null, activity);
                    break;
            }
        }
        @Override
        public void error400() {
            // When an Error 400 (Bad request) have occurred
            super.error400();
            btnAccept.setClickable(true);
            setLoadingIcon(View.INVISIBLE);
            btnAccept.setClickable(true);
        }
    }
}