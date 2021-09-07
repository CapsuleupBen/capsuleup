package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class lostConnection extends AppCompatActivity implements View.OnClickListener{
    // Activity used when there was a network error

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_connection);
    }

    @Override
    public void onClick(View v) {
        //in: View pressed
        //out: handles view press
        if(v==findViewById(R.id.btnReturn)) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }
}