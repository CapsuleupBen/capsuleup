package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Queue;

public class private_createAccount extends MyAppCompatActivity implements View.OnClickListener {
    // private user - Create account activity

    private EditText btnRegister;       // Register as a Business User
    private EditText etName;            // Get input from user (his phone number)
    private String name;                // client full name

    private COM socketThread;           // Communication class (Sending & receiving)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        MainActivity.currentContext = this;
        etName = findViewById(R.id.etName);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        socketThread = new COM(sendQueue, this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketThread.closeConnection();
    }
    @Override
    public void onClick(View v)
            //in: View (after there was a click)
            //out: returns nothing. checks which button pressed etc..
    {
        if (v == btnRegister) {
            name = etName.getText().toString();
            if(functions.isValidString(name))
                //if its a correct Name
            {
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                btnRegister.setClickable(false);
                sendQueue.add("014"+name);
            }
            else
                showDialog("Invalid name", null,this);
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
            switch (command)
            {
                case 114: {
                    // Client created
                    setLoadingIcon(View.INVISIBLE);
                    active = false;
                    Intent i = new Intent(context, private_profile.class);
                    MainActivity.userName = name;
                    startActivity(i);
                    break;
                }
            }
        }
        @Override
        public void error400() {
            // When an Error 400 (Bad request) have occurred
            super.error400();
            btnRegister.setClickable(true);
            setLoadingIcon(View.INVISIBLE);
        }
    }
}