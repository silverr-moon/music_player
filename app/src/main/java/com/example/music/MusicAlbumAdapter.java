package com.example.music;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;

public class MusicAlbumAdapter extends RecyclerView.Adapter<MusicAlbumAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<MusicFiles> albumFiles;
    View view;

    public MusicAlbumAdapter(ArrayList<MusicFiles> albumFiles, Context mContext) {
        this.mContext=mContext;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MusicAlbumAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(mContext).inflate(R.layout.album_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAlbumAdapter.MyViewHolder holder, int position) {
        holder.album_name.setText(albumFiles.get(position).getAlbum());
        try{
            byte[] image=getSongsArt(albumFiles.get(position).getPath());
            if(image !=null){
                Glide.with(mContext).asBitmap()
                        .load(image) //to load drawable resource replace image with R.drawable.name
                        .diskCacheStrategy(DiskCacheStrategy.NONE)//prevent caching
                        .skipMemoryCache(true) //skip memory cache as well
                        .into(holder.album_image);
            }
            else{
                holder.album_image.setBackgroundResource(R.drawable.song_list_bg);
                Glide.with(mContext)
                        .load(R.drawable.baseline_music_note_24)
                        .into(holder.album_image);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  {
        ImageView album_image;
        TextView album_name;
        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            album_image=itemView.findViewById(R.id.album_image);
            album_name=itemView.findViewById(R.id.album_name);
        }
    }
    private byte[] getSongsArt(String uri) throws IOException {
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        //got IO exception hence to skip and ensure retriever is released with no problem we have throws
        retriever.setDataSource(uri);
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;
        /*
            the MediaMetadataRetriever.release() method internally throws a RuntimeException. This is a checked exception that needs to be handled explicitly.
To resolve the issue, you should wrap the call to retriever.release() in a try-catch block, even within the finally block.hence we have another try catch inside finally
             */
    }
}
