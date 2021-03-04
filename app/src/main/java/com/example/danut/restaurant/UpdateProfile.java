package com.example.danut.restaurant;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfile extends AppCompatActivity {

    //declare variables
    private EditText newFirstName, newLastName, newPhone, newEmail;
    private Button buttonSave;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        //initialise the variables
        newFirstName = (EditText) findViewById(R.id.etNewFirstName);
        newLastName = (EditText) findViewById(R.id.etNewLastName);
        newPhone = (EditText) findViewById(R.id.etNewPhoneNumber);
        newEmail = (EditText)findViewById(R.id.etNewEmail);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //load the user details in the edit texts
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.show();
                //retrieve data from database
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    UserProfile userProfile = child.getValue(UserProfile.class);

                    progressDialog.dismiss();
                    if (user.getEmail().equalsIgnoreCase(userProfile.Email_Address)) {
                        newFirstName.setText(userProfile.First_Name);
                        newLastName.setText(userProfile.Last_Name);
                        newPhone.setText(userProfile.Phone_Number);
                        newEmail.setText(userProfile.Email_Address);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UpdateProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        //save the user details in the database
        buttonSave = (Button) findViewById(R.id.btnSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();
                String newFirst_Name = newFirstName.getText().toString();
                String newLast_Name = newLastName.getText().toString();
                String newPhone_Number = newPhone.getText().toString();
                String newEmail_Adress = newEmail.getText().toString();

                String user_id = firebaseAuth.getCurrentUser().getUid();
                DatabaseReference currentUser = databaseReference.child(user_id);
                UserProfile userProf = new UserProfile(newFirst_Name, newLast_Name, newPhone_Number, newEmail_Adress);
                currentUser.setValue(userProf);

                //clear data input fields
                newFirstName.setText("");
                newLastName.setText("");
                newPhone.setText("");
                newEmail.setText("");

                progressDialog.dismiss();
                firebaseAuth.signOut();
                finish();
                Toast.makeText(UpdateProfile.this, "Your details has been changed successfully", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(UpdateProfile.this, LoginUser.class));
            }
        });
    }
}
