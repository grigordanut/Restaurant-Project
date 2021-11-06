package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UpdateUser extends AppCompatActivity {

    //declare variables
    private EditText newFirstName, newLastName, newPhone, newEmail;
    private TextView textViewEditProfile;
    private Button buttonSave;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        //initialise the variables
        newFirstName = (EditText) findViewById(R.id.etNewFirstName);
        newLastName = (EditText) findViewById(R.id.etNewLastName);
        newPhone = (EditText) findViewById(R.id.etNewPhone);
        newEmail = (EditText)findViewById(R.id.etNewEmail);

        textViewEditProfile = (TextView)findViewById(R.id.tvEditProfile);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        //load the user details in the edit texts
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot ds_User : dataSnapshot.getChildren()) {
                    FirebaseUser user_Db = firebaseAuth.getCurrentUser();

                    Users user_Data = ds_User.getValue(Users.class);

                    assert user_Db != null;
                    assert user_Data != null;
                    if (user_Db.getUid().equals(ds_User.getKey())){
                        newFirstName.setText(user_Data.getFirstName_User());
                        newLastName.setText(user_Data.getLastName_User());
                        newPhone.setText(user_Data.getPhoneNum_User());
                        newEmail.setText(user_Data.getEmail_User());
                        textViewEditProfile.setText("Edit profile of: " + user_Data.getFirstName_User()+" " + user_Data.getLastName_User());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateUser.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        //save the user details in the database
        buttonSave = (Button) findViewById(R.id.btnSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String newFirst_Name = newFirstName.getText().toString().trim();
                String newLast_Name = newLastName.getText().toString().trim();
                String new_Phone = newPhone.getText().toString().trim();
                String newEmail_Address = newEmail.getText().toString();

                String user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                DatabaseReference currentUser = databaseReference.child(user_id);
                Users user_update = new Users(newFirst_Name, newLast_Name, new_Phone,newEmail_Address);
                currentUser.setValue(user_update);

                //clear data input fields
                newFirstName.getText().clear();
                newLastName.getText().clear();
                newPhone.getText().clear();
                newEmail.getText().clear();

                progressDialog.dismiss();
                Toast.makeText(UpdateUser.this, "Your details has been changed successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateUser.this, LoginUser.class));
                finish();
            }
        });
    }
}
