package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MenuAdapterCustomer extends RecyclerView.Adapter<MenuAdapterCustomer.ImageViewHolder> {

    //declare variables
    private final Context menuContext;
    private final List<Menus> menuUploads;

    private OnItemClickListener clickListener;

    public MenuAdapterCustomer(Context menu_context, List<Menus> menu_uploads) {
        menuContext = menu_context;
        menuUploads = menu_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(menuContext).inflate(R.layout.image_menu, parent, false);
        return new ImageViewHolder(v);
    }

    //set the item layout view
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Menus uploadCurrent = menuUploads.get(position);
        holder.tVNameMenu.setText("Menu Name: " + uploadCurrent.getMenu_Name());
        holder.tVDescriptionMenu.setText("Description: " + uploadCurrent.getMenu_Description());
        holder.tVPriceMenu.setText("Price: € " + uploadCurrent.getMenu_Price());

        Picasso.get()
                .load(uploadCurrent.getMenu_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageShowMenu);
    }

    //assign the values of textViews
    @Override
    public int getItemCount() {
        return menuUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tVNameMenu;
        public TextView tVDescriptionMenu;
        public TextView tVPriceMenu;
        public ImageView imageShowMenu;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tVNameMenu = itemView.findViewById(R.id.tvNameMenu);
            tVDescriptionMenu = itemView.findViewById(R.id.tvDescriptionMenu);
            tVPriceMenu = itemView.findViewById(R.id.tvPriceMenu);
            imageShowMenu = itemView.findViewById(R.id.imgShowMenu);

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
