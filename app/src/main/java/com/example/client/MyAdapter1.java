package com.example.client;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Queue;

public class MyAdapter1 extends RecyclerView.Adapter<MyAdapter1.MyViewHolder> {
    // List of items (each item have title, description)

    private String[] titles, descriptions; // Titles and descriptions of items in the RecyclerView
    private Queue<String> sendQueue;       // Send Queue of the current Context
    private Context context;               // Current context
    public MyAdapter1(Context c, String[] titles, String[] descriptions, Queue<String> sendQueue)
        //in: current Context, details about the Items (title, description), sendQueue of the context
    {
        this.context = c;
        this.titles = titles;
        this.descriptions = descriptions;
        this.sendQueue = sendQueue;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Called when View Holder created
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row1, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        // Set values for each Item
        holder.myText1.setText(titles[position]);
        holder.myText2.setText(descriptions[position]);
        // Set onClickListener on each Item
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Private user:
                if(context.getClass().equals(private_invitationsList.class)) {
                    // Go to book Invitation screen
                    private_invitationsList.invitationDate = titles[position];
                    private_invitationsList.invitationTime = descriptions[position];
                    Intent i = new Intent(context, private_bookInvitation.class);
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
        ConstraintLayout mainLayout; // Layout of the screen
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.myTextView1);
            myText2 = itemView.findViewById(R.id.myTextView2);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
