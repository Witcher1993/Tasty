package com.example.sadulla.tasty.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sadulla.tasty.Interface.ItemClickListener;
import com.example.sadulla.tasty.R;

import org.w3c.dom.Text;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView orderIdTextView;
    public TextView orderStatusTextView;
    public TextView orderAddressTextView;
    public TextView orderPhoneTextView;

    private ItemClickListener itemClickListener;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        orderIdTextView = (TextView)itemView.findViewById(R.id.order_id);
        orderStatusTextView = (TextView)itemView.findViewById(R.id.order_status);
        orderAddressTextView = (TextView)itemView.findViewById(R.id.order_address);
        orderPhoneTextView = (TextView)itemView.findViewById(R.id.order_phone);
    }

    @Override
    public void onClick(View view) {

    }
}
