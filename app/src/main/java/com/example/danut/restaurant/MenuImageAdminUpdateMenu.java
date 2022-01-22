package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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
import java.util.Objects;

public class MenuImageAdminUpdateMenu extends AppCompatActivity implements MenuAdapterAdmin.OnItemClickListener {

    //Declare variables
    private DatabaseReference databaseRefMenu;
    private ValueEventListener eventListenerMenu;

    private String restaurantName = "";
    private String restaurantKey = "";

    private RecyclerView recyclerView;
    private MenuAdapterAdmin menuAdapterAdmin;

    private List<Menus> menusList;

    private TextView tVMenuImageUpdateMenuRestName, tVMenuImageUpdateMenuMenusAv;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_image_admin_update_menu);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN: Menus available to update");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        tVMenuImageUpdateMenuRestName = findViewById(R.id.tvMenuImageUpdateMenuRestName);
        tVMenuImageUpdateMenuMenusAv = findViewById(R.id.tvMenuImageUpdateMenuMenusAv);

        menusList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            restaurantName = bundle.getString("RName");
            restaurantKey = bundle.getString("RKey");
        }

        tVMenuImageUpdateMenuRestName.setText("Restaurant: " + restaurantName);
        tVMenuImageUpdateMenuMenusAv.setText("No Menus available");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuAdapterAdmin = new MenuAdapterAdmin(MenuImageAdminUpdateMenu.this, menusList);
        recyclerView.setAdapter(menuAdapterAdmin);
        menuAdapterAdmin.setOnItmClickListener(MenuImageAdminUpdateMenu.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMenuDataUpdateMenus();
    }

    private void loadMenuDataUpdateMenus() {

        //Retrieve data from Menus database
        databaseRefMenu = FirebaseDatabase.getInstance().getReference("Menus");

        eventListenerMenu = databaseRefMenu.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menusList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Menus menus = postSnapshot.getValue(Menus.class);
                    if (menus != null) {
                        if (menus.getRestaurant_Key().equals(restaurantKey)) {
                            menus.setMenu_Key(postSnapshot.getKey());
                            menusList.add(menus);
                            tVMenuImageUpdateMenuMenusAv.setText("Select your Menu");
                        }
                    }
                }

                menuAdapterAdmin.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuImageAdminUpdateMenu.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on Menus onClick
    @Override
    public void onItemClick(final int position) {
        Menus selected_Menu = menusList.get(position);

        Intent update_Menu = new Intent(MenuImageAdminUpdateMenu.this, UpdateMenu.class);
        update_Menu.putExtra("MName", selected_Menu.getMenu_Name());
        update_Menu.putExtra("MDesc", selected_Menu.getMenu_Description());
        update_Menu.putExtra("MPrice", String.valueOf(selected_Menu.getMenu_Price()));
        update_Menu.putExtra("MImage", selected_Menu.getMenu_Image());
        update_Menu.putExtra("MKey", selected_Menu.getMenu_Key());
        startActivity(update_Menu);
    }
}