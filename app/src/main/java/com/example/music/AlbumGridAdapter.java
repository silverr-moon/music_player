package com.example.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AlbumGridAdapter extends RecyclerView.Adapter<AlbumGridAdapter.ViewHolder> {
    private Context context;
    private List<AlbumMusicItem> itemList;

    public AlbumGridAdapter(Context context, List<AlbumMusicItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public AlbumGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_grid_album,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumGridAdapter.ViewHolder holder, int position) {
        AlbumMusicItem item=itemList.get(position);
        holder.textView.setText(item.getTitle());

        //image load
        Glide.with(context)
                .load(item.getMusicUrl())
                .placeholder(R.drawable.baseline_music_note_24) //default image if loading fails
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageViewGrid);
            textView=itemView.findViewById(R.id.textViewGrid);
        }
    }
}
