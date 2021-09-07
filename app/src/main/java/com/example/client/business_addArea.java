package com.example.client;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Queue;

public class business_addArea extends MyAppCompatActivity implements View.OnClickListener, business_DialogAddValidator.DialogAddValidatorListener {

    private COM socketThread;       // Communication class (Sending & receiving)
    private String[] usersName={""}, usersPhone={"No Validators"};  // Details about the Validators
    private Button btnAccept;       // Button to accept New area
    private EditText etAreaName, etAreaPhone;   // EditTexts of the area name and phone.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_activity_add_area);
        MainActivity.currentContext = this;

        socketThread = new COM(sendQueue, this);
        etAreaName = findViewById(R.id.etAreaName);
        etAreaPhone = findViewById(R.id.etAreaPhone);
        btnAccept = findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(this);

        etAreaName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return business_addArea.this.onEditorAction(v, actionId, event, etAreaName);
            }
        });
        showValidators();
    }

    @Override
    public void onClick(View v) {
        //in: View pressed
        //out: handles view press
        if(v == btnAccept) {
            //Accept add area
            String areaPhoneNum = etAreaPhone.getText().toString(), areaName = etAreaName.getText().toString();
            if (areaPhoneNum.length() == 10 && functions.isInteger(areaPhoneNum))
                areaPhoneNum = areaPhoneNum.substring(0, 3) + "-" + areaPhoneNum.substring(3);

            if(functions.isPhoneNum((areaPhoneNum)) ) { // areaName.length() > 0) {
                if(functions.isValidString(areaName)) {
                    btnAccept.setClickable(false);
                    if (usersPhone.length > 0 && !"No Validators".equals(usersPhone[0]))
                        socketThread.sendQueue.add("322" + areaName + "#" + areaPhoneNum + "#" + functions.arrToStr(usersPhone) + "#" + functions.arrToStr(usersName));
                    else
                        socketThread.sendQueue.add("322" + areaName + "#" + areaPhoneNum + "#-#-");
                    setLoadingIcon(View.VISIBLE);
                }
                else
                    showDialog("Invalid area Name", null, this);
            }
            else
                showDialog("Invalid area Phone number", null, this);
               //Toast("Wrong Area phone / Name");
        }
        else if (v == findViewById(R.id.btnAddValidator))
            //Add validator
            openDialog();
        else
            closeKeyboard();
    }
    public void openDialog() {
        // Open Get-validators phone+name Dialog
        business_DialogAddValidator exampleDialog = new business_DialogAddValidator();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }
    @Override
    public void applyTexts(String userphone, String username) {
        //in: user phone, user name
        //out: adds validator
        String[] tmpName = usersName, tmpPhone = usersPhone;
        if (userphone.length() == 10 && functions.isInteger(userphone))
            userphone = userphone.substring(0, 3) + "-" + userphone.substring(3);
        if(functions.isPhoneNum(userphone) && username.length() < 10 && (functions.isValidString(username) || username.length() == 0)) {
            if(usersPhone.length > 0 && usersPhone[0].equals("No Validators")) {
                usersName = new String[]{username};
                usersPhone = new String[]{userphone};
            } else {
                usersName = new String[tmpName.length + 1];
                usersPhone = new String[tmpName.length + 1];
                for (int i = 0; i < tmpName.length; i++) {
                    usersName[i] = tmpName[i];
                    usersPhone[i] = tmpPhone[i];
                }
                usersName[tmpName.length] = username;
                usersPhone[tmpName.length] = userphone;
            }
        }
        else {
            showDialog("Invalid Phone Number or Name", null, this);
            return;
        }
        etAreaName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return business_addArea.this.onEditorAction(v, actionId, event, etAreaName);
            }
        });
        etAreaPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return business_addArea.this.onEditorAction(v, actionId, event, etAreaPhone);
            }
        });
        showValidators();
    }
    public void showValidators()
            // displays the added validators
    {
        RecyclerView recyclerView = findViewById(R.id.recyclerView1);
        if(usersName.length > 0) {
            RecyclerView.Adapter myAdapter;
            if(usersPhone[0].equals("No Validators"))
                myAdapter = new MyAdapter3(this, usersPhone, sendQueue);
            else
                myAdapter = new MyAdapter1(this, usersName, usersPhone, sendQueue);
            recyclerView.setAdapter(myAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                case 422:
                    // Add area success
                    setLoadingIcon(View.INVISIBLE);
                    socketThread.setActive(false);
                    business_myAreas.areaPhoneNum = etAreaPhone.getText().toString();
                    Intent i = new Intent(MainActivity.currentContext, business_area_profile.class); //business_myAreas.class);
                    startActivity(i);
                    break;
                case 423:
                    setLoadingIcon(View.INVISIBLE);
                    showDialog("Area's phone already in use", null, MainActivity.currentContext);
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