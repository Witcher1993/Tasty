package com.example.sadulla.tasty.ViewHolder;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.sadulla.tasty.Common.Common;
import com.example.sadulla.tasty.Interface.ItemClickListener;
import com.example.sadulla.tasty.Model.Order;
import com.example.sadulla.tasty.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    ,  View.OnCreateContextMenuListener{


    public TextView cartNameTextView;
    public TextView priceTextView;
    public ImageView cartCountImageView;

    private ItemClickListener itemClickListener;


    public void setCartNameTextView(TextView cartNameTextView) {
        this.cartNameTextView = cartNameTextView;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        cartNameTextView = (TextView)itemView.findViewById(R.id.cart_item_name);
        priceTextView = (TextView)itemView.findViewById(R.id.cart_item_price);
        cartCountImageView = (ImageView)itemView.findViewById(R.id.cart_item_count);

        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select action");
        menu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}


public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, viewGroup, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i) {
        TextDrawable drawable = TextDrawable.builder().buildRound(""+listData.get(i).getQuantity(), Color.RED);
        cartViewHolder.cartCountImageView.setImageDrawable(drawable);

        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(i).getPrice())) * (Integer.parseInt(listData.get(i).getQuantity()));
        cartViewHolder.priceTextView.setText(fmt.format(price));

        cartViewHolder.cartNameTextView.setText(listData.get(i).getProductName());


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
