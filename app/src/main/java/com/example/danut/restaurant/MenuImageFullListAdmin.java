package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class MenuImageFullListAdmin extends AppCompatActivity implements MenuAdapterFullListAdmin.OnItemClickListener {

    //Declare variables
    private FirebaseStorage menuStorage;
    private DatabaseReference databaseRefMenu;
    private ValueEventListener menuEventListener;

    private TextView tVMenusAvFullListAdmin;

    private RecyclerView rVFullList;
    private MenuAdapterFullListAdmin menuAdapterFullListAdmin;

    private List<Menus> menusList;

    private String restaurantName;
    private String restaurantKey;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_image_full_list_admin);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        menuStorage = FirebaseStorage.getInstance();
        databaseRefMenu = FirebaseDatabase.getInstance().getReference("Menus");

        menusList = new ArrayList<>();

        tVMenusAvFullListAdmin = findViewById(R.id.tvMenusAvFullListAdmin);
        tVMenusAvFullListAdmin.setText("No Menus available");

        rVFullList = findViewById(R.id.rvFullList);
        rVFullList.setHasFixedSize(true);
        rVFullList.setLayoutManager(new LinearLayoutManager(this));

        menuAdapterFullListAdmin = new MenuAdapterFullListAdmin(MenuImageFullListAdmin.this,  menusList);
        rVFullList.setAdapter(menuAdapterFullListAdmin);
        menuAdapterFullListAdmin.setOnItmClickListener(MenuImageFullListAdmin.this);

        //Action button new Menu
        Button buttonNewMenuFullList = findViewById(R.id.btnNewMenuFullList);
        buttonNewMenuFullList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuImageFullListAdmin.this, AddNewMenu.class);
                i.putExtra("RName",restaurantName);
                i.putExtra("RKey",restaurantKey);
                startActivity(i);
            }
        });

        //Action button back to Admin page
        Button buttonBackAdminFullList = findViewById(R.id.btnBackAdminFullList);
        buttonBackAdminFullList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuImageFullListAdmin.this, AdminPage.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMenusFullListAdmin();
    }

    private void loadMenusFullListAdmin() {
        menuEventListener = databaseRefMenu.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menusList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Menus menus = postSnapshot.getValue(Menus.class);
                    if(menus != null) {
                        menus.setMenu_Key(postSnapshot.getKey());
                        menusList.add(menus);
                        tVMenusAvFullListAdmin.setText( menusList.size()+" Menus available");
                        restaurantName = menus.getRestaurant_Name();
                        restaurantKey = menus.getRestaurant_Key();
                    }
                }

                menuAdapterFullListAdmin.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuImageFullListAdmin.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
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
                .setTitle("You selected: " + selected_Menu.getMenu_Name() +" menu!"+ "\nSelect an option:")
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
        Intent intent = new Intent(MenuImageFullListAdmin.this, UpdateMenu.class);
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

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MenuImageFullListAdmin.this);
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
                                    Toast.makeText(MenuImageFullListAdmin.this, "The Menu has been deleted successfully ", Toast.LENGTH_SHORT).show();
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
        tVMenusAvFullListAdmin.setText(menusList.size()+" Menus available");
    }
}