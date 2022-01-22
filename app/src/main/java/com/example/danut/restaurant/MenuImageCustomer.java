package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuImageCustomer extends AppCompatActivity implements MenuAdapterCustomer.OnItemClickListener{

    //Declare variables
    private DatabaseReference databaseRefMenu;
    private ValueEventListener eventListenerMenu;

    private TextView tVRestNameCustomer, tVMenusAvCustomer;

    private RecyclerView recyclerView;
    private MenuAdapterCustomer menuAdapterCustomer;

    private List<Menus> menusList;

    private String restaurantName = "";
    private String restaurantKey = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_image_customer);

        Objects.requireNonNull(getSupportActionBar()).setTitle("CUSTOMER: Menus available");

        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        menusList = new ArrayList<>();

        tVRestNameCustomer = findViewById(R.id.tvRestNameCustomer);
        tVMenusAvCustomer = findViewById(R.id.tvMenusAvCustomer);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            restaurantName = bundle.getString("RName");
            restaurantKey = bundle.getString("RKey");
        }

        tVRestNameCustomer.setText(restaurantName + " Restaurant ");
        tVMenusAvCustomer.setText("No Menus available");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuAdapterCustomer = new MenuAdapterCustomer(MenuImageCustomer.this, menusList);
        recyclerView.setAdapter(menuAdapterCustomer);
        menuAdapterCustomer.setOnItmClickListener(MenuImageCustomer.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMenuDataCustomer();
    }

    private void loadMenuDataCustomer(){

        //Retrieve data from Menus database
        databaseRefMenu = FirebaseDatabase.getInstance().getReference("Menus");

        eventListenerMenu = databaseRefMenu.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menusList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Menus menu_Data = postSnapshot.getValue(Menus.class);
                    if(menu_Data != null) {
                        if( menu_Data.getRestaurant_Key().equals(restaurantKey)) {
                            menu_Data.setMenu_Key(postSnapshot.getKey());
                            menusList.add(menu_Data);
                            tVMenusAvCustomer.setText(menusList.size()+" Menus available");
                        }
                    }
                }

                menuAdapterCustomer.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuImageCustomer.this, databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action on Menu onClick
    @Override
    public void onItemClick(int position) {

        Menus selected_Menu = menusList.get(position);

        Context context = MenuImageCustomer.this;
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.image_menu_customer_full, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        final ImageView img_full = (ImageView) promptsView.findViewById(R.id.imgFullCustomer);

        Picasso.get()
                .load(selected_Menu.getMenu_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(img_full);

        // set dialog message
        alertDialogBuilder
                .setTitle("Menu Name: " + selected_Menu.getMenu_Name())
                .setView(promptsView)
                .setCancelable(false)
                .setNegativeButton("CLOSE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
