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
    private final Context mContext;
    private final List<Menus> mUploads;
    private MenuAdapterAdmin.OnItemClickListener clickListener;

    public MenuAdapterCustomer(Context context, List<Menus> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_menu, parent, false);
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
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(mContext);
                View promptsView = li.inflate(R.layout.image_menu_full, null);

                androidx.appcompat.app.AlertDialog.Builder
                        alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(mContext);

                alertDialogBuilder.setView(promptsView);

                final ImageView img_full = (ImageView) promptsView.findViewById(R.id.imgImageFull);

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
        return mUploads.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public TextView textViewDescription;
        public TextView textViewPrice;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewPrice = itemView.findViewById(R.id.text_view_price);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }
}
