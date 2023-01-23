package com.example.danut.restaurant;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UpdateMenuImage extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;

    private StorageReference storageRefUpImg;
    private DatabaseReference databaseRefUpImg;
    private StorageTask menuTaskUpImg;

    private ImageView ivMenuUpImg;
    private Uri imageUriUpImg;

    private EditText etMenuName_UpImg, etMenuDescription_UpImg, etMenuPrice_UpImg;

    private TextView tVMenuUpImg, tVMenuInfoImg;

    private String menuNameUpImg = "";
    private String menuDescriptionUpImg = "";
    private String menuPriceUpImg = "";
    private String menuImageUpImg = "";
    private String menuKeyUpImg = "";

    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_menu_image);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Update Menu Picture");

        storageRefUpImg = FirebaseStorage.getInstance().getReference("Menus");
        databaseRefUpImg = FirebaseDatabase.getInstance().getReference("Menus");

        progressDialog = new ProgressDialog(UpdateMenuImage.this);

        //initialise variables
        tVMenuUpImg = findViewById(R.id.tvMenuUpImg);
        tVMenuInfoImg = findViewById(R.id.tvMenuInfoImg);

        etMenuName_UpImg = findViewById(R.id.etMenuNameUpImg);
        etMenuName_UpImg.setEnabled(false);
        etMenuDescription_UpImg = findViewById(R.id.etMenuDescriptionUpImg);
        etMenuDescription_UpImg.setEnabled(false);
        etMenuPrice_UpImg = findViewById(R.id.etMenuPriceUpImg);
        etMenuPrice_UpImg.setEnabled(false);
        ivMenuUpImg = findViewById(R.id.imgViewMenuUpImg);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            menuNameUpImg = bundle.getString("MNameImg");
            menuDescriptionUpImg = bundle.getString("MDescImg");
            menuPriceUpImg = bundle.getString("MPriceImg");
            menuImageUpImg = bundle.getString("MImageImg");
            menuKeyUpImg = bundle.getString("MKeyImg");
        }

        //receive data from the other activity
        Picasso.get()
                .load(menuImageUpImg)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(ivMenuUpImg);

        tVMenuUpImg.setText("Menu: " + menuNameUpImg);
        tVMenuInfoImg.setText("Click image to update Picture");

        etMenuName_UpImg.setText(menuNameUpImg);
        etMenuDescription_UpImg.setText(menuDescriptionUpImg);
        etMenuPrice_UpImg.setText(String.valueOf(menuPriceUpImg));

        ivMenuUpImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        //Action button Save updated Menu
        Button btn_SaveMenuUpImg = findViewById(R.id.btnSaveMenuUpImg);
        btn_SaveMenuUpImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuTaskUpImg != null && menuTaskUpImg.isInProgress()) {
                    Toast.makeText(UpdateMenuImage.this, "Update menu in progress", Toast.LENGTH_SHORT).show();
                } else {
                    updateMenuPicture();
                }
            }
        });

        Button btn_BacMenuUpImg = findViewById(R.id.btnBacMenuUpImg);
        btn_BacMenuUpImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateMenuImage.this, AdminPage.class));
            }
        });
    }

    //Insert a picture
    public void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUriUpImg = data.getData();
            ivMenuUpImg.setImageURI(imageUriUpImg);
            Toast.makeText(UpdateMenuImage.this, "Image uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void deleteOldMenuPicture() {

        StorageReference storageRefDelete = getInstance().getReferenceFromUrl(menuImageUpImg);
        storageRefDelete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateMenuImage.this, "Previous image deleted", Toast.LENGTH_SHORT).show();
                ivMenuUpImg.setImageResource(R.drawable.add_menus_picture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateMenuImage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Upload the updated Menu into the Menu table
    public void updateMenuPicture() {

        if (validateUpdateMenuPicture()) {

            progressDialog.setTitle("The Menu is updating!");
            progressDialog.show();

            final StorageReference fileReference = storageRefUpImg.child(System.currentTimeMillis() + "." + getFileExtension(imageUriUpImg));
            menuTaskUpImg = fileReference.putFile(imageUriUpImg)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {

                                    databaseRefUpImg.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                                String menu_key = postSnapshot.getKey();
                                                assert menu_key != null;

                                                if (menu_key.equals(menuKeyUpImg)) {
                                                    postSnapshot.getRef().child("menu_Image").setValue(uri.toString());

                                                    deleteOldMenuPicture();
                                                }
                                            }

                                            Toast.makeText(UpdateMenuImage.this, "The Menu will be updated", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(UpdateMenuImage.this, AdminPage.class));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(UpdateMenuImage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateMenuImage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public boolean validateUpdateMenuPicture() {

        boolean result = false;

        if (imageUriUpImg == null) {
            alertDialogMenuPicture();
        } else {
            result = true;
        }

        return result;
    }

    public void alertDialogMenuPicture() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle("Picture not Changed!!.")
                .setMessage("The Menu picture has not been changed.\nPlease click image to change the picture.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}