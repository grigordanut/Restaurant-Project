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

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_image_admin);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN: Menus available");

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

    public void loadMenusAdmin() {
        menuEventListener = databaseRefMenu.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menusList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Menus menus = postSnapshot.getValue(Menus.class);
                    assert menus != null;
                    menus.setMenu_Key(postSnapshot.getKey());
                    String rest_Key = menus.getRestaurant_Key();

                    if (rest_Key.equals(restaurantKey)) {
                        menusList.add(menus);
                        if (menusList.size() == 1) {
                            tVMenusAvAdmin.setText(menusList.size() + " menu available");
                        } else {
                            tVMenusAvAdmin.setText(menusList.size() + " menus available");
                        }
                    }
                }

                if (menusList.size() == 1) {
                    tVMenusAvAdmin.setText(menusList.size() + " menu available");
                } else {
                    tVMenusAvAdmin.setText(menusList.size() + " menus available");
                }
                menuAdapterAdmin.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuImageAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on menus onClick
    @Override
    public void onItemClick(int position) {

        final String[] options = {"Update this Menu", "Delete this Menu"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, options);
        Menus selected_Menu = menusList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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

    @SuppressLint("SetTextI18n")
    public void confirmDeletion(final int position) {

        Menus selected_Menu = menusList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MenuImageAdmin.this);
        alertDialogBuilder
                .setTitle("Delete menu from restaurant!!")
                .setMessage("Are you sure to delete the menu:\n" + selected_Menu.getMenu_Name() + "?")
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, id) -> {

                    String selectedMenuKey = selected_Menu.getMenu_Key();

                    StorageReference imageReference = menuStorage.getReferenceFromUrl(selected_Menu.getMenu_Image());
                    imageReference.delete().addOnSuccessListener(aVoid -> {
                        databaseRefMenu.child(selectedMenuKey).removeValue();

                        LayoutInflater inflater = getLayoutInflater();
                        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                        TextView text = layout.findViewById(R.id.tvToast);
                        ImageView imageView = layout.findViewById(R.id.imgToast);
                        text.setText("The menu was successfully deleted!!");
                        imageView.setImageResource(R.drawable.baseline_delete_forever_24);
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
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

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_admin, menu);
        return true;
    }

    public void menuImgAdminGoBack() {
        startActivity(new Intent(MenuImageAdmin.this, AdminPage.class));
        finish();
    }

    public void menuImgAdminAddMenu() {
        startActivity(new Intent(MenuImageAdmin.this, RestaurantImageAdminAddMenu.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menuImgAdmin_goBack) {
            menuImgAdminGoBack();
        }

        if (item.getItemId() == R.id.menuImgAdmin_addMenu) {
            menuImgAdminAddMenu();
        }

        return super.onOptionsItemSelected(item);
    }
}
