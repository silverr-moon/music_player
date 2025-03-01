package com.example.music;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HorizontalAdapater extends RecyclerView.Adapter<HorizontalAdapater.ItemViewHolder> {
    private Context context;
    private List<AlbumMusicItem> itemList;

    public HorizontalAdapater(Context context,List<AlbumMusicItem> itemList) {
        this.itemList = itemList;
        this.context=context;
    }

    @NonNull
    @Override
    public HorizontalAdapater.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_music_album,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalAdapater.ItemViewHolder holder, int position) {
        AlbumMusicItem item=itemList.get(position);
        holder.musicTitle.setText(item.getTitle());
        Glide.with(context).load(item.getMusicUrl()).into(holder.musicImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) { // Make sure position is valid
                    AlbumMusicItem currentItem = itemList.get(currentPosition);
                    Intent intent = new Intent(context, Album_details.class);
                    intent.putExtra("albumName", currentItem.getTitle());
                    intent.putExtra("albumcover", currentItem.getMusicUrl());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView musicImage;
        TextView musicTitle;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            musicImage=itemView.findViewById(R.id.music_image);
            musicTitle=itemView.findViewById(R.id.music_title);
        }
    }
}
