package com.example.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.Queue;

public class MyAppCompatActivity extends AppCompatActivity {
    // Class includes basic onUi functions

    protected Queue<String> sendQueue;  // Queue of messages to send (new for each Context)

    protected void onCreate(Bundle savedInstanceState)
        // Called when view is created
    {
        super.onCreate(savedInstanceState);
        sendQueue = new LinkedList<String>();
    }
    public void showDialog(String title, final String text, final Context context) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_text, null);
        if(title!=null)
            alertDialogBuilder.setTitle(title);
        if(text!=null)
            alertDialogBuilder.setView(view);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }});

        final TextView tvDialogText = view.findViewById(R.id.tvDialogText);

        ((Activity)context).runOnUiThread(new Runnable() {
            public void run() {
                //Set Dialog Text
                tvDialogText.setText(text);
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                    }
        });
    }


    public void setTitleAction()
        //in: nothing
        //out: sets the title action
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setIcon(R.drawable.capsule_image);
    }

    public boolean onEditorAction(TextView v, int keyCode, KeyEvent event, EditText et)
        // Handle close keyboard call.
    {
        if (keyCode == 5 || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            // hide virtual keyboard
            closeKeyboard();
            return true;
        }
        return false;
    }

    public void closeKeyboard()
        //in: nothing
        //out: nothing. closes the keyboard if open
    {
        final View view = this.getCurrentFocus();
        if(view != null) {
            final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            runOnUiThread(new Runnable() {
                public void run() {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });
        }
    }
    public void Toast(final String msg)
        //in: Msg
        //out: Displays the message in Toast
    {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), ""+msg.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
    public void setLoadingIcon(final int visible) {
        if(visible==View.VISIBLE || visible == View.INVISIBLE)
            runOnUiThread(new Runnable() {
            public void run() {
                findViewById(R.id.loadingPanel).setVisibility(visible);
            }
        });
    }
}
