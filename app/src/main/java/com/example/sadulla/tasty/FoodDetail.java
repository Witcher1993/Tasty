package com.example.sadulla.tasty;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.sadulla.tasty.Database.Database;
import com.example.sadulla.tasty.Model.Food;
import com.example.sadulla.tasty.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetail extends AppCompatActivity {

    TextView food_name;
    TextView food_price;
    TextView food_description;

    ImageView food_image;

    CollapsingToolbarLayout collapsingToolbarLayout;

    FloatingActionButton cartButton;

    ElegantNumberButton numberButton;

    String foodId="";

    FirebaseDatabase database;
    DatabaseReference foods;

    Food currentFood;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food");

        //Init View
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        cartButton = (FloatingActionButton)findViewById(R.id.cart_button);

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()

                ));
                Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        food_name = (TextView)findViewById(R.id.food_name_detail);
        food_price = (TextView)findViewById(R.id.food_price_detail);
        food_description = (TextView)findViewById(R.id.food_description_detail);
        food_image = (ImageView)findViewById(R.id.img_food);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);


        //Getting FoodId from Intent
        if(getIntent() != null )
            foodId = getIntent().getStringExtra("FoodId");

        if(!foodId.isEmpty())
        {
            getDetailFood(foodId);
        }

    }

    private void getDetailFood(final String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                //Image
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);

                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_price.setText(currentFood.getPrice());

                food_name.setText(currentFood.getName());

                food_description.setText(currentFood.getDescription());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
