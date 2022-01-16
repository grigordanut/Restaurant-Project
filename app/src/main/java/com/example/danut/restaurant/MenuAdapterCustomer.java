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

public class MenuAdapterCustomer extends RecyclerView.Adapter<MenuAdapterCustomer.ImageViewHolder>{

    //declare variables
    private final Context menuContext;
    private final List<Menus> menuUploads;

    public MenuAdapterCustomer(Context menu_context, List<Menus> menu_uploads){
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
        holder.tVNameMenu.setText("Menu Name: "+uploadCurrent.getMenu_Name());
        holder.tVDescriptionMenu.setText("Description: "+uploadCurrent.getMenu_Description());
        holder.tVPriceMenu.setText("Price: € "+uploadCurrent.getMenu_Price());

        Picasso.get()
                .load(uploadCurrent.getMenu_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageShowMenu);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(menuContext);
                View promptsView = li.inflate(R.layout.image_menu_customer_full, null);

                androidx.appcompat.app.AlertDialog.Builder
                        alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(menuContext);

                alertDialogBuilder.setView(promptsView);

                final ImageView img_full = (ImageView) promptsView.findViewById(R.id.imgFullCustomer);

                Picasso.get()
                        .load(uploadCurrent.getMenu_Image())
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(img_full);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle("Menu Name: " + uploadCurrent.getMenu_Name())
                        .setNegativeButton("CLOSE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
    }

    //assign the values of textViews
    @Override
    public int getItemCount() {
        return menuUploads.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

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
        }
    }
}
