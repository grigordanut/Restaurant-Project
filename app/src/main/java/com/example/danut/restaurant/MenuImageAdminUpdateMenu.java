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
import android.widget.ArrayAdapter;
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

        //Retrieve data from Menus database
        databaseRefMenu = FirebaseDatabase.getInstance().getReference("Menus");

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

        eventListenerMenu = databaseRefMenu.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    menusList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        Menus menu_Data = postSnapshot.getValue(Menus.class);
                        assert menu_Data != null;

                        if (menu_Data.getRestaurant_Key().equals(restaurantKey)) {
                            menu_Data.setMenu_Key(postSnapshot.getKey());
                            menusList.add(menu_Data);
                            tVMenuImageUpdateMenuMenusAv.setText("Select your Menu");
                        }
                    }

                    menuAdapterAdmin.notifyDataSetChanged();
                }
                else {
                    tVMenuImageUpdateMenuMenusAv.setText("No added Menus were found.");
                }

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

        final String[] options = {"Update this Menu", "Back Admin Page"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, options);
        Menus selected_Menu = menusList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Selected menu: " + selected_Menu.getMenu_Name() + "\nSelect an option:")
                .setAdapter(adapter, (dialog, id) -> {

                    if (id == 0) {
                        Menus selected_MenuImg = menusList.get(position);
                        Intent intent = new Intent(MenuImageAdminUpdateMenu.this, UpdateMenu.class);
                        intent.putExtra("MName", selected_MenuImg.getMenu_Name());
                        intent.putExtra("MDesc", selected_MenuImg.getMenu_Description());
                        intent.putExtra("MPrice", String.valueOf(selected_MenuImg.getMenu_Price()));
                        intent.putExtra("MImage", selected_MenuImg.getMenu_Image());
                        intent.putExtra("MKey", selected_MenuImg.getMenu_Key());
                        startActivity(intent);
                    }

                    if (id == 1) {
                        Intent back_Admin = new Intent(MenuImageAdminUpdateMenu.this, AdminPage.class);
                        startActivity(back_Admin);
                    }
                })
                .setNegativeButton("CLOSE", (dialog, id) -> dialog.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
//    @Override
//    public void onItemClick(final int position) {
//
//        final String[] options = {"Update Menu picture", "Update Menu Details"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, options);
//        Menus selected_Menu = menusList.get(position);
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder
//                .setCancelable(false)
//                .setTitle("Selected menu: " + selected_Menu.getMenu_Name() + "\nSelect an option:")
//                .setAdapter(adapter, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        if (i == 0) {
//                            Menus selected_MenuImg = menusList.get(position);
//                            Intent update_Image = new Intent(MenuImageAdminUpdateMenu.this, UpdateMenuImage.class);
//                            update_Image.putExtra("MNameImg", selected_MenuImg.getMenu_Name());
//                            update_Image.putExtra("MDescImg", selected_MenuImg.getMenu_Description());
//                            update_Image.putExtra("MPriceImg", String.valueOf(selected_MenuImg.getMenu_Price()));
//                            update_Image.putExtra("MImageImg", selected_MenuImg.getMenu_Image());
//                            update_Image.putExtra("MKeyImg", selected_MenuImg.getMenu_Key());
//                            startActivity(update_Image);
//                        }
//
//                        if (i == 1) {
//                            Menus selected_MenuDet = menusList.get(position);
//                            Intent update_Det = new Intent(MenuImageAdminUpdateMenu.this, UpdateMenuDetails.class);
//                            update_Det.putExtra("MNameDet", selected_MenuDet.getMenu_Name());
//                            update_Det.putExtra("MDescDet", selected_MenuDet.getMenu_Description());
//                            update_Det.putExtra("MPriceDet", String.valueOf(selected_MenuDet.getMenu_Price()));
//                            update_Det.putExtra("MImageDet", selected_MenuDet.getMenu_Image());
//                            update_Det.putExtra("MKeyDet", selected_MenuDet.getMenu_Key());
//                            startActivity(update_Det);
//                        }
//                    }
//                })
//                .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }
}