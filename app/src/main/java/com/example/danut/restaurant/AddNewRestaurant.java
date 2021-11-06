package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.List;

public class AddNewRestaurant extends AppCompatActivity {

    //Retrieve data from Restaurant database
    private DatabaseReference databaseRefRestCheck;

    //Add data to Restaurant database
    private DatabaseReference databaseRefRest;

    private StorageTask restUploadTask;

    private EditText etRest_Name, etRest_Address;

    private String rest_Name, rest_Address;

    private ProgressDialog progressDialog;

    public List<Restaurants> restList;
    public List<Restaurants> restListCheck;

    private String rest_KeyAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_restaurant);

        restList = new ArrayList<>();
        restListCheck = new ArrayList<>();

        etRest_Name = findViewById(R.id.etRestName);
        etRest_Address = findViewById(R.id.etRestAddress);

        progressDialog = new ProgressDialog(this);

        Button buttonSaveRestaurant = findViewById(R.id.btnSaveRestaurant);
        buttonSaveRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restUploadTask != null && restUploadTask.isInProgress()) {
                    Toast.makeText(AddNewRestaurant.this, "Upload restaurant in Progress", Toast.LENGTH_SHORT).show();
                }
                checkRestaurantName();
            }
        });
    }

    private void checkRestaurantName() {
        final String etRest_CheckName = etRest_Name.getText().toString().trim();
        databaseRefRestCheck = FirebaseDatabase.getInstance().getReference().child("Restaurants");
        databaseRefRestCheck.orderByChild("rest_Name").equalTo(etRest_CheckName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            alertDialogRestExist();
                        } else {
                            loadRestaurantData();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AddNewRestaurant.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadRestaurantData() {

        progressDialog.dismiss();
        if (validateRestDetails()) {
            rest_Name = etRest_Name.getText().toString().trim();
            rest_Address = etRest_Address.getText().toString().trim();

            progressDialog.setMessage("Add New Restaurant");
            progressDialog.show();

            databaseRefRest = FirebaseDatabase.getInstance().getReference("Restaurants");

            String restID = databaseRefRest.push().getKey();
            assert restID != null;
            rest_KeyAdd = restID;
            Restaurants restaurants = new Restaurants(rest_Name, rest_Address, rest_KeyAdd);
            databaseRefRest.child(restID).setValue(restaurants).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        etRest_Name.setText("");
                        etRest_Address.setText("");

                        startActivity(new Intent(AddNewRestaurant.this, RestaurantImageShowRestAdmin.class));

                        Toast.makeText(AddNewRestaurant.this, "Restaurant Successfully Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddNewRestaurant.this, "Filed to add new Restaurant", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddNewRestaurant.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private Boolean validateRestDetails() {

        //validate Restaurant details
        boolean result = false;
        rest_Name = etRest_Name.getText().toString().trim();
        rest_Address = etRest_Address.getText().toString().trim();

        if (TextUtils.isEmpty(rest_Name)) {
            etRest_Name.setError("Enter Restaurant Name");
            etRest_Name.requestFocus();
        } else if (TextUtils.isEmpty(rest_Address)) {
            etRest_Address.setError("Enter Restaurant Address");
            etRest_Address.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    public void alertDialogRestExist() {
        final String etRest_alertCheckName = etRest_Name.getText().toString().trim();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("The Restaurant " + etRest_alertCheckName + " already exists!");
        alertDialogBuilder.setPositiveButton("OK",
                (arg0, arg1) -> {
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}