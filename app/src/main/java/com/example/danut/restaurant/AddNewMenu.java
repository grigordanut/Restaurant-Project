package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.Objects;

public class AddNewMenu extends AppCompatActivity {

    //Declare Variables
    private static final int PICK_IMAGE = 100;

    private EditText menuName, menuDescription, menuPrice;
    private TextView tVMenuRestName;

    private String menu_Name, menu_Description;
    private double menu_Price;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask menuUploadTask;

    private ImageView menuImg;
    private Uri imageUri = null;

    private ProgressDialog progressDialog;

    private String restName = "";
    private String restKey = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_menu);

        Objects.requireNonNull(getSupportActionBar()).setTitle("ADMIN: Add menus to restaurant");

        progressDialog = new ProgressDialog(this);

        //Create a Menu table into the database
        storageReference = FirebaseStorage.getInstance().getReference("Menus");
        databaseReference = FirebaseDatabase.getInstance().getReference("Menus");

        tVMenuRestName = findViewById(R.id.tvMenuRestName);

        menuName = findViewById(R.id.etMenuName);
        menuDescription = findViewById(R.id.etMenuDescription);
        menuPrice = findViewById(R.id.etMenuPrice);
        menuImg = findViewById(R.id.menuImage);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            restName = bundle.getString("RName");
            restKey = bundle.getString("RKey");
        }

        tVMenuRestName.setText("Restaurant: " + restName);

        menuImg.setOnClickListener(view -> openGallery());

        Button btn_AddMenu = findViewById(R.id.btnAddMenu);
        btn_AddMenu.setOnClickListener(view -> {
            if (menuUploadTask != null && menuUploadTask.isInProgress()) {
                Toast.makeText(AddNewMenu.this, "Upload menu in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadMenuData();
            }
        });
    }

    //Insert a picture
    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //gallery.setType("Image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            menuImg.setImageURI(imageUri);

            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
            TextView text = layout.findViewById(R.id.tvToast);
            ImageView imageView = layout.findViewById(R.id.imgToast);
            text.setText("Image picked from Gallery!!");
            imageView.setImageResource(R.drawable.baseline_camera_24);
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //Upload a new menu into the Menu table
    @SuppressLint("SetTextI18n")
    public void uploadMenuData() {

        //Add menu into Menu's database
        if (validateMenuDetails()) {

            menu_Name = menuName.getText().toString().trim();
            menu_Description = menuDescription.getText().toString().trim();
            menu_Price = Double.parseDouble(menuPrice.getText().toString().trim());

            progressDialog.setTitle("Uploading menu details!!");
            progressDialog.show();

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            menuUploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {

                                Handler handler = new Handler();
                                handler.postDelayed(() -> progressDialog.setProgress(0), 2000);

                                Menus menus = new Menus(menu_Name, menu_Description, menu_Price, uri.toString(), restName, restKey);
                                String menu_Id = databaseReference.push().getKey();

                                assert menu_Id != null;
                                databaseReference.child(menu_Id).setValue(menus);

                                LayoutInflater inflater = getLayoutInflater();
                                @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                                TextView text = layout.findViewById(R.id.tvToast);
                                ImageView imageView = layout.findViewById(R.id.imgToast);
                                text.setText("The menu was successfully uploaded!!");
                                imageView.setImageResource(R.drawable.baseline_restaurant_menu_24);
                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();

                                startActivity(new Intent(AddNewMenu.this, AdminPage.class));
                                finish();

                            }))

                    .addOnFailureListener(e -> Toast.makeText(AddNewMenu.this, e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnProgressListener(taskSnapshot -> {
                        //show upload Progress
                        double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        progressDialog.setProgress((int) progress);
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddNewMenu.this);
        alertDialogBuilder
                .setTitle("No menu picture!!")
                .setMessage("Please add a menu picture.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
