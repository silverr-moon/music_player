package com.example.music;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlbumDetailAdapter extends RecyclerView.Adapter<AlbumDetailAdapter.ALbumViewHolder> {

    private List<albumDetailsModel> songList;

    public AlbumDetailAdapter(List<albumDetailsModel> songList){
        this.songList=songList;
    }

    @NonNull
    @Override
    public AlbumDetailAdapter.ALbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.album_detail_song_layout,parent,false);
        return new ALbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumDetailAdapter.ALbumViewHolder holder, int position) {
        albumDetailsModel albumDetailsModel=songList.get(position);
        holder.songTitle.setText(albumDetailsModel.getTitle());
        holder.songImg.setImageResource(R.drawable.ic_launcher_background);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class ALbumViewHolder extends RecyclerView.ViewHolder {

        TextView songTitle;
        ImageView songImg;

        public ALbumViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle=itemView.findViewById(R.id.music_file_name);
            songImg=itemView.findViewById(R.id.music_img);
        }
    }
}
