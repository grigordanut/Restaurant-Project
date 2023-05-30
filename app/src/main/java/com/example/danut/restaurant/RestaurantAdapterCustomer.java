package com.example.danut.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RestaurantAdapterCustomer extends RecyclerView.Adapter<RestaurantAdapterCustomer.ImageViewHolder> {

    private final Context restaurantsContext;
    private final List<Restaurants> restaurantsUploads;

    private OnItemClickListener clickListener;

    public RestaurantAdapterCustomer(Context restaurants_context, List<Restaurants> restaurants_uploads) {
        restaurantsContext = restaurants_context;
        restaurantsUploads = restaurants_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(restaurantsContext).inflate(R.layout.image_restaurant_customer, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {

        final Restaurants uploadCurrent = restaurantsUploads.get(position);
        holder.tVShowRestNameCustom.setText(uploadCurrent.getRest_Name());
    }

    @Override
    public int getItemCount() {
        return restaurantsUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tVShowRestNameCustom;

        public ImageViewHolder(View itemView) {
            super(itemView);

            tVShowRestNameCustom = itemView.findViewById(R.id.tvShowRestNameCustom);

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
