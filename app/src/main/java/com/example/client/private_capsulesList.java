package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Queue;

public class private_capsulesList extends MyAppCompatActivity {
    // Capsules list activity

    public static String capsuleName;   // Name of chosen capsule

    private static String[] capsulesName, capsulesDescription, capsulesImage; // name, description, image of each capsule

    private COM socketThread;           // Communication class (Sending & receiving)
    private Context context;            // Current context
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        MainActivity.currentContext = this;
        context = this;
        socketThread = new COM(sendQueue, this);

        sendQueue.add("024" + private_areasList.areaPhoneNum);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        capsuleName = null;
        MainActivity.currentContext = this;
        socketThread = new COM(sendQueue, this);
    }
    public void showCapsules() {
        // Display list of Capsules in the area
        if (capsulesName != null && capsulesDescription != null) {
            final RecyclerView.Adapter myAdapter;

            if(capsulesName!=null && capsulesName.length > 0 && capsulesName[0].equals(""))
                myAdapter = new MyAdapter3(context, capsulesDescription, sendQueue);
            else
                myAdapter = new MyAdapter(this, capsulesName, capsulesDescription, capsulesImage, sendQueue);

            final RecyclerView recyclerView = findViewById(R.id.recyclerView3);
            runOnUiThread(new Runnable() {
                public void run() {
                    recyclerView.swapAdapter(myAdapter, true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

                    findViewById(R.id.recyclerView3).setVisibility(View.VISIBLE);
                    findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
                }
            });
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
                case 134:
                    // There go to Invitations
                    if(args.length==4)
                    {
                        setLoadingIcon(View.INVISIBLE);
                        if(args[3] != null && args[3].equals("-"))
                            showDialog("No invitations found", null, context);
                        else {
                            String[][] invitations = functions.strToArr(args[3]);
                            if (invitations != null) {
                                // screen where you can see all the Invitations
                                active = false;
                                Intent i = new Intent(context, private_invitationsList.class);
                                i.putExtra("invitationsDate", functions.getIndexInArray(invitations, 0));
                                i.putExtra("invitationsTime", functions.getIndexInArray(invitations, 1));
                                startActivity(i);
                            } else
                                showDialog("No invitations found", null, context);
                        }
                    }
                    break;
                case 129: {
                    // There are no sub-areas
                    capsulesName = new String[]{""};
                    capsulesDescription = new String[]{"No results"};
                    showCapsules();
                    break;
                }
                case 124:
                    // Received Capsules
                    if (args.length == 1) {
                        String[][] capsules = functions.strToArr(args[0]);
                        if (capsules != null) {
                            capsulesName =  functions.getIndexInArray(capsules, 0);
                            capsulesDescription =  functions.getIndexInArray(capsules, 1);
                            capsulesImage = functions.getIndexInArray(capsules, 2);
                            showCapsules();
                        }
                        else
                            showDialog("Error", null, context);
                    }
                    break;
            }
        }
    }
}