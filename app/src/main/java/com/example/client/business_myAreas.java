package com.example.client;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Queue;

public class business_myAreas extends MyAppCompatActivity implements View.OnClickListener {
    // display areas owned by the business user - activity
    public static String areaPhoneNum;  // Phone number of the current Area pressed
    private String[] areasName, areasPhone, imagesStr;  // details to display about all areas received

    private COM socketThread;           // Communication class (Sending & receiving)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_activity_list);//business_activity_my_areas);
        MainActivity.currentContext = this;

        socketThread = new COM(sendQueue, this);
        socketThread.sendQueue.add("342");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.currentContext = this;
        socketThread = new COM(sendQueue, this);
    }

    @Override
    public void onClick(View v) {
        //in: View
        //out: nothing. handles the clicks on the View
        if(v == findViewById(R.id.btnAdd)) {
            socketThread.setActive(false);
            Intent i = new Intent(this, business_addArea.class);
            startActivity(i);
        }
    }
    public void showAreas()
        //in: nothing
        //out: shows the areas owned by this business user
    {
        final Context context = this;

        final RecyclerView.Adapter myAdapter;
        if(areasName!=null && areasName.length > 0 && areasName[0].equals(""))
            myAdapter = new MyAdapter3(context, areasPhone, sendQueue);
        else
            myAdapter = new MyAdapter(context, areasName, areasPhone, imagesStr, sendQueue);

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
                case 442:
                    // Get areas result
                    if(args.length==1) {
                        // The user has Areas
                        String[][] areas = functions.strToArr(args[0]);
                        if(areas != null && areas.length != 0)
                        {
                            areasName = functions.getIndexInArray(areas, 0);
                            areasPhone = functions.getIndexInArray(areas, 1);
                            imagesStr = functions.getIndexInArray(areas, 3);
                            showAreas();
                        }
                        else {
                            // Error
                            sendQueue.add("342");
                            showDialog("Error", null,  MainActivity.currentContext);
                        }
                    }
                    else {
                        // The user has No Areas
                        areasName = new String[]{""};
                        areasPhone = new String[]{"No areas"};
                        showAreas();
                    }
                    break;
            }
        }
    }
}
