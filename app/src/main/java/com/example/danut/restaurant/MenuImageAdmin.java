package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuImageAdmin extends AppCompatActivity implements MenuAdapterAdmin.OnItemClickListener {

    //Declare variables
    private FirebaseStorage menuStorage;
    private DatabaseReference databaseRefMenu;
    private ValueEventListener menuEventListener;

    private TextView tVRestName, tVMenusAvAdmin;

    private RecyclerView recyclerView;
    private MenuAdapterAdmin menuAdapterAdmin;

    private List<Menus> menusList;

    private String restaurantName = "";
    private String restaurantKey = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_image_admin);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN: Menus available");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        menuStorage = FirebaseStorage.getInstance();
        databaseRefMenu = FirebaseDatabase.getInstance().getReference().child("Menus");

        menusList = new ArrayList<>();

        tVRestName = findViewById(R.id.tvRestNameAdmin);
        tVMenusAvAdmin = findViewById(R.id.tvMenusAvAdmin);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            restaurantName = bundle.getString("RName");
            restaurantKey = bundle.getString("RKey");
        }

        //Set textview
        tVRestName.setText("Restaurant: " + restaurantName);
        tVMenusAvAdmin.setText("No Menus available");

        //Action button new Menus
        Button buttonNewMenuAdmin = findViewById(R.id.btnNewMenuAdmin);
        buttonNewMenuAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuImageAdmin.this, AddNewMenu.class);
                i.putExtra("RName", restaurantName);
                i.putExtra("RKey", restaurantKey);
                startActivity(i);
            }
        });

        //Action button back to restaurant
        Button buttonBackAdmin = findViewById(R.id.btnBackAdmin);
        buttonBackAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuImageAdmin.this, AdminPage.class));
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuAdapterAdmin = new MenuAdapterAdmin(MenuImageAdmin.this, menusList);
        recyclerView.setAdapter(menuAdapterAdmin);
        menuAdapterAdmin.setOnItmClickListener(MenuImageAdmin.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMenusAdmin();
    }

    private void loadMenusAdmin() {
        menuEventListener = databaseRefMenu.addValueEventListener(new ValueEventListener() {
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
                            tVMenusAvAdmin.setText(menusList.size() + " Menus available");
                        }
                    }
                }

                menuAdapterAdmin.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuImageAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on menus onClick
    @Override
    public void onItemClick(final int position) {
        showOptionMenu(position);
    }

    public void showOptionMenu(final int position) {
        final String[] options = {"Update this Menu", "Delete this Menu"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        Menus selected_Menu = menusList.get(position);

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("You selected the menu: " + selected_Menu.getMenu_Name() + "\nSelect an option:")
                .setCancelable(false)
                .setAdapter(adapter, (dialog, id) -> {

                    if (id == 0) {
                        updateMenus(position);
                    }
                    if (id == 1) {
                        confirmDeletion(position);
                    }
                })

                .setNegativeButton("CLOSE", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void updateMenus(final int position) {
        Intent intent = new Intent(MenuImageAdmin.this, UpdateMenu.class);
        Menus selected_Menu = menusList.get(position);
        intent.putExtra("MName", selected_Menu.getMenu_Name());
        intent.putExtra("MDesc", selected_Menu.getMenu_Description());
        intent.putExtra("MPrice", String.valueOf(selected_Menu.getMenu_Price()));
        intent.putExtra("MImage", selected_Menu.getMenu_Image());
        intent.putExtra("MKey", selected_Menu.getMenu_Key());
        startActivity(intent);
    }

    public void confirmDeletion(final int position) {

        Menus selected_Menu = menusList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MenuImageAdmin.this);
        alertDialogBuilder
                .setTitle("Delete menu from restaurant.")
                .setMessage("Are sure to delete the menu: " + selected_Menu.getMenu_Name() + "?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {

                    String selectedMenuKey = selected_Menu.getMenu_Key();

                    StorageReference imageReference = menuStorage.getReferenceFromUrl(selected_Menu.getMenu_Image());
                    imageReference.delete().addOnSuccessListener(aVoid -> {
                        databaseRefMenu.child(selectedMenuKey).removeValue();
                        Toast.makeText(MenuImageAdmin.this, "The Menu has been successfully deleted!", Toast.LENGTH_SHORT).show();
                    });
                })

                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseRefMenu.removeEventListener(menuEventListener);
        tVMenusAvAdmin.setText(menusList.size() + " Menus available");
    }
}
