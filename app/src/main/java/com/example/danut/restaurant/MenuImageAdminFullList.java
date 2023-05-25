package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
                if (dataSnapshot.exists()) {
                    menusList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Menus menus = postSnapshot.getValue(Menus.class);
                        assert menus != null;
                        menus.setMenu_Key(postSnapshot.getKey());
                        menusList.add(menus);
                        tVMenusAvFullListAdmin.setText(menusList.size() + " Menus available");
                        restaurantName = menus.getRestaurant_Name();
                        restaurantKey = menus.getRestaurant_Key();
                    }

                    menuAdapterAdminFullList.notifyDataSetChanged();
                }
                else {
                    tVMenusAvFullListAdmin.setText("No added Menus were found.");
                }

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

        final String[] options = {"Update this Menu", "Delete this Menu"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        Menus selected_Menu = menusList.get(position);

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Selected Menu: " + selected_Menu.getMenu_Name() + "\nSelect an option:")
                .setCancelable(false)
                .setAdapter(adapter, (dialog, id) -> {

                    if (id == 0) {
                        //chooseUpdate(position);
                        Intent intent = new Intent(MenuImageAdminFullList.this, UpdateMenu.class);
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

//    private void chooseUpdate(int position) {
//
//        final String[] options = {"Update Menu image", "Update Menu Details"};
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, options);
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder
//                .setCancelable(false)
//                .setTitle("Select an option:")
//                .setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        if (i == 0) {
//                            Intent intent_Image = new Intent(MenuImageAdminFullList.this, UpdateMenuImage.class);
//                            Menus selected_MenuImg = menusList.get(position);
//                            intent_Image.putExtra("MNameImg", selected_MenuImg.getMenu_Name());
//                            intent_Image.putExtra("MDescImg", selected_MenuImg.getMenu_Description());
//                            intent_Image.putExtra("MPriceImg", String.valueOf(selected_MenuImg.getMenu_Price()));
//                            intent_Image.putExtra("MImageImg", selected_MenuImg.getMenu_Image());
//                            intent_Image.putExtra("MKeyImg", selected_MenuImg.getMenu_Key());
//                            startActivity(intent_Image);
//                        }
//
//                        if (i == 1) {
//                            Intent intent_Det = new Intent(MenuImageAdminFullList.this, UpdateMenuDetails.class);
//                            Menus selected_MenuDet = menusList.get(position);
//                            intent_Det.putExtra("MNameDet", selected_MenuDet.getMenu_Name());
//                            intent_Det.putExtra("MDescDet", selected_MenuDet.getMenu_Description());
//                            intent_Det.putExtra("MPriceDet", String.valueOf(selected_MenuDet.getMenu_Price()));
//                            intent_Det.putExtra("MImageDet", selected_MenuDet.getMenu_Image());
//                            intent_Det.putExtra("MKeyDet", selected_MenuDet.getMenu_Key());
//                            startActivity(intent_Det);
//                        }
//                    }
//                })
//                .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }

    public void confirmDeletion(final int position) {

        Menus selected_Menu = menusList.get(position);

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MenuImageAdminFullList.this);
        alertDialogBuilder
                .setTitle("Delete menu from restaurant.")
                .setMessage("Are you sure to delete the menu: " + selected_Menu.getMenu_Name() + "?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                            final String selectedMenuKey = selected_Menu.getMenu_Key();
                            StorageReference imageReference = menuStorage.getReferenceFromUrl(selected_Menu.getMenu_Image());
                            imageReference.delete().addOnSuccessListener(aVoid -> {
                                databaseRefMenu.child(selectedMenuKey).removeValue();
                                Toast.makeText(MenuImageAdminFullList.this, "The Menu has been successfully deleted!", Toast.LENGTH_SHORT).show();
                            });
                        })

                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

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