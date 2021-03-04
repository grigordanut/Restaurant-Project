package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
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

public class ImageActivityAdmin extends AppCompatActivity implements ImageAdapter.OnItemClickListener{

    //Declare variables
    private FirebaseAuth firebaseAuth;

    private FirebaseStorage menuStorage;
    private DatabaseReference databaseRefMenu;
    private ValueEventListener menuDBEventListener;

    String restaurantID = "";

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    private List<Menus> mUploads;

    private Button buttonNewMenu, buttonBackRestaurant;
    private TextView tVRestName, tVRestMenusAdmin;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_admin);

        //initialize variables
        firebaseAuth = FirebaseAuth.getInstance();

        getIntent().hasExtra("RESID");
        restaurantID = getIntent().getExtras().getString("RESID");

        //Set textview
        tVRestName = findViewById(R.id.tvRestNameAdmin);
        tVRestName.setText(restaurantID + " Restaurant");

        tVRestMenusAdmin = findViewById(R.id.tvRestMenusAdmin);

        mUploads = new ArrayList<>();

        //Action button back to restaurant
        buttonBackRestaurant = (Button)findViewById(R.id.btnBackRestaurant);
        buttonBackRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImageActivityAdmin.this, AddRestaurant.class));
            }
        });

        //Action button new Menus
        buttonNewMenu = (Button) findViewById(R.id.btnNewMenu);
        buttonNewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ImageActivityAdmin.this, AddNewMenu.class);
                i.putExtra("RESID",restaurantID);
                startActivity(i);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //check if the menu list is empty and add a new menu
        if(databaseRefMenu == null){
            menuStorage = FirebaseStorage.getInstance();
            databaseRefMenu = FirebaseDatabase.getInstance().getReference().child("Menus");
        }
        menuDBEventListener = databaseRefMenu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Menus menus = postSnapshot.getValue(Menus.class);
                    if(menus != null) {
                        if( menus.getRestaurantName().equals(restaurantID)) {
                            menus.setMenuKey(postSnapshot.getKey());
                            mUploads.add(menus);
                            tVRestMenusAdmin.setText(mUploads.size()+" Menus available");
                        }
                    }
                }

                imageAdapter = new ImageAdapter(ImageActivityAdmin.this, mUploads);
                recyclerView.setAdapter(imageAdapter);
                imageAdapter.setOnItmClickListener(ImageActivityAdmin.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImageActivityAdmin.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Press long click to show more action: ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateClick(int position) {
        Intent intent = new Intent(ImageActivityAdmin.this, LoginUser.class);
        startActivity(intent);
        Toast.makeText(this, "Update click at position: ",Toast.LENGTH_SHORT).show();
    }

    //Action of the menu Delete and alert dialog
    @Override
    public void onDeleteClick(final int position) {
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(ImageActivityAdmin.this);
        builderAlert.setMessage("Are sure to delete this item?");
        builderAlert.setCancelable(true);
        builderAlert.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Menus selectedMenus = mUploads.get(position);
                        final String selectedKey = selectedMenus.getMenuKey();
                        StorageReference imageReference = menuStorage.getReferenceFromUrl(selectedMenus.getItemImage());
                        imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseRefMenu.child(selectedKey).removeValue();
                                Toast.makeText(ImageActivityAdmin.this, "The item has been deleted successfully ",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        builderAlert.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builderAlert.create();
        alert11.show();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        databaseRefMenu.removeEventListener(menuDBEventListener);
    }
}
