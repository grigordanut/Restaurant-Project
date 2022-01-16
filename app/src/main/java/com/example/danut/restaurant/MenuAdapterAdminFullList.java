package com.example.danut.restaurant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MenuAdapterAdminFullList extends RecyclerView.Adapter<MenuAdapterAdminFullList.ImageViewHolder> {

    //declare variables
    private final Context menuContext;
    private final List<Menus> menuUploads;
    private OnItemClickListener clickListener;

    public MenuAdapterAdminFullList(Context menu_context, List<Menus> menu_uploads){
        menuContext = menu_context;
        menuUploads = menu_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(menuContext).inflate(R.layout.image_menu_full_list, parent, false);
        return new ImageViewHolder(v);
    }

    //set the item layout view
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        Menus uploadCurrent = menuUploads.get(position);
        holder.tVRestNameFulList.setText(uploadCurrent.getRestaurant_Name());
        holder.textViewName.setText("Menu Name: "+uploadCurrent.getMenu_Name());
        holder.textViewDescription.setText("Description: "+uploadCurrent.getMenu_Description());
        holder.textViewPrice.setText("Price: € "+uploadCurrent.getMenu_Price());

        Picasso.get()
                .load(uploadCurrent.getMenu_Image())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    //assign the values of textViews
    @Override
    public int getItemCount() {
        return menuUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tVRestNameFulList;
        public TextView textViewName;
        public TextView textViewDescription;
        public TextView textViewPrice;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tVRestNameFulList = itemView.findViewById(R.id.tvRestNameFulList);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewPrice = itemView.findViewById(R.id.text_view_price);
            imageView = itemView.findViewById(R.id.image_view_upload);

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

    public void setOnItmClickListener(MenuAdapterAdminFullList.OnItemClickListener listener){
        clickListener = listener;
    }
}
