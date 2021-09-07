package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Queue;

public class MainActivity extends MyAppCompatActivity implements View.OnClickListener{
    FloatingActionButton btnBusiness, btnScanner, btnPrivate;
    // Beginning Log-in activity

    public static String userName, userPhone; // Name and phone of user
    public static Context currentContext;     // Current Context
    public static int userType=0;       // Private=0/Business=1/Scanner=2

    private String login_command="012"; // Default Log-in command (private user)
    private String phoneNum;            // phone number of the user
    private String ip = "3.21.18.8";    //"192.168.1.30"; //  // Default server IP
    private int port = 1448;            // Private client port
    private COM socketThread;           // Communication class (Sending & receiving)

    private TextView btnSignIn;         // continue to 'getName' / 'searchArea' screen
    private EditText etPhoneNum, etIp;  // Get input from user (his phone number)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_login);
        MainActivity.currentContext = this;
        btnSignIn = findViewById(R.id.btnSignIn);
        etPhoneNum = findViewById(R.id.etPhone);
        etIp = findViewById(R.id.etIp);
        btnSignIn.setOnClickListener(this);
        etPhoneNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return MainActivity.this.onEditorAction(v, actionId, event, etPhoneNum);
            }
        });
        btnBusiness = findViewById(R.id.btnBusiness);
        btnPrivate = findViewById(R.id.btnPrivate);
        btnScanner = findViewById(R.id.btnScanner);
    }


    @Override
    public void onClick(View v) {
        if (v == btnSignIn && btnSignIn.isClickable()) {
            // Sign in
            phoneNum = etPhoneNum.getText().toString();
            if (phoneNum.length() == 10 && functions.isInteger(phoneNum))
                phoneNum = phoneNum.substring(0, 3) + "-" + phoneNum.substring(3);
            String ipTmp = etIp.getText().toString();
            if(functions.isIp(ipTmp))
                ip = ipTmp;
            port = 1448 + userType;
            if(functions.isPhoneNum(phoneNum)) {
                socketThread = new COM(ip, port, phoneNum, sendQueue, this, login_command);
                btnSignIn.setClickable(false);
                setLoadingIcon(View.VISIBLE);
            }
            else
                // Input error
                showDialog("Invalid Phone Number", null, this);
        }
        else if(v==btnBusiness) {
            userType = 1;
            login_command = "312";
            btnBusiness.setCustomSize(300);
            btnScanner.setCustomSize(240);
            btnPrivate.setCustomSize(240);
            btnBusiness.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnScanner.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fabColor)));
            btnPrivate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fabColor)));
        }
        else if(v==btnScanner) {
            login_command = "512";
            userType = 2;
            btnScanner.setCustomSize(300);
            btnBusiness.setCustomSize(240);
            btnPrivate.setCustomSize(240);
            btnScanner.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnBusiness.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fabColor)));
            btnPrivate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fabColor)));
        }
        else if(v==btnPrivate) {
            login_command = "012";
            userType = 0;
            btnPrivate.setCustomSize(300);
            btnScanner.setCustomSize(240);
            btnBusiness.setCustomSize(240);
            btnPrivate.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnScanner.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fabColor)));
            btnBusiness.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.fabColor)));
        }
    }

    class COM extends TCP
    {
        COM(String server_ip, int port, String phoneN, Queue<String> sendQueue, Context c, String login_command)
        //in: Server ip, port of connection, client phone number
        //out: creates The TCP connection and handles it.
        {
            super(server_ip, port, phoneN, sendQueue, c, login_command);
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
                case 112:
                    //Private: Log in success
                    setLoadingIcon(View.INVISIBLE);
                    btnSignIn.setClickable(true);
                    if (args.length == 2) {
                        if (args[0].equals("0"))
                        // User exists
                        {
                            active = false;
                            Intent i = new Intent(context, private_profile.class);
                            userName = args[1];
                            userPhone = phoneNum;
                            startActivity(i);
                        }
                        else if (args[0].equals("1")) {
                            // New user
                            active = false;
                            Intent i = new Intent(context, private_createAccount.class);
                            userPhone = phoneNum;
                            startActivity(i);
                        } else {
                            // Invalid Phone
                            showDialog("You cannot use Business phone number for Private client", null,  MainActivity.currentContext);
                            socketThread.setActive(false);
                        }
                    }
                    break;
                case 412:
                    // Business: Log in success
                    if (args.length == 2) {
                        setLoadingIcon(View.INVISIBLE);
                        btnSignIn.setClickable(true);
                        if (args[0].equals("0"))
                        // User exists
                        {
                            active = false;
                            Intent i = new Intent(context, business_profile.class);
                            userName = args[1];
                            userPhone = phoneNum;
                            startActivity(i);
                        }
                        else if (args[0].equals("1"))
                        // New User
                        {
                            active = false;
                            Intent i = new Intent(context, business_createAccount.class);
                            userPhone = phoneNum;
                            startActivity(i);
                        }
                        else {
                            // Invalid phone number
                            showDialog("You cannot use Private phone number for Business client", null,  MainActivity.currentContext);
                            socketThread.setActive(false);
                        }
                    }
                    break;
                case 612:
                    //Scanner: Log in success
                    setLoadingIcon(View.INVISIBLE);
                    btnSignIn.setClickable(true);
                    if (args.length == 0) {
                        active = false;
                        Intent i = new Intent(context, scanner_profile.class);
                        userPhone = phoneNum;
                        startActivity(i);
                    }
                    break;
            }
        }
        public void error777() {
            // When an Error 777 (Too much Requests) have occurred
            showDialog("Please Try again Later", null, context);
            setLoadingIcon(View.INVISIBLE);
            btnSignIn.setClickable(true);
        }
        @Override
        public void error400() {
            // When an Error 400 (Bad request) have occurred
            super.error400();
            setLoadingIcon(View.INVISIBLE);
            btnSignIn.setClickable(true);
            active = false;
        }
    }
}