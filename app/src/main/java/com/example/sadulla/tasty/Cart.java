package com.example.sadulla.tasty;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sadulla.tasty.Common.Common;
import com.example.sadulla.tasty.Database.Database;
import com.example.sadulla.tasty.Model.Order;
import com.example.sadulla.tasty.Model.Request;
import com.example.sadulla.tasty.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    TextView totalPriceTextView;

    Button placeButton;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;

    FirebaseDatabase database;
    DatabaseReference requests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView)findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        totalPriceTextView = (TextView)findViewById(R.id.total);

        placeButton = (Button)findViewById(R.id.place_order_button);

        loadListFood();


        placeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cart.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Your cart is empty!!!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void showAlertDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("On more step!!!");
        alertDialog.setMessage("Enter yout address: ");

        final EditText addressEditText = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        addressEditText.setLayoutParams(lp);
        alertDialog.setView(addressEditText);//Add edittext to alertdialog
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Create new Request
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        addressEditText.getText().toString(),
                        totalPriceTextView.getText().toString(),
                        cart
                );

                //Submission to FireBase
                //Use System.Current.Mills as a key
                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);
                //Delete Cart
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "Thank you!!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();

    }

    private void loadListFood() {
        //Creating new adapter to show from SQLite ==> Creating class
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


        //Calculating the total price
        int total = 0;
        for (Order order:cart)
        {
            total+=(Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

        }

        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        totalPriceTextView.setText(fmt.format(total));

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position) {
        //Remove item from List<Order> by it's position
        cart.remove(position);
        //After delete all data from SQLite
        new Database(this).cleanCart();
        //Update SQLite from List<Order>
        for (Order item:cart)
            new Database(this).addToCart(item);
        //Refresh page
        loadListFood();

    }
}
