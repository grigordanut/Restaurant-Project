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
import android.view.MenuItem;
import android.widget.ArrayAdapter;
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
                    }

                    menuAdapterAdminFullList.notifyDataSetChanged();
                } else {
                    tVMenusAvFullListAdmin.setText("There are not Menus added");
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuImageAdminFullList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on Menus onClick
    @Override
    public void onItemClick(final int position) {

        final String[] options = {"Update this Menu", "Delete this Menu"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, options);
        Menus selected_Menu = menusList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MenuImageAdminFullList.this);
        alertDialogBuilder
                .setTitle("Selected Menu: " + selected_Menu.getMenu_Name() + "\nSelect an option:")
                .setCancelable(false)
                .setAdapter(adapter, (dialog, id) -> {

                    if (id == 0) {
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

    public void confirmDeletion(int position) {

        Menus selected_Menu = menusList.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MenuImageAdminFullList.this);
        alertDialogBuilder
                .setTitle("Delete menu from Restaurant!!")
                .setMessage("Are you sure to delete the menu:\n" + selected_Menu.getMenu_Name() + "?")
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, id) -> {
                    final String selectedMenuKey = selected_Menu.getMenu_Key();
                    StorageReference imageReference = menuStorage.getReferenceFromUrl(selected_Menu.getMenu_Image());
                    imageReference.delete().addOnSuccessListener(aVoid -> {
                        databaseRefMenu.child(selectedMenuKey).removeValue();
                        Toast.makeText(MenuImageAdminFullList.this, "The Menu has been successfully deleted!", Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.menu_image_admin_full_list, menu);
        return true;
    }

    public void menuImgAdminFullListGoBack() {
        startActivity(new Intent(MenuImageAdminFullList.this, AdminPage.class));
        finish();
    }

    public void menuImgAdminFullListAddMenu() {
        startActivity(new Intent(MenuImageAdminFullList.this, RestaurantImageAdminAddMenu.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menuImgAdminFullList_goBack) {
            menuImgAdminFullListGoBack();
        }

        if (item.getItemId() == R.id.menuImgAdminFullList_addMenu) {
            menuImgAdminFullListAddMenu();
        }

        return super.onOptionsItemSelected(item);
    }
}