package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Queue;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    // List of items (each item have title, description, image)

    private String[] titles, descriptions, imageStrs;   // Details about Items
    private Queue<String> sendQueue;                    // SendQueue of current Context
    private Context context;                            // Current context
    public MyAdapter(Context c, String[] titles, String[] descriptions, String[] imagesStr, Queue<String> sendQueue)
        //in: current Context, details about the Items (title, description, images), sendQueue of the context
    {
        this.context = c;
        this.titles = titles;
        this.descriptions = descriptions;
        this.imageStrs = imagesStr;
        this.sendQueue = sendQueue;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Called when View Holder created
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        // Set values for Item
        holder.myText1.setText(titles[position]);
        holder.myText2.setText(descriptions[position]);
        // Set Image for each item
        if(imageStrs == null || imageStrs[position].equals("-"))
            // There is No image
            holder.myImage.setImageResource(R.drawable.no_image);
        else {
            //There is image
            byte[] decodedBytes = android.util.Base64.decode(imageStrs[position], Base64.DEFAULT); // Base64.getDecoder().decode(imageStrs[position]);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.myImage.setImageBitmap(bitmap);
        }
        // Set onClickListener on each Item (different for each Activity)
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Business user:
                if(context.getClass().equals(business_myAreas.class))
                    // Move to Area details screen
                {
                    Intent i = new Intent(context, business_area_profile.class);
                    business_myAreas.areaPhoneNum = descriptions[position];
                    context.startActivity(i);
                }
                else if(context.getClass().equals(business_myCapsules.class))
                    // Move to My invitations Screen
                {
                    Intent i = new Intent(context, business_myInvitations.class);
                    business_myCapsules.capsuleName = titles[position];
                    context.startActivity(i);
                }
                // Private user:
                else if (context.getClass().equals(private_areasList.class)) {
                    // Move to area profile screen
                    private_areasList.areaPhoneNum = descriptions[position];
                    private_areasList.areaName = titles[position];
                    Intent i = new Intent(context, private_area_profile.class);
                    context.startActivity(i);
                }
                else if (context.getClass().equals(private_capsulesList.class)) {
                    // Get Invitations of sub-area
                    //((private_capsulesList) context).findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    ((private_capsulesList) context).setLoadingIcon(View.VISIBLE);
                    private_capsulesList.capsuleName = titles[position];
                    sendQueue.add("034" + private_areasList.areaPhoneNum + "#" + titles[position]);
                }
                // Scanner user:
                else if(context.getClass().equals(scanner_myAreas.class))
                    // Move to Scan screen
                {
                    Intent i = new Intent(context, scanner_Scan.class);
                    scanner_myAreas.areaPhoneNum = descriptions[position];
                    context.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return: Item count
        return titles.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myText1, myText2;   // Text Views in each card inside the RecyclerView
        ImageView myImage;           // Images in each card inside the RecyclerView
        ConstraintLayout mainLayout; // Layout of the screen
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.myTextView1);
            myText2 = itemView.findViewById(R.id.myTextView2);
            myImage = itemView.findViewById(R.id.myImageView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
