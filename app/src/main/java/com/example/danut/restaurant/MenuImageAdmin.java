package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        //Action button new Menus
        Button btn_NewMenuAdmin = findViewById(R.id.btnNewMenuAdmin);
        btn_NewMenuAdmin.setOnClickListener(v -> startActivity(new Intent(MenuImageAdmin.this, RestaurantImageAdminAddMenu.class)));

        //Action button back to restaurant
        Button btn_BackAdmin = findViewById(R.id.btnBackAdmin);
        btn_BackAdmin.setOnClickListener(v -> startActivity(new Intent(MenuImageAdmin.this, AdminPage.class)));

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
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Menus menus = postSnapshot.getValue(Menus.class);
                        assert menus != null;
                        menus.setMenu_Key(postSnapshot.getKey());
                        String rest_Key = menus.getRestaurant_Key();

                        if (rest_Key.equals(restaurantKey)) {
                            menusList.add(menus);
                            tVMenusAvAdmin.setText(menusList.size() + " Menus available");
                        }

                        if (menusList.size() == 0) {
                            tVMenusAvAdmin.setText("No Menus available");
                        }
                    }

                    menuAdapterAdmin.notifyDataSetChanged();
                }
                else {
                    tVMenusAvAdmin.setText("There are not Menus added");
                    alertDialogNoMenusAvailable();
                }

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

        final String[] options = {"Update this Menu", "Delete this Menu"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        Menus selected_Menu = menusList.get(position);

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Selected Menu: " + selected_Menu.getMenu_Name() + "\nSelect an option:")
                .setCancelable(false)
                .setAdapter(adapter, (dialog, id) -> {

                    if (id == 0) {
                        Intent intent = new Intent(MenuImageAdmin.this, UpdateMenu.class);
                        Menus selected_MenuImg = menusList.get(position);
                        intent.putExtra("MName", selected_MenuImg.getMenu_Name());
                        intent.putExtra("MDesc", selected_MenuImg.getMenu_Description());
                        intent.putExtra("MPrice", String.valueOf(selected_MenuImg.getMenu_Price()));
                        intent.putExtra("MImage", selected_MenuImg.getMenu_Image());
                        intent.putExtra("MKey", selected_MenuImg.getMenu_Key());
                        startActivity(intent);

                    }
                    if (id == 1) {
                        confirmDeletion(position);
                    }
                })

                .setNegativeButton("CLOSE", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void confirmDeletion(final int position) {

        Menus selected_Menu = menusList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MenuImageAdmin.this);
        alertDialogBuilder
                .setTitle("Delete menu from Restaurant!!")
                .setMessage("Are you sure to delete the menu:\n" + selected_Menu.getMenu_Name() + "?")
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, id) -> {

                    String selectedMenuKey = selected_Menu.getMenu_Key();

                    StorageReference imageReference = menuStorage.getReferenceFromUrl(selected_Menu.getMenu_Image());
                    imageReference.delete().addOnSuccessListener(aVoid -> {
                        databaseRefMenu.child(selectedMenuKey).removeValue();
                        menusList.clear();
                        Toast.makeText(MenuImageAdmin.this, "The Menu has been successfully deleted!", Toast.LENGTH_SHORT).show();
                    });
                })

                .setNegativeButton("NO", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseRefMenu.removeEventListener(menuEventListener);
    }

    public void alertDialogNoMenusAvailable() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("There are not Menus available!!")
                .setMessage("Would you like to add Menus?")
                .setPositiveButton("YES", (dialog, id) -> {
                    finish();
                    Intent intent = new Intent(MenuImageAdmin.this, RestaurantImageAdminAddMenu.class);
                    startActivity(intent);
                })

                .setNegativeButton("NO", (dialog, id) -> startActivity(new Intent(MenuImageAdmin.this, AdminPage.class)));

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
