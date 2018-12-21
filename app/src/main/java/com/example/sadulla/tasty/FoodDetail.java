package com.example.sadulla.tasty;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.sadulla.tasty.Common.Common;
import com.example.sadulla.tasty.Database.Database;
import com.example.sadulla.tasty.Model.Food;
import com.example.sadulla.tasty.Model.Order;
import com.example.sadulla.tasty.Model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.lang.reflect.Array;
import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    //Init widgets
    TextView food_name;
    TextView food_price;
    TextView food_description;

    ImageView food_image;

    CollapsingToolbarLayout collapsingToolbarLayout;

    FloatingActionButton cartButton;
    FloatingActionButton rateButton;

    ElegantNumberButton numberButton;

    RatingBar ratingBar;

    String foodId="";

    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTable;

    Food currentFood;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food"); //Reference for Food key
        ratingTable = database.getReference("Rating");//Reference for Rating key

        //Init View
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        cartButton = (FloatingActionButton)findViewById(R.id.cart_button);
        rateButton = (FloatingActionButton)findViewById(R.id.rate_button);
        ratingBar = (RatingBar)findViewById(R.id.food_detail_rating_bar);


        //Rate button
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

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
            //CHECK WHETHER INTERNET CONNECTION IS VALID
            if (Common.isConnectedToInternet(getBaseContext())) {
                //Get food description
                getDetailFood(foodId);
                //Get food rating
                getRatingFood(foodId);
            }
            else
            {
                Toast.makeText(FoodDetail.this, "NO INTERNET!!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }



    //Get food Rating
    private void getRatingFood(String foodId) {

        Query foodRating = ratingTable.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {

            int count=0;
            int sum=0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }

                if (count != 0)
                {
                    float average = sum/count;
                    ratingBar.setRating(average);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //This meyhod shows Rating Dialog
    private void showRatingDialog() {

        new AppRatingDialog.Builder()
                .setPositiveButtonText("SUBMIT")
                .setNegativeButtonText("CANCEL")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Bad", "Quite Ok", "Very Godd", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("SELECT SOME STAR AND FEEDBACK")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Comment")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();

    }


    //Getting food detail
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


    //Listeners for AppRating
    //Negative
    @Override
    public void onNegativeButtonClicked() {

    }

    //Positive
    @Override
    public void onPositiveButtonClicked(int value, String comments) {
        //Get rate and upload to firebase
        final Rating rating = new Rating(Common.currentUser.getPhone(), foodId, String.valueOf(value), comments);

        ratingTable.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currentUser.getPhone()).exists())
                {
                    //Remove pld value
                    ratingTable.child(Common.currentUser.getPhone()).removeValue();
                    //Update new value
                    ratingTable.child(Common.currentUser.getPhone()).setValue(rating);
                }
                else
                {
                    //Update new value
                    ratingTable.child(Common.currentUser.getPhone()).setValue(rating);
                }

                Toast.makeText(FoodDetail.this, "Thank you!!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
