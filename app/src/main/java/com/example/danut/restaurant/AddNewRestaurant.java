package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;

import java.util.Objects;

public class AddNewRestaurant extends AppCompatActivity {

    //Retrieve data from Restaurant database
    private DatabaseReference databaseRefRestCheck;

    //Add data to Restaurant database
    private DatabaseReference databaseRefRest;

    private StorageTask restUploadTask;

    private EditText etRest_Name, etRest_Address;

    private String rest_Name, rest_Address;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_restaurant);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN: Add new restaurant");

        progressDialog = new ProgressDialog(this);

        //Checks the if the Restaurant name already exist into database
        databaseRefRestCheck = FirebaseDatabase.getInstance().getReference("Restaurants");

        //Create table Restaurants into database
        databaseRefRest = FirebaseDatabase.getInstance().getReference("Restaurants");

        etRest_Name = findViewById(R.id.etRestName);
        etRest_Address = findViewById(R.id.etRestAddress);

        Button btn_SaveRestaurant = findViewById(R.id.btnSaveRestaurant);
        btn_SaveRestaurant.setOnClickListener(v -> {
            if (restUploadTask != null && restUploadTask.isInProgress()) {
                Toast.makeText(AddNewRestaurant.this, "Upload restaurant in Progress", Toast.LENGTH_SHORT).show();
            }
            checkRestaurantName();
        });
    }

    private void checkRestaurantName() {

        final String etRest_CheckName = etRest_Name.getText().toString().trim();

        databaseRefRestCheck.orderByChild("rest_Name").equalTo(etRest_CheckName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            alertDialogRestExist();
                        } else {
                            uploadRestaurantData();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AddNewRestaurant.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void uploadRestaurantData() {

        if (validateRestDetails()) {

            progressDialog.setTitle("Uploading restaurant details!!");
            progressDialog.show();

            String rest_Id = databaseRefRest.push().getKey();
            Restaurants rest_Data = new Restaurants(rest_Name, rest_Address);

            assert rest_Id != null;
            databaseRefRest.child(rest_Id).setValue(rest_Data).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            LayoutInflater inflater = getLayoutInflater();
                            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                            TextView text = layout.findViewById(R.id.tvToast);
                            ImageView imageView = layout.findViewById(R.id.imgToast);
                            text.setText("Restaurant added successfully!!");
                            imageView.setImageResource(R.drawable.baseline_restaurant_24);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                            startActivity(new Intent(AddNewRestaurant.this, RestaurantImageAdminShowRest.class));
                            finish();

                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (Exception e) {
                                Toast.makeText(AddNewRestaurant.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        progressDialog.dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddNewRestaurant.this, e.getMessage(), Toast.LENGTH_SHORT).show());

        }
    }

    private Boolean validateRestDetails() {

        //validate Restaurant details
        boolean result = false;

        rest_Name = etRest_Name.getText().toString().trim();
        rest_Address = etRest_Address.getText().toString().trim();

        if (TextUtils.isEmpty(rest_Name)) {
            etRest_Name.setError("Enter the restaurant name");
            etRest_Name.requestFocus();
        } else if (TextUtils.isEmpty(rest_Address)) {
            etRest_Address.setError("Enter the restaurant address");
            etRest_Address.requestFocus();
        } else {
            result = true;
        }
        return result;
    }

    public void alertDialogRestExist() {

        final String etRest_alertCheckName = etRest_Name.getText().toString().trim();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Checking the restaurant name!!")
                .setMessage("The restaurant: " + etRest_alertCheckName + " already exists!!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}