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
import java.util.Objects;

public class MenuImageAdminFullList extends AppCompatActivity implements MenuAdapterAdminFullList.OnItemClickListener {

    //Declare variables
    private FirebaseStorage menuStorage;
    private DatabaseReference databaseRefMenu;
    private ValueEventListener menuEventListener;

    private TextView tVMenusAvFullListAdmin;

    private RecyclerView rVFullList;
    private MenuAdapterAdminFullList menuAdapterAdminFullList;

    private List<Menus> menusList;

    private String restaurantName;
    private String restaurantKey;

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_image_admin_full_list);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN: All Menus available");

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

        menuAdapterAdminFullList = new MenuAdapterAdminFullList(MenuImageAdminFullList.this, menusList);
        rVFullList.setAdapter(menuAdapterAdminFullList);
        menuAdapterAdminFullList.setOnItmClickListener(MenuImageAdminFullList.this);

        //Action button new Menu
        Button buttonNewMenuFullList = findViewById(R.id.btnNewMenuFullList);
        buttonNewMenuFullList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuImageAdminFullList.this, AddNewMenu.class);
                i.putExtra("RName", restaurantName);
                i.putExtra("RKey", restaurantKey);
                startActivity(i);
            }
        });

        //Action button back to Admin page
        Button buttonBackAdminFullList = findViewById(R.id.btnBackAdminFullList);
        buttonBackAdminFullList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuImageAdminFullList.this, AdminPage.class));
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
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Menus menus = postSnapshot.getValue(Menus.class);
                    if (menus != null) {
                        menus.setMenu_Key(postSnapshot.getKey());
                        menusList.add(menus);
                        tVMenusAvFullListAdmin.setText(menusList.size() + " Menus available");
                        restaurantName = menus.getRestaurant_Name();
                        restaurantKey = menus.getRestaurant_Key();
                    }
                }

                menuAdapterAdminFullList.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuImageAdminFullList.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on Menus onClick
    @Override
    public void onItemClick(final int position) {
        showOptionMenu(position);
    }

    public void showOptionMenu(final int position) {

        Menus selected_Menu = menusList.get(position);

        final String[] options = {"Update this Menu", "Delete this Menu"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
        Intent intent = new Intent(MenuImageAdminFullList.this, UpdateMenu.class);
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

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MenuImageAdminFullList.this);
        alertDialogBuilder
                .setTitle("Delete menu from restaurant.")
                .setMessage("Are sure to delete the menu: " + selected_Menu.getMenu_Name() + "?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final String selectedMenuKey = selected_Menu.getMenu_Key();
                                StorageReference imageReference = menuStorage.getReferenceFromUrl(selected_Menu.getMenu_Image());
                                imageReference.delete().addOnSuccessListener(aVoid -> {
                                    databaseRefMenu.child(selectedMenuKey).removeValue();
                                    Toast.makeText(MenuImageAdminFullList.this, "The Menu has been successfully deleted!", Toast.LENGTH_SHORT).show();
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
        tVMenusAvFullListAdmin.setText(menusList.size() + " Menus available");
    }
}