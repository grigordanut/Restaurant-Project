package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MenuImageAdmin extends AppCompatActivity implements MenuImageAdapter.OnItemClickListener{

    //Declare variables
    private FirebaseStorage menuStorage;
    private DatabaseReference databaseRefMenu;
    private ValueEventListener menuEventListener;

    private RecyclerView recyclerView;
    private MenuImageAdapter menuImageAdapter;

    private List<Menus> menusList;

    private Button buttonNewMenu, buttonBackAdmin;
    private TextView tVRestName, tVMenusAvAdmin;

    private String restaurantName = "";
    private String restaurantKey = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_image_admin);

        getIntent().hasExtra("RName");
        restaurantName = getIntent().getExtras().getString("RName");

        getIntent().hasExtra("RKey");
        restaurantKey = getIntent().getExtras().getString("RKey");

        //Set textview
        tVRestName = findViewById(R.id.tvRestNameAdmin);
        tVRestName.setText(restaurantName + " Restaurant");

        tVMenusAvAdmin = findViewById(R.id.tvMenusAvAdmin);
        tVMenusAvAdmin.setText("No Menus available");

        menusList = new ArrayList<>();

        //Action button new Menus
        buttonNewMenu = (Button) findViewById(R.id.btnNewMenu);
        buttonNewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuImageAdmin.this, AddNewMenu.class);
                i.putExtra("RName",restaurantName);
                i.putExtra("RKey",restaurantKey);
                startActivity(i);
            }
        });

        //Action button back to restaurant
        buttonBackAdmin = (Button)findViewById(R.id.btnBackRestaurant);
        buttonBackAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuImageAdmin.this, AdminPage.class));
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //check if the menu list is empty and add a new menu
        if(databaseRefMenu == null){
            menuStorage = FirebaseStorage.getInstance();
            databaseRefMenu = FirebaseDatabase.getInstance().getReference().child("Menus");
        }
        menuEventListener = databaseRefMenu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menusList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Menus menus = postSnapshot.getValue(Menus.class);
                    if(menus != null) {
                        if( menus.getRestaurant_Key().equals(restaurantKey)) {
                            menus.setMenu_Key(postSnapshot.getKey());
                            menusList.add(menus);
                            tVMenusAvAdmin.setText( menusList.size()+" Menus available");
                        }
                    }
                }

                menuImageAdapter = new MenuImageAdapter(MenuImageAdmin.this,  menusList);
                recyclerView.setAdapter(menuImageAdapter);
                menuImageAdapter.setOnItmClickListener(MenuImageAdmin.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuImageAdmin.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
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
        Menus selected_Menu =  menusList.get(position);

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("You selected " + selected_Menu.getMenu_Name() +" menu!"+ "\nSelect an option:")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            updateMenus(position);
                        }
                        if (which == 1) {
                            confirmDeletion(position);
                        }
                    }
                })
                .setNegativeButton("CLOSE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void updateMenus(final int position) {
        Intent intent = new Intent(MenuImageAdmin.this, UpdateMenu.class);
        Menus selected_Menu = menusList.get(position);
        intent.putExtra("MName", selected_Menu.getMenu_Name());
        intent.putExtra("MDesc", selected_Menu.getMenu_Description());
        intent.putExtra("MPrice", selected_Menu.getMenu_Price());
        intent.putExtra("MImage", selected_Menu.getMenu_Image());
        intent.putExtra("MKey", selected_Menu.getMenu_Key());
        startActivity(intent);
    }

    public void confirmDeletion(final int position) {
        Menus selected_Menu = menusList.get(position);

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MenuImageAdmin.this);
        alertDialogBuilder
                .setMessage("Are sure to delete the " + selected_Menu.getMenu_Name() + " Menu?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final String selectedMenuKey = selected_Menu.getMenu_Key();
                                StorageReference imageReference = menuStorage.getReferenceFromUrl(selected_Menu.getMenu_Image());
                                imageReference.delete().addOnSuccessListener(aVoid -> {
                                    databaseRefMenu.child(selectedMenuKey).removeValue();
                                    Toast.makeText(MenuImageAdmin.this, "The Bike has been deleted successfully ", Toast.LENGTH_SHORT).show();
                                });
                            }
                        })

                .setNegativeButton("No",
                        (dialog, which) -> dialog.cancel());

        AlertDialog alert1 = alertDialogBuilder.create();
        alert1.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseRefMenu.removeEventListener(menuEventListener);
        tVMenusAvAdmin.setText(menusList.size()+" Menus available");
    }
}
