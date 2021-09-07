package com.example.client;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class private_myInvitations extends MyAppCompatActivity {
    // Private user - my invitations activity

    private String[] invitationsAreaPhone, invitationsAreaName, invitationsCapsuleName, invitationsDate, invitationsTime; // Details about my Invitations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_activity_my_invitations);

        // Show the invitation details
        Intent i = getIntent();
        invitationsAreaPhone = i.getStringArrayExtra("invitationsAreaPhone");
        invitationsAreaName = i.getStringArrayExtra("invitationsAreaName");
        invitationsCapsuleName = i.getStringArrayExtra("invitationsCapsuleName");
        invitationsDate = i.getStringArrayExtra("invitationsDate");
        invitationsTime = i.getStringArrayExtra("invitationsTime");

        MyAdapter2 myAdapter = new MyAdapter2(this, invitationsAreaPhone, invitationsAreaName, invitationsCapsuleName, invitationsDate, invitationsTime, sendQueue);
        RecyclerView recyclerView = findViewById(R.id.recyclerView3);
        recyclerView.swapAdapter(myAdapter, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}