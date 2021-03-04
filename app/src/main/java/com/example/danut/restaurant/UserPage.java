package com.example.danut.restaurant;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserPage extends AppCompatActivity {

    //Declare variables
    private TextView textViewFirstName, textViewLastName, textViewPhone, textViewEmail, textViewWelcome;

    private Button buttonUpdateProfile, buttonChangePassword;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        //initialise the variables
        textViewFirstName =  findViewById(R.id.tvFirstNameUserPage);
        textViewLastName = findViewById(R.id.tvLastNameUserPage);
        textViewPhone = findViewById(R.id.tvPhoneNumberUserPage);
        textViewEmail = findViewById(R.id.tvEmailUserPage);

        textViewWelcome = (TextView) findViewById(R.id.tvWelcome);

        textViewFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });

        textViewLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });

        textViewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });

        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //retrive data from database into text views
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //retrieve data from database
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            UserProfile userProfile = child.getValue(UserProfile.class);

                            if (user.getEmail().equalsIgnoreCase(userProfile.Email_Address)) {
                                textViewFirstName.setText(userProfile.First_Name);
                                textViewLastName.setText(userProfile.Last_Name);
                                textViewPhone.setText(userProfile.Phone_Number);
                                textViewEmail.setText(userProfile.Email_Address);

                                textViewWelcome.setText("Welcome: "+userProfile.First_Name+" "+userProfile.Last_Name);
                            }
                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(UserPage.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });

        buttonUpdateProfile = (Button) findViewById(R.id.btnUpdateProfile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPage.this, UpdateProfile.class);
                startActivity(intent);
            }
        });
    }

    //lert dialog the notify the user
    public void alertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Click EDIT PROFILE to change your details");
                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //user log out
    private void LogOut(){
        firebaseAuth.signOut();
        textViewFirstName.setText("");
        textViewLastName.setText("");
        textViewPhone.setText("");
        textViewEmail.setText("");
        finish();
        startActivity(new Intent(UserPage.this, MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOutUser:{
                LogOut();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
