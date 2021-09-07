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

public class MyAdapter3 extends RecyclerView.Adapter<MyAdapter3.MyViewHolder> {
    // List of items (each item have title)

    private String[] titles;            // Titles of the Items
    private Queue<String> sendQueue;    // Send Queue of current context
    private Context context;            // current Context
    public MyAdapter3(Context c, String[] titles, Queue<String> sendQueue)
        //in: current Context, details about the Items (title), sendQueue of the context
    {
        this.context = c;
        this.titles = titles;
        this.sendQueue = sendQueue;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Called when View Holder created
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row3, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        // Set values for Item
        holder.myText1.setText(titles[position]);
        // Set onClickListener on each Item (different for each Activity)
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Private user:
                if(context.getClass().equals(private_searchArea.class))
                    // Go to areas List screen
                {
                    Intent i = new Intent(context, private_areasList.class);
                    i.putExtra("searchString", titles[position]);
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
        TextView myText1;               // The text that is in each card inside the RecyclerView
        ConstraintLayout mainLayout;    // Layout of the screen
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.cvTitleRow3);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
