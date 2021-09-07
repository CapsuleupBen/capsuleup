package com.example.client;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Queue;

public class scanner_myAreas extends MyAppCompatActivity {
    // Scanner user - my areas activity

    public static String areaPhoneNum;      //Phone number of the area that is pressed
    private String[] areasName, areasPhone, areasImage; // name+phone of each area available
    private Context context;                // Current Context
    private COM socketThread;               // Communication class (Sending & receiving)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        MainActivity.currentContext = this;

        context = this;
        socketThread = new COM(sendQueue, this);
        socketThread.sendQueue.add("542");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.currentContext = this;
        socketThread = new COM(sendQueue, this);
    }

    public void showAreas()
        //Displays the Areas available
    {
        final RecyclerView.Adapter myAdapter;
        if(areasName!=null && areasName.length > 0 && areasName[0].equals(""))
            myAdapter = new MyAdapter3(this, areasPhone, sendQueue);
        else
            myAdapter = new MyAdapter(this, areasName, areasPhone, areasImage, sendQueue);
        final RecyclerView recyclerView = findViewById(R.id.recyclerView3);
        runOnUiThread(new Runnable() {
            public void run() {
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                recyclerView.setVisibility(View.VISIBLE);
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
                case 642:
                    if(args.length==1) {
                        // There are areas available
                        String[][] areas = functions.strToArr(args[0]);
                        if (areas != null) {
                            if (areas.length != 0) {
                                areasPhone = functions.getIndexInArray(areas, 0);
                                areasName = functions.getIndexInArray(areas, 1);
                                areasImage = functions.getIndexInArray(areas, 2);
                                showAreas();
                            }
                        }
                        else {
                            sendQueue.add("542");
                            showDialog("Error", null, context);
                        }
                    }
                    else {
                        // No areas available
                        areasName = new String[]{""};
                        areasPhone = new String[]{"No areas found"};
                        showAreas();
                    }
                    break;
            }
        }
    }
}
