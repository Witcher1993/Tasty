package com.example.sadulla.tasty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.sadulla.tasty.Common.Common;
import com.example.sadulla.tasty.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;

//

public class SignIn extends AppCompatActivity {
    //
    Button signInButton;
    MaterialEditText phoneEdit;
    MaterialEditText passwordEdit;
    CheckBox rememberCkb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //
        passwordEdit = (MaterialEditText)findViewById(R.id.sign_in_password_edit);
        phoneEdit = (MaterialEditText)findViewById(R.id.sign_in_phone_edit);

        //
        signInButton = (Button)findViewById(R.id.sign_in_sign_in_button);
        //

        rememberCkb = (CheckBox)findViewById(R.id.remember_check_box);

        //
        //Initialize Paper ==> open API to store current users login and password in NoSQL database
        Paper.init(this);



        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        //
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //CHECK WHETHER INTERNET CONNECTION IS VALID
                if (Common.isConnectedToInternet(getBaseContext())) {

                    //Save current Clients user and password
                    if (rememberCkb.isChecked())
                    {
                        Paper.book().write(Common.USER_KEY, phoneEdit.getText().toString());
                        Paper.book().write(Common.PWD_KEY, passwordEdit.getText().toString());
                    }


                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("WAIT...");
                    mDialog.show();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //check user validation
                            if (dataSnapshot.child(phoneEdit.getText().toString()).exists()) {
                                //Get User Information
                                mDialog.dismiss();
                                User user = dataSnapshot.child(phoneEdit.getText().toString()).getValue(User.class);
                                user.setPhone(phoneEdit.getText().toString());//set phone number
                                if (user.getPassword().equals(passwordEdit.getText().toString())) {

                                    Intent homeIntent = new Intent(SignIn.this, Home.class);
                                    Common.currentUser = user;
                                    startActivity(homeIntent);
                                    finish();

                                } else {
                                    Toast.makeText(SignIn.this, "FAIL", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "User does not exists", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else
                {
                    Toast.makeText(SignIn.this, "NO INTERNET!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });


    }
}
