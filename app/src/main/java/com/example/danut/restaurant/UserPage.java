package com.example.danut.restaurant;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //Declare Variables
    private static final int PICK_IMAGE = 100;
    private Uri imageUriPicture;
    private String user_ImageId = "";
    private CircleImageView ivAddPicture;

    //Access customer database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    //Retrieve and display data from Users database
    private StorageReference stRefAddUserPicture;
    private DatabaseReference dbRefAddUserPicture;
    private DatabaseReference dbRefUsers;
    private ValueEventListener evListenerUsers;
    private StorageTask eventsUploadTask;

    //Retrieve and Display data from Restaurants database
    private DatabaseReference dbRefRest;
    private ValueEventListener evListenerRest;

    private List<Restaurants> restaurantsList;

    private TextView tVUserPage, tVUserWelcome, tVUserRestsAv;

    private int numberRestsAv;

    private ProgressDialog progressDialog;

    //Declaring some objects
    private DrawerLayout drawerLayoutUser;
    private ActionBarDrawerToggle drawerToggleUser;
    private NavigationView navigationViewUser;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        Objects.requireNonNull(getSupportActionBar()).setTitle("User main page");

        progressDialog = new ProgressDialog(UserPage.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        stRefAddUserPicture = FirebaseStorage.getInstance().getReference("Users");
        dbRefAddUserPicture = FirebaseDatabase.getInstance().getReference("Users");

        //Retrieve data from Users database
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");

        //Retrieve data from Restaurants database
        dbRefRest = FirebaseDatabase.getInstance().getReference("Restaurants");

        restaurantsList = new ArrayList<>();

        drawerLayoutUser = findViewById(R.id.activity_user_page);
        navigationViewUser = findViewById(R.id.navViewUserPage);
        View header = navigationViewUser.getHeaderView(0);

        tVUserWelcome = findViewById(R.id.tvUserWelcome);
        tVUserRestsAv = findViewById(R.id.tvUserRestsAv);
        tVUserPage = header.findViewById(R.id.tvUserPage);
        ivAddPicture = header.findViewById(R.id.imgUserPicture);

        findViewById(R.id.layoutRestaurants).setOnClickListener(this);
        findViewById(R.id.layoutMenus).setOnClickListener(this);

        drawerToggleUser = new ActionBarDrawerToggle(this, drawerLayoutUser, R.string.open_userPage, R.string.close_userPage);

        drawerLayoutUser.addDrawerListener(drawerToggleUser);
        drawerToggleUser.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        evListenerUsers = dbRefUsers.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi", "NonConstantResourceId"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    final Users users_Data = postSnapshot.getValue(Users.class);

                    assert users_Data != null;
                    if (firebaseUser.getUid().equals(postSnapshot.getKey())) {
                        tVUserWelcome.setText("Welcome: " + users_Data.getUser_firstName() + " " + users_Data.getUser_lastName());

                        Picasso.get()
                                .load(users_Data.getUser_picture())
                                .placeholder(R.mipmap.ic_launcher_round)
                                .fit()
                                .centerCrop()
                                .into(ivAddPicture);

                        tVUserPage.setText(users_Data.getUser_firstName() + " " + users_Data.getUser_lastName());
                        user_ImageId = users_Data.getUser_picture();

                        navigationViewUser.setNavigationItemSelectedListener(item -> {
                            int id = item.getItemId();
                            switch (id) {

                                //User add picture
                                case R.id.user_addPicture:
                                    openGallery();
                                    break;

                                //Edit User profile
                                case R.id.user_editProfile:
                                    Intent edit_Profile = new Intent(UserPage.this, UserEditProfile.class);
                                    startActivity(edit_Profile);
                                    break;

                                //Change User email
                                case R.id.user_changeEmail:
                                    Intent change_Email = new Intent(UserPage.this, UserChangeEmail.class);
                                    startActivity(change_Email);
                                    break;

                                //Change User Password
                                case R.id.user_changePassword:
                                    Intent change_Password = new Intent(UserPage.this, UserChangePassword.class);
                                    startActivity(change_Password);
                                    break;

                                default:
                                    return true;
                            }
                            return true;
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_page, menu);
        return true;
    }

    public void userLogOut() {
        alertDialogUserLogout();
    }

    public void userEditProfile() {
        startActivity(new Intent(UserPage.this, UserEditProfile.class));
        finish();
    }

    public void userChangeEmail() {
        startActivity(new Intent(UserPage.this, UserChangeEmail.class));
        finish();
    }

    public void userChangePassword() {
        startActivity(new Intent(UserPage.this, UserChangePassword.class));
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggleUser.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.user_logOut) {
            userLogOut();
        }

        if (item.getItemId() == R.id.user_editProfile) {
            userEditProfile();
        }

        if (item.getItemId() == R.id.user_changeEmail) {
            userChangeEmail();
        }

        if (item.getItemId() == R.id.user_changePassword) {
            userChangePassword();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    public void alertDialogUserLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserPage.this);
        alertDialogBuilder
                .setTitle("User logout!!")
                .setMessage("Are you sure to log out?")
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, id) -> {

                    progressDialog.setTitle("User logout!!");
                    progressDialog.show();

                    firebaseAuth.signOut();
                    LayoutInflater inflater = getLayoutInflater();
                    @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                    TextView text = layout.findViewById(R.id.tvToast);
                    ImageView imageView = layout.findViewById(R.id.imgToast);
                    text.setText("Logout successful!!");
                    imageView.setImageResource(R.drawable.ic_baseline_logout_24);
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                    startActivity(new Intent(UserPage.this, LoginUser.class));
                    finish();
                })

                .setNegativeButton("NO", (dialog, id) -> dialog.cancel());

        progressDialog.dismiss();
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadRestaurantsAv();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (evListenerUsers != null) {
            dbRefUsers.removeEventListener(evListenerUsers);
        }
    }

    public void loadRestaurantsAv() {

        evListenerRest = dbRefRest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                restaurantsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Restaurants rest_Data = postSnapshot.getValue(Restaurants.class);

                    if (rest_Data != null) {
                        rest_Data.setRest_Key(postSnapshot.getKey());
                        restaurantsList.add(rest_Data);
                        numberRestsAv = restaurantsList.size();
                        tVUserRestsAv.setText(String.valueOf(numberRestsAv));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserPage.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //Add Restaurants
            case R.id.layoutRestaurants:
                startActivity(new Intent(UserPage.this, RestaurantImageCustomerShowRest.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            //Show Menus
            case R.id.layoutMenus:
                startActivity(new Intent(UserPage.this, RestaurantImageCustomerShowMenu.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

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
            imageUriPicture = data.getData();
            ivAddPicture.setImageURI(imageUriPicture);

            checkUserPictureExists();

            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
            TextView text = layout.findViewById(R.id.tvToast);
            ImageView imageView = layout.findViewById(R.id.imgToast);
            text.setText("User picture uploaded!!");
            imageView.setImageResource(R.drawable.baseline_image_24);
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

    private void checkUserPictureExists() {

        if (user_ImageId == null) {
            uploadUserPicture();
        } else {
            uploadUserPicture();
            deleteOldUserPicture();
        }
    }

    //Upload a picture into the Users table
    public void uploadUserPicture() {

        //Add picture into Users database
        progressDialog.setTitle("Upload User picture!!");
        progressDialog.show();

        final StorageReference fileReference = stRefAddUserPicture.child(System.currentTimeMillis() + "." + getFileExtension(imageUriPicture));
        eventsUploadTask = fileReference.putFile(imageUriPicture)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {

                            dbRefAddUserPicture.addListenerForSingleValueEvent(new ValueEventListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        if (firebaseUser.getUid().equals(postSnapshot.getKey())) {
                                            postSnapshot.getRef().child("user_picture").setValue(uri.toString());
                                        }
                                    }

                                    user_ImageId = uri.toString();
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(UserPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }))

                .addOnFailureListener(e -> Toast.makeText(UserPage.this, e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnProgressListener(taskSnapshot -> {
                    //show upload Progress
                    double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    progressDialog.setProgress((int) progress);
                });
    }

    private void deleteOldUserPicture() {
        StorageReference storageRefDelete = getInstance().getReferenceFromUrl(user_ImageId);
        storageRefDelete.delete()
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(UserPage.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}