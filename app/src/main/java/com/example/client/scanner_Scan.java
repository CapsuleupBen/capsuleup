package com.example.client;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import java.util.Queue;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class scanner_Scan extends MyAppCompatActivity implements ZXingScannerView.ResultHandler {
    // Scanner client - Scan activity

    public static int RC_CAMERA=1;          // Camera RC
    private COM socketThread;               // Communication class
    private String clientName="", clientPhone="", invTime="", invDate="", title=""; // Current scanned details
    private ZXingScannerView mScannerView;  // Scanner Class
    private String lastScan;                // Last scan Input
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.scanner_activity_scan);
        MainActivity.currentContext = this;

        socketThread = new COM(sendQueue, this);

        mScannerView = (ZXingScannerView)findViewById(R.id.scanner);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            checkPermission();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentContext = this;
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
        mScannerView.setAutoFocus(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    private void checkPermission() {
        //in: nothing
        //out: checks if the user gave the application Camera Permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, RC_CAMERA);
        else
            showDialog("Device cannot be used as Scanner (SDK<23)", null, MainActivity.currentContext);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
        //handle Request Camera permission result (Granted / denied)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if( requestCode == RC_CAMERA && grantResults.length > 0)
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ;
            else {
                showDialog("You must accept Camera permission", null, MainActivity.currentContext);
                finish();
            }
    }
    public void showInvitation() {
        //in: nothing
        //out: returns nothing. sets the Values of the TextViews on the scanner_Scan screen - to the newest.
        final TextView tvClientName=findViewById(R.id.tvClientName), tvClientPhone=findViewById(R.id.tvClientPhone), tvInvDate=findViewById(R.id.tvInvitationDate), tvInvTime=findViewById(R.id.tvInvitationTime), tvTitle=findViewById(R.id.tvTitle);
        final ZXingScannerView.ResultHandler resultHandler = this;
        runOnUiThread(new Runnable() {
            public void run() {
                tvClientName.setText(clientName);
                tvClientPhone.setText(clientPhone);
                tvInvDate.setText(invDate);
                tvInvTime.setText(invTime);
                tvTitle.setText(title);
                findViewById(R.id.layoutScanDetails).setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 3s
                        mScannerView.resumeCameraPreview(resultHandler);
                    }
                }, 3000);


            }
        });

    }

    @Override
    public void handleResult(Result rawResult) {
        //in: Result of scanner
        //out: handles result

        final ZXingScannerView.ResultHandler resultHandler = this;
        // Restart Screen Timer
        Chronometer simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer);
        simpleChronometer.setBase(SystemClock.elapsedRealtime());
        simpleChronometer.stop();
        simpleChronometer.start();

        if(!rawResult.getText().equals(lastScan)) {
            lastScan = rawResult.getText();
            // Send barcode
            if (functions.isValidString(lastScan))
                // if the barcode is valid
                socketThread.sendQueue.add("555" + scanner_myAreas.areaPhoneNum + "#" + rawResult.getText());
            else
                // Invalid barcode
                mScannerView.resumeCameraPreview(resultHandler);
        }
        else
            mScannerView.resumeCameraPreview(resultHandler);
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
                case 655:
                    // Received data about Scanned Barcode
                    if(args.length == 5 && functions.isInteger(args[0]) && Integer.parseInt(args[0]) == 2) {
                        //Valid client
                        clientName = args[1];
                        clientPhone = args[2];
                        invDate = args[3];
                        invTime = args[4];
                        title = "Approved";
                        showInvitation();
                        break;
                    }
                    else
                        showDialog("Error", null, MainActivity.currentContext);
                    break;
                case 656:
                    if(args.length == 1) {
                        //Invalid barcode
                        int statusCode = Integer.parseInt(args[0]);
                        clientName = "";
                        clientPhone = "";
                        invDate = "";
                        invTime = "";
                        switch (statusCode) {
                            case 3:
                                // Invalid barcode
                                title = "Invalid barcode";
                                break;
                            case 4:
                                // Scanner doesn't have privileges for this area
                                title = "Error";
                                break;
                            case 5:
                                // Scanned for wrong area
                                title = "Wrong area";
                                break;
                            default:
                                // Error
                                title = "Error";
                                break;
                        }
                        showInvitation();
                    }
                    break;
            }
        }
    }

}