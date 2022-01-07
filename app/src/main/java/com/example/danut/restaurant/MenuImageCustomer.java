package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
    private ValueEventListener eventListenerMenu;

    private TextView tVRestNameCustomer, tVMenusAvCustomer;

    private String restaurantName ="";
    private String restaurantKey = "";

    private RecyclerView recyclerView;
    private MenuAdapterCustomer menuAdapterCustomer;

    private List<Menus> menusList;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_image_customer);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        //Retrieve data from Menus database
        databaseRefMenu = FirebaseDatabase.getInstance().getReference("Menus");

        menusList = new ArrayList<>();

        tVRestNameCustomer = (TextView) findViewById(R.id.tvRestNameCustomer);
        tVMenusAvCustomer = findViewById(R.id.tvMenusAvCustomer);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            restaurantName = bundle.getString("RName");
            restaurantKey = bundle.getString("RKey");
        }

        tVRestNameCustomer.setText(restaurantName + " Restaurant ");
        tVMenusAvCustomer.setText("No Menus available");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuAdapterCustomer = new MenuAdapterCustomer(MenuImageCustomer.this, menusList);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMenuDataCustomer();
    }

    private void loadMenuDataCustomer(){

        eventListenerMenu = databaseRefMenu.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menusList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Menus menus = postSnapshot.getValue(Menus.class);
                    if(menus != null) {
                        if( menus.getRestaurant_Key().equals(restaurantKey)) {
                            menus.setMenu_Key(postSnapshot.getKey());
                            menusList.add(menus);
                            tVMenusAvCustomer.setText(menusList.size()+" Menus Available");
                        }
                    }
                }

                menuAdapterCustomer.notifyDataSetChanged();
                recyclerView.setAdapter(menuAdapterCustomer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuImageCustomer.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}
