package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Queue;

public class business_addInvitation extends MyAppCompatActivity implements View.OnClickListener {

    private COM socketThread;       // Communication class (Sending & receiving)

    private Button btnAccept;       // Accept add invitation button
    private EditText etInvitationDate, etInvitationTime, etInvitationsAmount; // edit texts to fill to submit time & date
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_activity_add_invitation);
        MainActivity.currentContext = this;

        socketThread = new COM(sendQueue, this);

        btnAccept = findViewById(R.id.btnSubmitInvitation);
        btnAccept.setOnClickListener(this);
        etInvitationsAmount = findViewById(R.id.etInvitationsAmount);
        etInvitationDate=findViewById(R.id.etInvitationDate);
        etInvitationTime=findViewById(R.id.etInvitationTime);
        etInvitationTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return business_addInvitation.this.onEditorAction(v, actionId, event, etInvitationTime);
            }
        });
    }

    @Override
    public void onClick(View v) {
        //in: View pressed
        //out: handles view press
        if(v == btnAccept) {
            String date = etInvitationDate.getText().toString(), time = etInvitationTime.getText().toString(), amount = etInvitationsAmount.getText().toString();
            if(functions.isDate(date) ) {
                if (functions.isTime(time)) {
                    // Date and time are valid
                    if(functions.isInteger(amount) && Integer.parseInt(amount) > 0) {
                        btnAccept.setClickable(false);
                        setLoadingIcon(View.VISIBLE);
                        sendQueue.add("326" + business_myAreas.areaPhoneNum + "#" + business_myCapsules.capsuleName + "#" + date + "#" + time + "#" + amount);
                    }
                    else
                        showDialog("Invalid Amount", null, this);
                }
                else
                    showDialog("Invalid Time", null, this);
            }
            else
                showDialog("Invalid Date", null, this);
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
                case 426:
                    // Add Invitation success
                    setLoadingIcon(View.INVISIBLE);
                    socketThread.setActive(false);
                    Intent i = new Intent(MainActivity.currentContext, business_myInvitations.class);
                    startActivity(i);
                    break;
            }
        }
        @Override
        public void error400() {
            // When an Error 400 (Bad request) have occurred
            super.error400();
            setLoadingIcon(View.INVISIBLE);
            btnAccept.setClickable(true);
        }
    }
}