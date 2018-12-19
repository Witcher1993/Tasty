package com.example.sadulla.tasty;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sadulla.tasty.Common.Common;
import com.example.sadulla.tasty.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {

    MaterialEditText phoneEdit;
    MaterialEditText nameEdit;
    MaterialEditText passwordEdit;

    Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        phoneEdit = (MaterialEditText)findViewById(R.id.sign_up_phone_edit);
        nameEdit = (MaterialEditText) findViewById(R.id.sign_up_name_edit);
        passwordEdit = (MaterialEditText)findViewById(R.id.sign_up_password_edit);

        signUpButton = (Button)findViewById(R.id.sign_up_sign_up_button);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //CHECK WHETHER INTERNET CONNECTION IS VALID
                if(Common.isConnectedToInternet(getBaseContext())) {

                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("WAIT...");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Chcek if user already exists
                            if (dataSnapshot.child(phoneEdit.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Phone Number exists", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();
                                User user = new User(nameEdit.getText().toString(), passwordEdit.getText().toString());
                                table_user.child(phoneEdit.getText().toString()).setValue(user);
                                Toast.makeText(SignUp.this, "You successfully registered", Toast.LENGTH_SHORT).show();
                                finish();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else
                {
                    Toast.makeText(SignUp.this, "NO INTERNET!!!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }
}
