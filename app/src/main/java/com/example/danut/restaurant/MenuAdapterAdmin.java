package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.danut.restaurant.R.*;

/**
 * Created by danut on 24/03/2018.
 */

public class MenuAdapterAdmin extends RecyclerView.Adapter<MenuAdapterAdmin.ImageViewHolder>{

    //declare variables
    private final Context mContext;
    private final List<Menus> mUploads;
    private OnItemClickListener clickListener;

    public MenuAdapterAdmin(Context context, List<Menus> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(layout.image_menu, parent, false);
        return new ImageViewHolder(v);
    }

    //set the item layout view
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Menus uploadCurrent = mUploads.get(position);
        holder.textViewName.setText("Menu Name: "+uploadCurrent.getMenu_Name());
        holder.textViewDescription.setText("Description: "+uploadCurrent.getMenu_Description());
        holder.textViewPrice.setText("Price: € "+uploadCurrent.getMenu_Price());

        Picasso.get()
            .load(uploadCurrent.getMenu_Image())
            .placeholder(mipmap.ic_launcher)
            .fit()
            .centerCrop()
            .into(holder.imageView);
    }

    //assign the values of textViews
    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView textViewName;
        public TextView textViewDescription;
        public TextView textViewPrice;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(id.text_view_name);
            textViewDescription = itemView.findViewById(id.text_view_description);
            textViewPrice = itemView.findViewById(id.text_view_price);
            imageView = itemView.findViewById(id.image_view_upload);

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

    public void setOnItmClickListener(OnItemClickListener listener){
        clickListener = listener;
    }
}
