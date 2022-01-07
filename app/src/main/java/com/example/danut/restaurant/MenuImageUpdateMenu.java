package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuImageUpdateMenu extends AppCompatActivity {

    //Declare variables
    private DatabaseReference databaseRefMenu;
    private ValueEventListener eventListenerMenu;

    private String restaurantName ="";
    private String restaurantKey = "";

    private RecyclerView recyclerView;
    private MenuAdapterUpdateMenu menuAdapterUpdateMenu;

    private List<Menus> menusList;

    private TextView tVMenuImageUpdateMenuRestName, tVMenuImageUpdateMenuMenusAv;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_image_update_menu);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        //Retrieve data from Menus database
        databaseRefMenu = FirebaseDatabase.getInstance().getReference().child("Menus");

        tVMenuImageUpdateMenuRestName = findViewById(R.id.tvMenuImageUpdateMenuRestName);
        tVMenuImageUpdateMenuMenusAv = findViewById(R.id.tvMenuImageUpdateMenuMenusAv);

        menusList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            restaurantName = bundle.getString("RName");
            restaurantKey = bundle.getString("RKey");
        }

        tVMenuImageUpdateMenuRestName.setText(restaurantName + " Restaurant ");
        tVMenuImageUpdateMenuMenusAv.setText("No Menus available");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuAdapterUpdateMenu = new MenuAdapterUpdateMenu(MenuImageUpdateMenu.this, menusList);
        recyclerView.setAdapter(menuAdapterUpdateMenu);


    }

    @Override
    public void onStart() {
        super.onStart();
        loadMenuDataUpdateMenus();
    }

    private void loadMenuDataUpdateMenus(){

        eventListenerMenu = databaseRefMenu.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menusList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Menus menus = postSnapshot.getValue(Menus.class);
                    if(menus != null) {
                        if( menus.getRestaurant_Key().equals(restaurantKey)) {
                            menus.setMenu_Key(postSnapshot.getKey());
                            menusList.add(menus);
                            tVMenuImageUpdateMenuMenusAv.setText(menusList.size()+" Menus Available");
                        }
                    }
                }

                menuAdapterUpdateMenu.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuImageUpdateMenu.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}