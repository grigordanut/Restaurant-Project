package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuImageCustomer extends AppCompatActivity {

    //Declare variables
    private DatabaseReference databaseRefMenu;
    String restaurantKey = "";
    String restaurantName ="";

    private RecyclerView recyclerView;
    private MenuAdapterCustomer menuAdapterCustomer;

    private List<Menus> mUploads;

    private TextView tVRestNameCustomer, tVMenusAvCustomer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_image_customer);

        getIntent().hasExtra("RKey");
        restaurantKey = getIntent().getExtras().getString("RKey");

        getIntent().hasExtra("RName");
        restaurantName = getIntent().getExtras().getString("RName");

        tVRestNameCustomer = (TextView) findViewById(R.id.tvRestNameCustomer);
        tVRestNameCustomer.setText(restaurantName + " Restaurant ");

        tVMenusAvCustomer = findViewById(R.id.tvMenusAvCustomer);
        tVMenusAvCustomer.setText("No Menus available");

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
                        if( menus.getRestaurant_Key().equals(restaurantKey)) {
                            mUploads.add(menus);
                            tVMenusAvCustomer.setText(mUploads.size()+" Menus Available");
                        }
                    }
                }

                menuAdapterCustomer= new MenuAdapterCustomer(MenuImageCustomer.this, mUploads);
                recyclerView.setAdapter(menuAdapterCustomer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuImageCustomer.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
