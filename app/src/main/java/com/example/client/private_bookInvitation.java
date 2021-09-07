package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Queue;

public class private_bookInvitation extends MyAppCompatActivity implements View.OnClickListener {
    // Book invitation activity

    private COM socketThread;        // Communication class (Sending & receiving)
    private Button btnAccept;        // Accept book invitation button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_activity_book_invitation);
        MainActivity.currentContext = this;

        socketThread = new COM(sendQueue, this);

        ((TextView)findViewById(R.id.tvAreaName)).setText(private_areasList.areaName);
        ((TextView)findViewById(R.id.tvCapsuleName)).setText(private_capsulesList.capsuleName);
        ((TextView)findViewById(R.id.tvInvitationDate)).setText(private_invitationsList.invitationDate);
        ((TextView)findViewById(R.id.tvInvitationTime)).setText(private_invitationsList.invitationTime);
        ((TextView)findViewById(R.id.tvAreaPhone)).setText(private_areasList.areaPhoneNum);
        btnAccept = findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(this);
    }

    public void onClick(View v)
    //in: View (after there was a click)
    //out: returns nothing. checks which button pressed etc..
    {
        if (v == btnAccept) {
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            btnAccept.setClickable(false);
            sendQueue.add("008" + private_areasList.areaPhoneNum + "#" + private_capsulesList.capsuleName + "#" + private_invitationsList.invitationDate
                    + "#" + private_invitationsList.invitationTime);
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
                case 108:
                    // Invitation booked successfully
                    active = false;
                    Intent i = new Intent(context, private_profile.class);
                    startActivity(i);
                    break;
            }
        }
        @Override
        public void error400() {
            // When an Error 400 (Bad request) have occurred
            super.error400();
            btnAccept.setClickable(true);
            setLoadingIcon(View.INVISIBLE);
        }
    }
}

