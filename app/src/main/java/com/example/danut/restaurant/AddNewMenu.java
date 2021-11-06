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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddNewMenu extends AppCompatActivity {

    //Declare Variables
    private ImageView imageView;
    private static final int PICK_IMAGE = 100;

    private EditText eTextMenuName, eTextMenuDescription, eTextMenuPrice;
    private String et_MenuName, et_MenuDescription;
    private double et_MenuPrice;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask menuUploadTask;

    private Uri imageUri = null;

    private ProgressDialog progressDialog;

    private String restName = "";
    private String restKey = "";
    private String menuKey = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_menu);

        getIntent().hasExtra("RName");
        restName = Objects.requireNonNull(getIntent().getExtras()).getString("RName");

        getIntent().hasExtra("RKey");
        restKey = Objects.requireNonNull(getIntent().getExtras()).getString("RKey");

        TextView tVMenuRestMName = findViewById(R.id.tvMenuRestName);
        tVMenuRestMName.setText("Add Menus to " + restName + " Restaurant");

        eTextMenuName = findViewById(R.id.etMenuName);
        eTextMenuDescription = findViewById(R.id.etMenuDescription);
        eTextMenuPrice = findViewById(R.id.etMenuPrice);

        progressDialog = new ProgressDialog(AddNewMenu.this);

        imageView = findViewById(R.id.menuImage);
        imageView.setOnClickListener(new View.OnClickListener() {
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
                if (menuUploadTask != null && menuUploadTask.isInProgress()){
                    Toast.makeText(AddNewMenu.this, "Upload menu in progress", Toast.LENGTH_SHORT).show();
                }
                else{
                    uploadMenu();
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
            imageView.setImageURI(imageUri);
            //Picasso.with(this).load(imageView).into(imageView);
            Toast.makeText(AddNewMenu.this, "Image uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //Upload a new menu into the menu table
    public void uploadMenu() {
        progressDialog.dismiss();

        //Add menu into Menu's database
        if (validateMenuDetails()) {

            et_MenuName = eTextMenuName.getText().toString().trim();
            et_MenuDescription = eTextMenuDescription.getText().toString().trim();
            et_MenuPrice = Double.parseDouble(eTextMenuPrice.getText().toString().trim());

            //Create a new menu into the menu table
            storageReference = FirebaseStorage.getInstance().getReference("Menus");
            databaseReference = FirebaseDatabase.getInstance().getReference("Menus");

            progressDialog.setTitle("The menu is uploading");
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
                                    menuKey = menu_Id;
                                    Menus menus = new Menus(et_MenuName, et_MenuDescription, et_MenuPrice, uri.toString(),restName, restKey, menuKey);
                                    assert menu_Id != null;
                                    databaseReference.child(menu_Id).setValue(menus);

                                    eTextMenuName.setText("");
                                    eTextMenuDescription.setText("");
                                    eTextMenuPrice.setText("");
                                    imageView.setImageResource(R.drawable.add_menus_picture);

                                    //startActivity(new Intent(AddNewMenu.this, AdminPage.class));

                                    Intent intentAdd = new Intent(AddNewMenu.this,MenuImageAdmin.class);
                                    intentAdd.putExtra("RName",menus.getRestaurant_Name());
                                    intentAdd.putExtra("RKey",menus.getRestaurant_Key());
                                    startActivity(intentAdd);
                                    Toast.makeText(AddNewMenu.this, "Menu successfully uploaded", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                            progressDialog.dismiss();
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

        final String etMenu_NameValidation = eTextMenuName.getText().toString().trim();
        final String etMenu_DescriptionValidation = eTextMenuDescription.getText().toString().trim();
        final String etMenu_PriceValidation = eTextMenuPrice.getText().toString().trim();

        if (imageUri == null) {
            alertDialogMenuPicture();
        } else if (TextUtils.isEmpty(etMenu_NameValidation)) {
            eTextMenuName.setError("Enter the menu name");
        } else if (TextUtils.isEmpty(etMenu_DescriptionValidation)) {
            eTextMenuDescription.setError("Enter the menu description");
        } else if (TextUtils.isEmpty(etMenu_PriceValidation)) {
            eTextMenuPrice.setError("Enter the menu price");
        } else {
            result = true;
        }
        return result;
    }

    public void alertDialogMenuPicture() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Please add a picture");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
