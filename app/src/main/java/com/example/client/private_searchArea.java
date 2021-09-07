package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Queue;

public class private_searchArea extends MyAppCompatActivity implements View.OnClickListener {
    // private user - Search areas activity

    public static String areaName;    // area name to search full name
    private String[] searchRecommend; // list of Areas names

    private COM socketThread;         // Communication class (Sending & receiving)
    private EditText etAreaName;      // Search Edit text
    private FloatingActionButton btnSearchArea;     // continue to 'private_createAccount' / 'private_searchArea' screen
    private boolean first=true, searching=false;    // first = if first RV, searching if currently Searching

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_activity_search_area);
        MainActivity.currentContext = this;

        socketThread = new COM(sendQueue, this);
        sendQueue.add("016");
        btnSearchArea = findViewById(R.id.btnSearchArea);

        //Wait for change in Searched String - and get Recommendations based on that string
        etAreaName = findViewById(R.id.etAreaName);
        etAreaName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String str = etAreaName.getText().toString();
                if (functions.isValidString(str)) //str.matches(("[a-zA-Z1-9\\s\'\"]+")))
                    //if its a correct Name
                    sendQueue.add("016"+str);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
        etAreaName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return private_searchArea.this.onEditorAction(v, actionId, event, etAreaName);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.currentContext = this;

        socketThread = new COM(sendQueue, this);
        btnSearchArea.setClickable(true);
    }

    @Override
    public void onClick(View v)
    //in: View (after there was a click)
    //out: returns nothing. checks which button pressed etc..
    {
        if (v == btnSearchArea) {
            areaName = etAreaName.getText().toString();
            if (functions.isValidString(areaName)) //areaName.matches(("[a-zA-Z1-9\\s\'\"]+")))
                //if its a correct Name
            {
                searching = true;
                btnSearchArea.setClickable(false);
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                findViewById(R.id.layoutSearch).setVisibility(View.INVISIBLE);
                Intent i = new Intent(this, private_areasList.class);
                i.putExtra("searchString", areaName);
                startActivity(i);
            }
            else
                showDialog("Invalid Input", null, MainActivity.currentContext);
        }
    }

    public void showAreaRecommendations()
        // Displays the search recommendations
    {
        if(searching)
            return;
        final RecyclerView.Adapter myAdapter;
        myAdapter = new MyAdapter3(this, searchRecommend, sendQueue);
        final RecyclerView recyclerView = findViewById(R.id.recyclerView3);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(first)
                    recyclerView.setAdapter(myAdapter);
                else
                    recyclerView.swapAdapter(myAdapter, true);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.currentContext));
                findViewById(R.id.layoutSearch).setVisibility(View.VISIBLE);
                findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            }
        });
        first = false;
    }

    class COM extends TCP {
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
                case 116:
                    // Received search recommendations
                    if (args.length == 1) {
                        // There are search recommendations
                        String[] areas = args[0].split("&");
                        if (areas != null) {
                            searchRecommend = areas;
                            showAreaRecommendations();
                        } else
                            showDialog("Error", null, MainActivity.currentContext);
                    }
                    else {
                        // There are no search recommendations for this search
                        searchRecommend = new String[]{"No results"};
                        showAreaRecommendations();
                    }
                    break;
            }
        }
        @Override
        public void error400() {
            // When an Error 400 (Bad request) have occurred
            super.error400();
            searching = false;
            btnSearchArea.setClickable(true);
            setLoadingIcon(View.INVISIBLE);
        }
    }
}


