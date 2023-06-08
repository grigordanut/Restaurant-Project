package com.example.danut.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
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

public class RestaurantAdapterCustomer extends RecyclerView.Adapter<RestaurantAdapterCustomer.ImageViewHolder> {

    private final Context restaurantsContext;
    private final List<Restaurants> restaurantsUploads;

    private FirebaseStorage menusStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener menusEventListener;

    private List<Menus> menusList;

    private int numberMenusAvailable;

    private OnItemClickListener clickListener;

    public RestaurantAdapterCustomer(Context restaurants_context, List<Restaurants> restaurants_uploads) {
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
        holder.tVRestNameCustom.setText(uploadCurrent.getRest_Name());
        holder.tVRestAddressCustom.setText(uploadCurrent.getRest_Address());

        menusList = new ArrayList<>();

        //initialize the Menus Storage database
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
                        menusList.add(menus);
                        numberMenusAvailable = menusList.size();
                        holder.tVMenusAvailableCustom.setText(String.valueOf(numberMenusAvailable));
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

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tVRestNameCustom;
        public TextView tVRestAddressCustom;
        public TextView tVMenusAvailableCustom;

        public ImageViewHolder(View itemView) {
            super(itemView);

            tVRestNameCustom = itemView.findViewById(R.id.tvRestName);
            tVRestAddressCustom = itemView.findViewById(R.id.tvRestAddress);
            tVMenusAvailableCustom = itemView.findViewById(R.id.tvMenusAvailable);

            itemView.setOnClickListener(this);
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
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItmClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }
}
