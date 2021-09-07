package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Queue;

public class scanner_profile extends MyAppCompatActivity implements View.OnClickListener {
    // Scanner user - profile activity
    private COM socketThread;             // Communication class (Sending & receiving)
    private EditText btnMyAreas, btnLogOut;   // Press to search areas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.scanner_activity_profile);
        MainActivity.currentContext = this;
        socketThread = new COM(sendQueue, this);

        ((TextView)findViewById(R.id.tvClientPhone)).setText(MainActivity.userPhone);
        btnMyAreas = findViewById(R.id.btnMyAreas);
        btnMyAreas.setOnClickListener(this);
        btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        //in: View (after there was a click)
        //out: checks which button pressed etc..
        if(v == btnMyAreas)
        {
            btnMyAreas.setClickable(false);
            socketThread.setActive(false);
            Intent i = new Intent(this, scanner_myAreas.class);
            startActivity(i);
        }
        else if(v == btnLogOut) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            socketThread.closeConnection();
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
            }
        }
    }
}