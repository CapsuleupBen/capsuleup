package com.example.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Queue;

public class TCP {
    // Communication class
    private static String login_command;       // Create account command

    protected static RSA public_key;           // Public RSA key that the Server generated.
    private static byte[] aes_key;             // Symetric key that can be used for encryption and decrytion
    protected static boolean initialized_enc;  // Does the Server has the client AES key?

    protected static String phoneNum;          // client Phone number
    protected static int port;                 // connection Port
    protected static String server_ip;         // server IP

    protected boolean active;           // if this connection is active
    protected Queue<String> sendQueue;  // queue with all the messages that the Client wants to send
    protected Context context;

    public SendThread sendThread;       // sending messages thread
    public RecvThread recvThread;       // receiving messages thread
    public static Socket socket;        // Main socket
    public static OutputStream outp;    // Send messages Stream
    public static DataInputStream inp;  // Receive messages Stream

    TCP(String server_ip, int port, String phoneN, Queue<String> sendQueue, Context c, String login_command)
    //in: Server ip, port of connection, client phone number
    //out: creates The TCP connection and handles it.
    {
        //AES key generation:
        SecureRandom secureRandom = new SecureRandom();
        aes_key = new byte[32];
        secureRandom.nextBytes(aes_key);
        this.initialized_enc = false;

        this.login_command = login_command;
        this.context = c;
        this.server_ip = server_ip;
        this.port = port;
        this.phoneNum = phoneN;
        this.active = true;
        this.socket = null;
        this.sendQueue = sendQueue;
        // start threads:
        sendThread = new SendThread();
        recvThread = new RecvThread();
        sendThread.start();
        recvThread.start();
    }

    TCP(Queue<String> sendQueue, Context c)
    //in: sendQueue
    //out: runs the TCP connection and handles it.
    {
        this.context = c;
        this.active = true;
        this.sendQueue = sendQueue;
        // start threads:
        sendThread = new SendThread();
        sendThread.start();
        recvThread = new RecvThread();
        recvThread.start();
    }

    public void handleMsg(String msg)
    //in: message received from Server
    //out: returns nothing. handles the Message.
    {
        if(msg.length() > 1000)
            Log.i("receivedData", "Received: " + msg.substring(0, 3) + "... "+msg.length());
        else
            Log.i("receivedData", "Received: " + msg);
        if(!initialized_enc) {
            // After Server received AES key
            int command = getCommand(msg);
            String[] args = getArgs(msg);
            switch (command) {
                case 901:
                    // RSA public key received
                    if (args.length == 1) {
                        // Encrypt the AES key with the RSA public key
                        String public_key_str = args[0];
                        this.public_key = new RSA(public_key_str);
                        byte[] encryptedAesKey = public_key.encrypt(aes_key);
                        // Send the encrypted AES key to the server
                        byte[] toSend = functions.appendByteArrays("911".getBytes(), encryptedAesKey);
                        sendThread.sendMsg(toSend);
                        // Log-in to Server
                        sendQueue.add(login_command + phoneNum);
                        initialized_enc = true;
                    }
                    break;
                case 777:
                    // Client is ignored by Server
                    ((MainActivity.COM)this).error777();
                    break;
            }

        }
        else {
            int command = getCommand(msg);
            switch (command) {
                case 400:
                    // Invalid Input error
                    error400();
                    break;
                case 408:
                    //Disconnect Client
                    clientError();
                    break;
            }
        }
    }
    public void error400() {
        // When an Error 400 (Bad request) have occurred
        ((MyAppCompatActivity)context).showDialog("Error 400 - Bad Request", null, context);
    }
    public void setActive(boolean flag)
            //in: boolean value
            //out: returns nothing. sets the Active value to the flag's value
    {
        this.active = flag;
    }
    public void initConnection()
            //in: nothing
            //out: returns True if initializes successfully.
    {
        // creating socket connection
        try {
            this.socket = new Socket(server_ip, port);
            inp = new DataInputStream(socket.getInputStream());
            outp = socket.getOutputStream();
        } catch (Exception e) {
            Log.e("connectError", "" + e.toString());
            clientError();
        }
    }
    public void clientError()
            //in: nothing.
            //out: closes the connection.
    {
        closeConnection();
        Intent i = new Intent(context, lostConnection.class);
        context.startActivity(i);
    }
    public void closeConnection() {
        if (socket!=null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (Exception e) {
                Log.e("disconnectError", "" + e.toString());
            }
        }
        socket = null;
        active = false;
    }

