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

public class RestaurantAdapterAdmin extends RecyclerView.Adapter<RestaurantAdapterAdmin.ImageViewHolder>{

    private Context restaurantsContext;
    private List<Restaurants> restaurantsUploads;

    private FirebaseStorage menusStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener menusEventListener;

    private List<Menus> menusList;

    private int numberMenusAvailable;

    private OnItemClickListener clickListener;

    public RestaurantAdapterAdmin(Context restaurants_context, List<Restaurants> restaurants_uploads){
        restaurantsContext = restaurants_context;
        restaurantsUploads = restaurants_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(restaurantsContext).inflate(R.layout.image_restaurant_admin,parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {

        final Restaurants uploadCurrent = restaurantsUploads.get(position);
        holder.tVRestName.setText(uploadCurrent.getRest_Name());
        holder.tVRestAddress.setText(uploadCurrent.getRest_Address());

        menusList = new ArrayList<>();

        //initialize the bike storage database
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
                        holder.tVMenusAvailable.setText(String.valueOf(numberMenusAvailable));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(BikeStoreAdapterAddBikesAdmin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantsUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tVRestName;
        public TextView tVRestAddress;
        public TextView tVMenusAvailable;

        public ImageViewHolder(View itemView) {
            super(itemView);

            tVRestName = itemView.findViewById(R.id.tvRestName);
            tVRestAddress = itemView.findViewById(R.id.tvRestAddress);
            tVMenusAvailable =  itemView.findViewById(R.id.tvMenusAvailable);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener !=null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    clickListener.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItmClickListener(OnItemClickListener listener){
        clickListener = listener;
    }
}
