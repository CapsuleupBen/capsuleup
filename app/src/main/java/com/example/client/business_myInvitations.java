package com.example.client;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Queue;

public class business_myInvitations extends MyAppCompatActivity implements View.OnClickListener {
    // display invitations created by the business user - activity

    private String[] invitationsDate, invitationsTime;  // Details about each invitation
    private COM socketThread;        // Communication class (Sending & receiving)
    private Activity activity;       // Current Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_activity_list);
        activity = this;
        MainActivity.currentContext = this;
        socketThread = new COM(sendQueue, this);
        socketThread.sendQueue.add("362"+ business_myAreas.areaPhoneNum + "#" + business_myCapsules.capsuleName);
    }

    public void showInvitations()
            // Displays the Invitations list
    {
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        if(invitationsDate.length > 0) {
            final RecyclerView.Adapter myAdapter;
            if(!invitationsDate[0].equals(""))
                myAdapter = new MyAdapter1(this, invitationsDate, invitationsTime, sendQueue);
            else
                myAdapter = new MyAdapter3(this, invitationsTime, sendQueue);
            runOnUiThread(new Runnable() {
                public void run() {
                    recyclerView.setAdapter(myAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                    recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

                    findViewById(R.id.layoutMyList).setVisibility(View.VISIBLE);
                    findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
                }});

        }
    }
    @Override
    public void onClick(View v) {
        //in: View
        //out: nothing. handles the clicks on the View
        if(v == findViewById(R.id.btnAdd)) {
            socketThread.setActive(false);
            Intent i = new Intent(this, business_addInvitation.class);
            startActivity(i);
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
                case 462:
                    // Get invitations result
                    if(args.length==1) {
                        // Have invitations
                        String[][] areas = functions.strToArr(args[0]);
                        if(areas != null && areas.length != 0)
                        {
                            invitationsDate = functions.getIndexInArray(areas, 0);
                            invitationsTime = functions.getIndexInArray(areas, 1);
                            showInvitations();
                        }
                        else {
                            // Error
                            socketThread.sendQueue.add("362"+ business_myAreas.areaPhoneNum + "#" + business_myCapsules.capsuleName);
                            showDialog("Error", null,  MainActivity.currentContext);
                        }
                    }
                    else
                        // Doesn't have invitations
                    {
                        invitationsDate = new String[]{""};
                        invitationsTime = new String[]{"No invitations found"};
                        showInvitations();
                    }
                    break;
            }
        }
    }
}