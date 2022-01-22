package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class AddNewMenu extends AppCompatActivity {

    //Declare Variables
    private ImageView menuImg;
    private static final int PICK_IMAGE = 100;

    private EditText menuName, menuDescription, menuPrice;
    private String menu_Name, menu_Description;
    private double menu_Price;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask menuUploadTask;

    private Uri imageUri = null;

    private ProgressDialog progressDialog;

    private String restName = "";
    private String restKey = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_menu);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN: Add Menus to Restaurant");

        progressDialog = new ProgressDialog(AddNewMenu.this);

        TextView tVMenuRestName = findViewById(R.id.tvMenuRestName);

        menuName = findViewById(R.id.etMenuName);
        menuDescription = findViewById(R.id.etMenuDescription);
        menuPrice = findViewById(R.id.etMenuPrice);
        menuImg = findViewById(R.id.menuImage);

        //Create a new menu into the Menu table
        storageReference = FirebaseStorage.getInstance().getReference("Menus");
        databaseReference = FirebaseDatabase.getInstance().getReference("Menus");

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            restName = bundle.getString("RName");
            restKey = bundle.getString("RKey");
        }

        tVMenuRestName.setText("Restaurant: " + restName);

        menuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        //Action button insert
        Button buttonAddMenu = findViewById(R.id.btnAddMenu);
        buttonAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuUploadTask != null && menuUploadTask.isInProgress()) {
                    Toast.makeText(AddNewMenu.this, "Upload menu in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadMenuData();
                }
            }
        });

        Button backAdminMenu = findViewById(R.id.btnBackAdminMenu);
        backAdminMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddNewMenu.this, AdminPage.class));
            }
        });
    }

    //Insert a picture
    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //gallery.setType("Image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            menuImg.setImageURI(imageUri);
            Toast.makeText(AddNewMenu.this, "Image uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //Upload a new menu into the menu table
    public void uploadMenuData() {
        progressDialog.dismiss();

        //Add menu into Menu's database
        if (validateMenuDetails()) {

            menu_Name = menuName.getText().toString().trim();
            menu_Description = menuDescription.getText().toString().trim();
            menu_Price = Double.parseDouble(menuPrice.getText().toString().trim());

            progressDialog.setTitle("The Menu is uploading");
            progressDialog.show();

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            menuUploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String menu_Id = databaseReference.push().getKey();
                                    Menus menus = new Menus(menu_Name, menu_Description, menu_Price, uri.toString(), restName, restKey);
                                    assert menu_Id != null;
                                    databaseReference.child(menu_Id).setValue(menus);

                                    menuName.setText("");
                                    menuDescription.setText("");
                                    menuPrice.setText("");
                                    menuImg.setImageResource(R.drawable.add_menus_picture);

                                    progressDialog.dismiss();

                                    Toast.makeText(AddNewMenu.this, "Menu successfully uploaded", Toast.LENGTH_LONG).show();
                                    Intent intentAdd = new Intent(AddNewMenu.this, MenuImageAdmin.class);
                                    intentAdd.putExtra("RName", menus.getRestaurant_Name());
                                    intentAdd.putExtra("RKey", menus.getRestaurant_Key());
                                    startActivity(intentAdd);
                                    finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddNewMenu.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            //show upload Progress
                            double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            progressDialog.setProgress((int) progress);
                        }
                    });

        }
    }

    private Boolean validateMenuDetails() {

        boolean result = false;

        final String menu_NameValidation = menuName.getText().toString().trim();
        final String menu_DescriptionValidation = menuDescription.getText().toString().trim();
        final String menu_PriceValidation = menuPrice.getText().toString().trim();

        if (imageUri == null) {
            alertDialogMenuPicture();
        } else if (TextUtils.isEmpty(menu_NameValidation)) {
            menuName.setError("Enter the menu name");
        } else if (TextUtils.isEmpty(menu_DescriptionValidation)) {
            menuDescription.setError("Enter the menu description");
        } else if (TextUtils.isEmpty(menu_PriceValidation)) {
            menuPrice.setError("Enter the menu price");
        } else {
            result = true;
        }
        return result;
    }

    public void alertDialogMenuPicture() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Add menu picture.")
                .setMessage("Please add a picture!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
