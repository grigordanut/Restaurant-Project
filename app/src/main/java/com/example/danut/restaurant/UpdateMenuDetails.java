package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UpdateMenuDetails extends AppCompatActivity {

    private DatabaseReference databaseRefUpDet;
    private StorageTask menuTaskUpDet;

    private ImageView ivMenuUpDet;

    private EditText etMenuName_UpDet, etMenuDescription_UpDet, etMenuPrice_UpDet;

    private TextView tVMenuUpDet;

    private String menuName_UpDet, menuDescription_UpDet;
    private double menuPrice_UpDet;

    private String menuNameUpDet = "";
    private String menuDescriptionUpDet = "";
    private String menuPriceUpDet = "";
    private String menuImageUpDet = "";
    private String menuKeyUpDet = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_menu_details);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Update Menu Details");

        databaseRefUpDet = FirebaseDatabase.getInstance().getReference("Menus");

        progressDialog = new ProgressDialog(UpdateMenuDetails.this);

        //initialise variables
        tVMenuUpDet = findViewById(R.id.tvMenuUpDet);

        etMenuName_UpDet = findViewById(R.id.etMenuNameUpDet);
        etMenuDescription_UpDet = findViewById(R.id.etMenuDescriptionUpDet);
        etMenuPrice_UpDet = findViewById(R.id.etMenuPriceUpDet);
        ivMenuUpDet = findViewById(R.id.imgViewMenuUpDet);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            menuNameUpDet = bundle.getString("MNameDet");
            menuDescriptionUpDet = bundle.getString("MDescDet");
            menuPriceUpDet = bundle.getString("MPriceDet");
            menuImageUpDet = bundle.getString("MImageDet");
            menuKeyUpDet = bundle.getString("MKeyDet");
        }

        //receive data from the other activity
        Picasso.get()
                .load(menuImageUpDet)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(ivMenuUpDet);

        tVMenuUpDet.setText("Menu: " + menuNameUpDet);

        etMenuName_UpDet.setText(menuNameUpDet);
        etMenuDescription_UpDet.setText(menuDescriptionUpDet);
        etMenuPrice_UpDet.setText(String.valueOf(menuPriceUpDet));

        ivMenuUpDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogMenuPicture();
            }
        });

        //Action button Save updated Menu
        Button btn_SaveMenuUpDet = findViewById(R.id.btnSaveMenuUpDet);
        btn_SaveMenuUpDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuTaskUpDet != null && menuTaskUpDet.isInProgress()) {
                    Toast.makeText(UpdateMenuDetails.this, "Update menu in progress", Toast.LENGTH_SHORT).show();
                } else {
                    updateMenuDetails();
                }
            }
        });

        Button btn_BacMenuUpDet = findViewById(R.id.btnBacMenuUpDet);
        btn_BacMenuUpDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateMenuDetails.this, AdminPage.class));
            }
        });
    }

    private void updateMenuDetails() {

        if (validateUpdateMenuDetails()) {

            //Add a new Menu into the Menu's table
            menuName_UpDet = etMenuName_UpDet.getText().toString().trim();
            menuDescription_UpDet = etMenuDescription_UpDet.getText().toString().trim();
            menuPrice_UpDet = Double.parseDouble(etMenuPrice_UpDet.getText().toString().trim());

            progressDialog.setMessage("The Menu is updating!!");
            progressDialog.show();

            databaseRefUpDet.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String menu_key = postSnapshot.getKey();
                        assert menu_key != null;

                        if (menu_key.equals(menuKeyUpDet)) {
                            postSnapshot.getRef().child("menu_Name").setValue(menuName_UpDet);
                            postSnapshot.getRef().child("menu_Description").setValue(menuDescription_UpDet);
                            postSnapshot.getRef().child("menu_Price").setValue(menuPrice_UpDet);
                        }
                    }

                    Toast.makeText(UpdateMenuDetails.this, "The Menu will be updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateMenuDetails.this, AdminPage.class));
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UpdateMenuDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public boolean validateUpdateMenuDetails() {

        boolean result = false;

        final String upMenu_NameVal = etMenuName_UpDet.getText().toString().trim();
        final String upMenu_DescriptionVal = etMenuDescription_UpDet.getText().toString().trim();
        final String upMenu_PriceVal = etMenuPrice_UpDet.getText().toString().trim();

        if (TextUtils.isEmpty(upMenu_NameVal)) {
            etMenuName_UpDet.setError("Please enter Menu name");
            etMenuName_UpDet.requestFocus();
        } else if (TextUtils.isEmpty(upMenu_DescriptionVal)) {
            etMenuDescription_UpDet.setError("Please enter Menu description");
            etMenuDescription_UpDet.requestFocus();
        } else if (TextUtils.isEmpty(upMenu_PriceVal)) {
            etMenuPrice_UpDet.setError("Please enter Menu price");
            etMenuPrice_UpDet.requestFocus();
        } else {
            result = true;
        }

        return result;
    }

    public void alertDialogMenuPicture() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Picture cannot be Changed!!.")
                .setMessage("The picture cannot be changed here.\nPlease choose the Change Menu Image to change the picture.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}