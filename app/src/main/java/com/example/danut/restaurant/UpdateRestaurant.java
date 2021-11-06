package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UpdateRestaurant extends AppCompatActivity {

    //Save updated Restaurant data to database
    private DatabaseReference databaseRefRestUpdate;
    private TextView tVRestUpdate;

    private EditText etRest_NameUp, etRest_AddressUp;

    private String rest_NameUp, rest_AddressUp;

    private ProgressDialog progressDialog;

    private String restNameUpdate = "";
    private String restAddressUpdate = "";
    private String restKeyUpdate = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_restaurant);

        progressDialog = new ProgressDialog(this);

        tVRestUpdate = findViewById(R.id.tvRestUpdate);

        etRest_NameUp = findViewById(R.id.etRestNameUpdate);
        etRest_AddressUp = findViewById(R.id.etRestAddressUpdate);

        Bundle bundleStore = getIntent().getExtras();
        if (bundleStore != null) {
            restNameUpdate = bundleStore.getString("RName");
            restAddressUpdate = bundleStore.getString("RAddress");
            restKeyUpdate = bundleStore.getString("RKey");
        }

        tVRestUpdate.setText("Update " + restNameUpdate + " Restaurant details");

        etRest_NameUp.setText(restNameUpdate);
        etRest_AddressUp.setText(restAddressUpdate);

        Button btnSaveRestUpdates = findViewById(R.id.btnSaveRestUpdate);
        btnSaveRestUpdates.setOnClickListener(v -> updateRestaurantDetails());
    }

    public void updateRestaurantDetails() {

        if (validateBikeStoreDetailsUp()) {

            rest_NameUp = etRest_NameUp.getText().toString().trim();
            rest_AddressUp = etRest_AddressUp.getText().toString().trim();

            progressDialog.setMessage("Update the Restaurant");
            progressDialog.show();

            databaseRefRestUpdate = FirebaseDatabase.getInstance().getReference("Restaurants");
            Query queryStore = databaseRefRestUpdate.orderByChild("rest_Key").equalTo(restKeyUpdate);

            queryStore.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getRef().child("rest_Name").setValue(rest_NameUp);
                        ds.getRef().child("rest_Address").setValue(rest_AddressUp);
                    }
                    progressDialog.dismiss();
                    Toast.makeText(UpdateRestaurant.this, "Restaurant Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateRestaurant.this, AdminPage.class));
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UpdateRestaurant.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Boolean validateBikeStoreDetailsUp() {
        boolean result = false;

        rest_NameUp = etRest_NameUp.getText().toString().trim();
        rest_AddressUp = etRest_AddressUp.getText().toString().trim();

        if (TextUtils.isEmpty(rest_NameUp)) {
            etRest_NameUp.setError("Enter Restaurant name");
            etRest_NameUp.requestFocus();
        } else if (TextUtils.isEmpty(rest_AddressUp)) {
            etRest_AddressUp.setError("Enter Restaurant Address");
            etRest_AddressUp.requestFocus();
        } else {
            result = true;
        }
        return result;
    }
}