package com.example.danut.restaurant;

import static android.icu.text.DateFormat.NONE;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapterAdminShowRest extends RecyclerView.Adapter<RestaurantAdapterAdminShowRest.ImageViewHolder> {

    private final Context restaurantsContext;
    private final List<Restaurants> restaurantsUploads;

    private OnItemClickListener clickListener;

    private FirebaseStorage menusStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener menusEventListener;

    private List<Menus> menusList;

    private int numberMenusAvailable;

    public RestaurantAdapterAdminShowRest(Context restaurants_context, List<Restaurants> restaurants_uploads) {
        restaurantsContext = restaurants_context;
        restaurantsUploads = restaurants_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(restaurantsContext).inflate(R.layout.image_restaurant, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        final Restaurants uploadCurrent = restaurantsUploads.get(position);
        holder.tVRestName.setText(uploadCurrent.getRest_Name());
        holder.tVRestAddress.setText(uploadCurrent.getRest_Address());

        menusList = new ArrayList<>();

        //initialize the Menus database
        menusStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Menus");

        menusEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menusList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Menus menus = postSnapshot.getValue(Menus.class);
                    assert menus != null;
                    if (menus.getRestaurant_Key().equals(uploadCurrent.getRest_Key())) {
                        menus.setMenu_Key(postSnapshot.getKey());
                        menusList.add(menus);
                        numberMenusAvailable = menusList.size();
                        holder.tVMenusAvailable.setText(String.valueOf(numberMenusAvailable));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantsUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView tVRestName;
        public TextView tVRestAddress;
        public TextView tVMenusAvailable;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tVRestName = itemView.findViewById(R.id.tvRestName);
            tVRestAddress = itemView.findViewById(R.id.tvRestAddress);
            tVMenusAvailable = itemView.findViewById(R.id.tvMenusAvailable);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(position);
                }
            }
        }

        //create onItem click menu
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select an Action");
            MenuItem doAddMenu = menu.add(NONE, 1, 1, "Add New Menu");
            MenuItem doUpdateRest = menu.add(NONE, 2, 2, "Update Restaurant");
            MenuItem doDeleteRest = menu.add(NONE, 3, 3, "Delete restaurant");
            MenuItem doClose = menu.add(NONE, 4, 4, "Close");

            doAddMenu.setOnMenuItemClickListener(this);
            doUpdateRest.setOnMenuItemClickListener(this);
            doDeleteRest.setOnMenuItemClickListener(this);
            doClose.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (clickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            clickListener.onAddMenuClick(position);
                            return true;
                        case 2:
                            clickListener.onUpdateRestClick(position);
                            return true;
                        case 3:
                            if (tVMenusAvailable.getText().toString().equals(String.valueOf(0))) {
                                clickListener.onDeleteRestClick(position);
                            } else {
                                clickListener.alertDialogRestaurantNotEmpty(position);
                            }
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onAddMenuClick(int position);

        void onUpdateRestClick(int position);

        void onDeleteRestClick(int position);

        void alertDialogRestaurantNotEmpty(int position);
    }

    public void setOnItmClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

}