    class RecvThread extends Thread {
        // Thread receives messages
        @Override
        public void run() {
                //Initialize sockets
                while(socket==null && active && MainActivity.currentContext.equals(context))
                    initConnection();
                //Receive messages
                while (active && MainActivity.currentContext.equals(context))
                    this.receiveMsg();
                //Disable thread
                if(!MainActivity.currentContext.equals(context))
                    active = false;
        }
        public int receiveMsgLen() {
            //in: nothing
            //out: returns Message length to receive (first 10 bytes of message indicate the length)
            boolean valid_length = true;
            byte[] msg_len = new byte[10];
            // Get length of message
            try {
                if (inp.available() >= 10)
                    inp.read(msg_len);
            } catch (Exception e) {
                valid_length = false;
            }
            if (valid_length && functions.isNumber(msg_len))
                // Valid length
                return functions.byteArrToInt(msg_len);
            return 0;
        }
        public void receiveMsg() {
            //in: nothing
            //out: Receives and handles message.
            int msg_len = this.receiveMsgLen(); // Length of message to receive
            if(msg_len == 0)  //
                return;
            byte[] msg = new byte[msg_len];     // Received message
            int last_read_len = 0;              // Length of the last message received
            int received_counter = 0;           // Length of full message received (used when len>1024)
            while (msg_len > 1024 && active && MainActivity.currentContext.equals(context))
                //  message is bigger then 1024 Bytes
            {
                boolean received = false;      // True Received data
                byte[] tmp = new byte[1024];   // Receives 1024 byte message and adds it to final message
                try {
                     if(inp.available() > 1024) {
                        last_read_len = inp.read(tmp);
                        received = true;
                    }
                } catch (Exception e) {
                    clientError(); }
                if(received) {
                    // Put the 1024 bytes received into final message
                    for (int i = received_counter; i < received_counter + 1024; i++)
                        msg[i] = tmp[i - received_counter];
                    msg_len -= last_read_len;           // Remove the size received from the size that needs to be received
                    received_counter += last_read_len;  // Add the size received to the final size received
                }
            }
            byte[] tmp = new byte[msg_len];
            last_read_len = 0;
            while (active && MainActivity.currentContext.equals(context))
                // Receive Last part of message (< 1024 bytes)
            {
                try {
                    if(inp.available() >= msg_len)
                        last_read_len = inp.read(tmp);
                } catch (Exception e)  {
                    clientError(); }
                if(last_read_len != 0)
                    break;
            }
            // Put last part into message
            for(int i=0;i<tmp.length;i++)
                msg[msg.length-msg_len+i] = tmp[i];

            // Handle Received message
            if(!initialized_enc)
                // Key is not initialized yet - Do not Decrypt
                handleMsg(new String(msg));
            else
                // key is Initialized - Decrypt message
                handleMsg(new String(AES.decrypt(msg, aes_key)));
        }
    }
    class SendThread extends Thread {
        // thread ables to Send encrypted messages via sockets from Main Activity
        @Override
        public void run() {
            while(active && MainActivity.currentContext.equals(context))
                while(!sendQueue.isEmpty() && active && MainActivity.currentContext.equals(context)) {
                    String value = sendQueue.remove();
                    // Encrypt with the AES key
                    byte[] toSend = AES.encrypt(value, aes_key);
                    if(toSend!=null)
                        sendMsg(toSend);
                }
        }
        public void sendMsg(@NonNull byte[] msg)
        //in: String to send
        //out: returns nothing. sends the message
        {
            if (socket!= null && !socket.isClosed() && msg.length > 0) {
                byte[] len =  String.format("%010d", Integer.parseInt(""+msg.length)).getBytes();
                //toSend = length + msg;
                byte[] toSend = functions.appendByteArrays(len, msg);
                try {
                    outp.write(toSend);
                    outp.flush();
                } catch (Exception e) {
                    if (e.toString().equals("java.net.SocketException: Connection reset"))
                        clientError();
                    Log.e("sendData", "" + e.toString());
                }
            }
        }
    }
    public int getCommand(String msg)
    //in: message received from Server
    //out: returns Command from message (if error message - returns 0 )
    {
        int command = 0;
        if(msg.length()>=3) {
            String s = msg.substring(0, 3);
            if (s != null && functions.isInteger(s)) //s.matches("[-+]?\\d*\\.?\\d+"))
                //make sure that the first 3 digits are Command ( Integer )
                command = Integer.parseInt(s);
        }
        return command;
    }
    public String[] getArgs(String msg)
    //in: message received from Server
    //out: returns args Array from message (if there aren't - returns empty arr)
    {
        String [] args = {};
        if (msg.length() > 3)
            args = msg.substring(3).split("#");
        return args;
    }
}


