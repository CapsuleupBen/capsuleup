package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Queue;

public class private_areasList extends MyAppCompatActivity
    // Display areas list - activity
{
    public static String areaPhoneNum, areaName;    // phone & name of area chosen
    private static String[] areasName, areasDescription, imagesStr; // Details of each area

    private COM socketThread;        // Communication class (Sending & receiving)
    private Context context;         // This activity's context
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        MainActivity.currentContext = this;
        context = this;
        socketThread = new COM(sendQueue, this);

        Intent i = getIntent();
        if(i.hasExtra("searchString"))
            sendQueue.add("006" + i.getStringExtra("searchString"));
    }
    public void showAreas() {
        //in: nothing
        //out: Displays the areas received
        final RecyclerView.Adapter myAdapter;
        if(areasName!=null && areasName.length > 0 && areasName[0].equals(""))
            myAdapter = new MyAdapter3(context, areasDescription, sendQueue);
        else
            myAdapter = new MyAdapter(context, areasName, areasDescription, imagesStr, sendQueue);
        final RecyclerView recyclerView = findViewById(R.id.recyclerView3);
        runOnUiThread(new Runnable() {
            public void run() {
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                findViewById(R.id.recyclerView3).setVisibility(View.VISIBLE);
                findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            }
        });

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        areaPhoneNum=null;
        areaName=null;
        MainActivity.currentContext = this;
        socketThread = new COM(sendQueue, this);
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
                case 106:
                    // Received result of search - list of areas
                    if (args.length == 1) {
                        String[][] areas = functions.strToArr(args[0]);
                        if (areas != null) {
                            active = false;
                            areasName = functions.getIndexInArray(areas, 0);
                            areasDescription = functions.getIndexInArray(areas, 1);
                            imagesStr = functions.getIndexInArray(areas, 3);
                            showAreas();
                        }
                        else
                            showDialog("Error", null, context);
                    }
                    else {
                        // No areas found
                        areasName = new String[]{""};
                        areasDescription = new String[]{"No areas"};
                        showAreas();
                    }
                    break;
            }
        }
    }
}