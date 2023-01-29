package com.example.danut.restaurant;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UpdateMenuOld extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;

    private StorageReference storageRefUpdate;
    private DatabaseReference databaseRefUpdate;
    private StorageTask menuTaskUpdate;

    private ImageView ivMenuUpdate;
    private Uri imageUriUpdate;

    private EditText etMenuName_Update, etMenuDescription_Update, etMenuPrice_Update;

    private TextView tViewMenuUpdate;
    private Button btn_SaveMenuUpdate;

    private String menuName_Update, menuDescription_Update;
    private double menuPrice_Update;

    String menuNameUpdate = "";
    String menuDescriptionUpdate = "";
    String menuPriceUpdate = "";
    String menuImageUpdate = "";
    String menuKeyUpdate = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_menu_old);

        storageRefUpdate = FirebaseStorage.getInstance().getReference("Menus");
        databaseRefUpdate = FirebaseDatabase.getInstance().getReference("Menus");

        progressDialog = new ProgressDialog(UpdateMenuOld.this);

        //initialise variables
        tViewMenuUpdate = findViewById(R.id.tvMenuUpdate);

        etMenuName_Update = findViewById(R.id.etMenuNameUpdate);
        etMenuDescription_Update = findViewById(R.id.etMenuDescriptionUpdate);
        etMenuPrice_Update = findViewById(R.id.etMenuPriceUpdate);
        ivMenuUpdate = findViewById(R.id.imgViewMenuUpdate);

        btn_SaveMenuUpdate = findViewById(R.id.btnSaveMenuUpdate);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            menuNameUpdate = bundle.getString("MName");
            menuDescriptionUpdate = bundle.getString("MDesc");
            menuPriceUpdate = bundle.getString("MPrice");
            menuImageUpdate = bundle.getString("MImage");
            menuKeyUpdate = bundle.getString("MKey");
        }

        //receive data from the other activity
        Picasso.get()
                .load(menuImageUpdate)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(ivMenuUpdate);

        tViewMenuUpdate.setText("Update the menu: " + menuNameUpdate);

        etMenuName_Update.setText(menuNameUpdate);
        etMenuDescription_Update.setText(menuDescriptionUpdate);
        etMenuPrice_Update.setText(String.valueOf(menuPriceUpdate));

        ivMenuUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //deleteOldMenuPicture();
                openGallery();
            }
        });

        //Action button Save updated Menu

        btn_SaveMenuUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (menuTaskUpdate != null && menuTaskUpdate.isInProgress()) {
                    Toast.makeText(UpdateMenuOld.this, "Update Menu in progress!!", Toast.LENGTH_SHORT).show();
                } else {
                    if (imageUriUpdate == null) {
                        alertDialogBikePicture();

                        //uploadMenuWithOldPicture();
                    } else {
                        updateMenuWithNewPicture();
                    }
                }
            }
        });

        Button buttonBacMenuUpdate = findViewById(R.id.btnBacMenuUpdate);
        buttonBacMenuUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateMenuOld.this, AdminPage.class));
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
            imageUriUpdate = data.getData();
            ivMenuUpdate.setImageURI(imageUriUpdate);
            Toast.makeText(UpdateMenuOld.this, "Image uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void deleteOldMenuPicture() {

        StorageReference storageRefDelete = getInstance().getReferenceFromUrl(menuImageUpdate);
        storageRefDelete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateMenuOld.this, "Previous image deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateMenuOld.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Upload the updated Menu into the Menu table
    public void updateMenuWithNewPicture() {

        if (validateUpdateMenuDetails()) {

            menuName_Update = etMenuName_Update.getText().toString().trim();
            menuDescription_Update = etMenuDescription_Update.getText().toString().trim();
            menuPrice_Update = Double.parseDouble(etMenuPrice_Update.getText().toString().trim());

            progressDialog.setTitle("The Menu is updating!");
            progressDialog.show();

            final StorageReference fileReference = storageRefUpdate.child(System.currentTimeMillis() + "." + getFileExtension(imageUriUpdate));
            menuTaskUpdate = fileReference.putFile(imageUriUpdate)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {

                                    databaseRefUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                                String menu_Key = postSnapshot.getKey();
                                                assert menu_Key != null;

                                                if (menu_Key.equals(menuKeyUpdate)) {
                                                    postSnapshot.getRef().child("menu_Name").setValue(menuName_Update);
                                                    postSnapshot.getRef().child("menu_Description").setValue(menuDescription_Update);
                                                    postSnapshot.getRef().child("menu_Price").setValue(menuPrice_Update);
                                                    postSnapshot.getRef().child("menu_Image").setValue(uri.toString());
                                                    deleteOldMenuPicture();
                                                }
                                            }

                                            Toast.makeText(UpdateMenuOld.this, "The Menu will be updated", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(UpdateMenuOld.this, AdminPage.class));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(UpdateMenuOld.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateMenuOld.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            //show upload Progress
                            double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Updated: " + (int) progress + "%");
                            progressDialog.setProgress((int) progress);
                        }
                    });
        }
    }

    private void uploadMenuWithOldPicture() {

        if (validateUpdateMenuDetails()) {

            //Add a new Menu into the Menu's table
            menuName_Update = etMenuName_Update.getText().toString().trim();
            menuDescription_Update = etMenuDescription_Update.getText().toString().trim();
            menuPrice_Update = Double.parseDouble(etMenuPrice_Update.getText().toString().trim());

            progressDialog.setTitle("The Menu is updating!");
            progressDialog.show();

            databaseRefUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String menu_Key = postSnapshot.getKey();
                        assert menu_Key != null;

                        if (menu_Key.equals(menuKeyUpdate)) {
                            postSnapshot.getRef().child("menu_Name").setValue(menuName_Update);
                            postSnapshot.getRef().child("menu_Description").setValue(menuDescription_Update);
                            postSnapshot.getRef().child("menu_Price").setValue(menuPrice_Update);
                        }
                    }

                    Toast.makeText(UpdateMenuOld.this, "The Menu will be updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateMenuOld.this, AdminPage.class));
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UpdateMenuOld.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public boolean validateUpdateMenuDetails() {

        boolean result = false;

        final String upMenu_NameVal = etMenuName_Update.getText().toString().trim();
        final String upMenu_DescriptionVal = etMenuDescription_Update.getText().toString().trim();
        final String upMenu_PriceVal = etMenuPrice_Update.getText().toString().trim();

        if (TextUtils.isEmpty(upMenu_NameVal)) {
            etMenuName_Update.setError("Please enter Menu name");
            etMenuName_Update.requestFocus();
        } else if (TextUtils.isEmpty(upMenu_DescriptionVal)) {
            etMenuDescription_Update.setError("Please enter Menu description");
            etMenuDescription_Update.requestFocus();
        } else if (TextUtils.isEmpty(upMenu_PriceVal)) {
            etMenuPrice_Update.setError("Please enter Menu price");
            etMenuPrice_Update.requestFocus();
        } else {
            result = true;
        }

        return result;
    }

    public void alertDialogBikePicture() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("No Menu picture changed.")
                .setMessage("Update the Menu with old picture.")
                .setPositiveButton("YES",
                        (arg0, arg1) -> uploadMenuWithOldPicture())
                .setNegativeButton("CANCEL", (dialog, id) -> dialog.dismiss());

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}