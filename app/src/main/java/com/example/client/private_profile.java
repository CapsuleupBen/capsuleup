package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Queue;

public class private_profile extends MyAppCompatActivity implements View.OnClickListener {
    // Private user's profile
    private COM socketThread;      // Communication class (Sending & receiving)
    private EditText btnSearchAreas, btnMyInvitations, btnLogOut;   // Press to search areas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.private_activity_profile);
        MainActivity.currentContext = this;

        socketThread = new COM(sendQueue, this);

        ((TextView)findViewById(R.id.tvClientPhone)).setText(MainActivity.userPhone);
        ((TextView)findViewById(R.id.tvClientName)).setText(MainActivity.userName);
        btnSearchAreas = findViewById(R.id.btnSearchAreas);
        btnSearchAreas.setOnClickListener(this);
        btnMyInvitations = findViewById(R.id.btnMyInvitations);
        btnMyInvitations.setOnClickListener(this);
        btnLogOut = findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(v == btnSearchAreas)
        // Go to search Area screen
        {
            btnSearchAreas.setClickable(false);
            socketThread.setActive(false);
            Intent i = new Intent(this, private_searchArea.class);
            startActivity(i);
        }
        else if(v == btnMyInvitations)
        // go to My invitations screen
        {
            btnMyInvitations.setClickable(false);
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            sendQueue.add("005");
        }
        else if(v == btnLogOut) {
            // Log out
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
                case 105: {
                    // Received my invitations
                    if(args.length==1) {
                        String[][] invitations = functions.strToArr(args[0]);
                        if(invitations != null)
                        {
                            active = false;
                            setLoadingIcon(View.INVISIBLE);
                            Intent i = new Intent(context, private_myInvitations.class);
                            i.putExtra("invitationsAreaPhone", functions.getIndexInArray(invitations, 0));
                            i.putExtra("invitationsAreaName", functions.getIndexInArray(invitations, 1));
                            i.putExtra("invitationsCapsuleName", functions.getIndexInArray(invitations, 2));
                            i.putExtra("invitationsDate", functions.getIndexInArray(invitations, 3));
                            i.putExtra("invitationsTime", functions.getIndexInArray(invitations, 4));
                            startActivity(i);
                        }
                        else {
                            setLoadingIcon(View.INVISIBLE);
                            btnMyInvitations.setClickable(true);
                            showDialog("Error", null, MainActivity.currentContext);
                        }
                    }
                    else {
                        setLoadingIcon(View.INVISIBLE);
                        btnMyInvitations.setClickable(true);
                        showDialog("No invitations", null, MainActivity.currentContext);
                    }
                    break;
                }
            }
        }
    }
}