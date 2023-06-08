package com.example.danut.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //Access customer database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    //Retrieve and Display data from Users database
    private DatabaseReference userDatabaseReference;
    private ValueEventListener userEventListener;

    //Retrieve and Display data from Restaurants database
    private DatabaseReference restDatabaseReference;
    private ValueEventListener restEventListener;
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

        Objects.requireNonNull(getSupportActionBar()).setTitle("USER: main page");

        progressDialog = new ProgressDialog(UserPage.this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Retrieve data from Restaurants database
        restDatabaseReference = FirebaseDatabase.getInstance().getReference("Restaurants");

        //Retrieve data from Users database
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        restaurantsList = new ArrayList<>();

        drawerLayoutUser = findViewById(R.id.activity_user_page);
        navigationViewUser = findViewById(R.id.navViewUserPage);
        View header = navigationViewUser.getHeaderView(0);

        tVUserWelcome = findViewById(R.id.tvUserWelcome);
        tVUserRestsAv = findViewById(R.id.tvUserRestsAv);
        tVUserPage = header.findViewById(R.id.tvUserPage);

        findViewById(R.id.layoutRestaurants).setOnClickListener(this);
        findViewById(R.id.layoutMenus).setOnClickListener(this);

        drawerToggleUser = new ActionBarDrawerToggle(this, drawerLayoutUser, R.string.open_userPage, R.string.close_userPage);

        drawerLayoutUser.addDrawerListener(drawerToggleUser);
        drawerToggleUser.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        userEventListener = userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi", "NonConstantResourceId"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    final Users users_Data = postSnapshot.getValue(Users.class);

                    assert users_Data != null;
                    if (firebaseUser.getUid().equals(postSnapshot.getKey())) {
                        tVUserWelcome.setText("Welcome: " + users_Data.getUser_firstName() + " " + users_Data.getUser_lastName());

                        tVUserPage.setText(users_Data.getUser_firstName() + " " + users_Data.getUser_lastName());

                        //Adding Click Events to our navigation drawer item
                        navigationViewUser.setNavigationItemSelectedListener(item -> {
                            int id = item.getItemId();
                            switch (id) {

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
                .setTitle("Log out User!!")
                .setMessage("Are you sure to Log out?")
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, id) -> {

                    progressDialog.setTitle("Log out User!!");
                    progressDialog.show();

                    firebaseAuth.signOut();
                    LayoutInflater inflater = getLayoutInflater();
                    @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                    TextView text = layout.findViewById(R.id.tvToast);
                    ImageView imageView = layout.findViewById(R.id.imgToast);
                    text.setText("Logout Successful!!");
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
        loadPatientsAv();
    }

    public void loadPatientsAv() {

        restEventListener = restDatabaseReference.addValueEventListener(new ValueEventListener() {
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
            //Show Restaurants
            case R.id.layoutRestaurants:
                startActivity(new Intent(UserPage.this, RestaurantImageCustomerShowRest.class));
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            //Show Menus
            case R.id.layoutMenus:
                startActivity(new Intent(UserPage.this, RestaurantImageCustomerShowMenu.class));
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

        }//end switch

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}