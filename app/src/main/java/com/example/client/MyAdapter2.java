package com.example.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Queue;

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyViewHolder> {
    // List of Invitations (each item have phone, name, sub-area name, date, time)

    private String[] areasPhone, areasName, capsulesName, dates, times; // Details of each Item (Invitation)
    private Queue<String> sendQueue;    // Send queue of current Context
    private Context context;            // Current Context
    public MyAdapter2(Context c, String[] areasPhone, String[] areasName, String[] capsulesName, String[] dates, String[] times, Queue<String> sendQueue)
        //in: current Context, details about the Items (area phones, area names, capsule names, dates, times), sendQueue of the context
    {
        this.context = c;
        this.areasPhone = areasPhone;
        this.areasName = areasName;
        this.capsulesName = capsulesName;
        this.dates = dates;
        this.times = times;
        this.sendQueue = sendQueue;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Called when View Holder created
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row2, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        // Set values for each Item
        holder.myText1.setText(areasName[position]);
        holder.myText2.setText(capsulesName[position]);
        holder.myText3.setText(dates[position]);
        holder.myText4.setText(times[position]);
        holder.myText5.setText(areasPhone[position]);
    }

    @Override
    public int getItemCount() {
        // Return: Item count
        return areasPhone.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myText1, myText2, myText3, myText4, myText5;    // Text Views in each card inside the RecyclerView
        ConstraintLayout mainLayout;                             // Layout of the screen
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.cvTitleRow3);
            myText2 = itemView.findViewById(R.id.myTextView2);
            myText3 = itemView.findViewById(R.id.myTextView3);
            myText4 = itemView.findViewById(R.id.myTextView4);
            myText5 = itemView.findViewById(R.id.myTextView5);
            mainLayout = itemView.findViewById(R.id.mainLayout);

        }

    }

}
