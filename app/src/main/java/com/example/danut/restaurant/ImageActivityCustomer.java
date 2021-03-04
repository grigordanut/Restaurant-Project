package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImageActivityCustomer extends AppCompatActivity {

    //Declare variables
    private DatabaseReference databaseRefMenu;
    String restaurantID = "";

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    private List<Menus> mUploads;

    private TextView tVRestNameCustomer, tVRestMenusCustomer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_customer);

        getIntent().hasExtra("RESID");
        restaurantID = getIntent().getExtras().getString("RESID");

        tVRestNameCustomer = (TextView) findViewById(R.id.tvRestNameCustomer);
        tVRestNameCustomer.setText(restaurantID + " Restaurant ");

        tVRestMenusCustomer = findViewById(R.id.tvRestMenusCustomer);

        mUploads = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();

        //add menu into the menu list
        if(databaseRefMenu == null){
            databaseRefMenu = FirebaseDatabase.getInstance().getReference().child("Menus");
        }
        databaseRefMenu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Menus menus = postSnapshot.getValue(Menus.class);
                    if(menus != null) {
                        if( menus.getRestaurantName().equals(restaurantID)) {
                            mUploads.add(menus);
                            tVRestMenusCustomer.setText(mUploads.size()+" Menus Available");
                        }
                    }
                }

                imageAdapter = new ImageAdapter(ImageActivityCustomer.this, mUploads);
                recyclerView.setAdapter(imageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImageActivityCustomer.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
