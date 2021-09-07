package com.example.client;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Queue;

public class business_myCapsules extends MyAppCompatActivity implements View.OnClickListener {
    // display sub-areas owned by the business user - activity

    public static String capsuleName;   // Capsule name of the Capsule chosen
    private String[] capsulesName, capsulesDescription, capsulesImage; // Details about each capsule
    private COM socketThread;           // Communication class (Sending & receiving)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_activity_list);//business_activity_my_capsules);
        MainActivity.currentContext = this;

        socketThread = new COM(sendQueue, this);
        socketThread.sendQueue.add("352" + business_myAreas.areaPhoneNum);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.currentContext = this;
    }

    @Override
    public void onClick(View v) {
        //in: View pressed
        //out: returns nothing. handles click on the view.
        if(v == findViewById(R.id.btnAdd)) {
            socketThread.setActive(false);
            Intent i = new Intent(this, business_addCapsule.class);
            startActivity(i);
        }
    }

    public void showCapsules()
            //in: nothing
            //out: shows the Capsules of the area
    {
        final Context context = this;

        final RecyclerView.Adapter myAdapter;
        if(!capsulesName[0].equals(""))
            myAdapter = new MyAdapter(this, capsulesName, capsulesDescription, capsulesImage , sendQueue);
        else
            myAdapter = new MyAdapter3(this, capsulesDescription, sendQueue);

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        runOnUiThread(new Runnable() {
            public void run() {
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                findViewById(R.id.layoutMyList).setVisibility(View.VISIBLE);
                findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            }
        });
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
                case 452: {
                    // Get capsules result
                    if(args.length==1) {
                        // Have Capsules
                        String[][] areas = functions.strToArr(args[0]);
                        if(areas != null && areas.length != 0) {
                            capsulesName = functions.getIndexInArray(areas, 0);
                            capsulesDescription = functions.getIndexInArray(areas, 1);
                            capsulesImage = functions.getIndexInArray(areas, 2);
                            showCapsules();
                        }
                        else {
                            sendQueue.add("352" + business_myAreas.areaPhoneNum);
                            showDialog("Error", null, MainActivity.currentContext);
                        }
                    }
                    else if(args.length==0)
                        // Doesn't have capsules
                    {
                        capsulesName = new String[]{""};
                        capsulesDescription = new String[]{"No capsules found"};
                        showCapsules();
                    }
                    break;
                }
            }
        }
    }
}