package com.example.sadulla.tasty;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button signUpButton;
    Button signInButton;
    TextView sloganTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = (Button)findViewById(R.id.main_sign_in_button);
        signUpButton = (Button)findViewById(R.id.main_sign_up_button);

        sloganTextView = (TextView)findViewById(R.id.slogan_text_view);
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/LEMON.TTF");
        sloganTextView.setTypeface(face);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Sign Up", Toast.LENGTH_SHORT).show();
                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Sign In", Toast.LENGTH_SHORT).show();
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);
            }
        });


    }
}
