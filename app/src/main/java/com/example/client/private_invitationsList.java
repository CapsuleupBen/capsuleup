package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class private_invitationsList extends MyAppCompatActivity {
    // Private user - invitations list activity
    public static String invitationDate, invitationTime;      // Time&date of chosen Invitation
    private static String[] invitationsDate, invitationsTime; // Time and date of All invitations available for chosen sub-area

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        MainActivity.currentContext = this;

        Intent i = getIntent();
        if (i.hasExtra("invitationsDate")) {
            invitationsDate = i.getStringArrayExtra("invitationsDate");
            if (i.hasExtra("invitationsTime")) {
                invitationsTime = i.getStringArrayExtra("invitationsTime");
                MyAdapter1 myAdapter = new MyAdapter1(this, invitationsDate, invitationsTime, new LinkedList<String>());
                RecyclerView recyclerView = findViewById(R.id.recyclerView3);
                recyclerView.swapAdapter(myAdapter, true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

                findViewById(R.id.recyclerView3).setVisibility(View.VISIBLE);
                findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.currentContext = this;
        invitationTime = null;
        invitationDate = null;
    }
}