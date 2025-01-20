package com.example.music;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.telecom.TelecomManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BitmapTransformation;
import kotlin.ranges.UIntRange;

public class MusicSongAdapter extends RecyclerView.Adapter<MusicSongAdapter.MyViewHolder> {

    private Context mcontext;
    private ArrayList<MusicFiles> mFiles;

    MusicSongAdapter(Context mcontext,ArrayList<MusicFiles> mFiles){
        this.mcontext=mcontext;
        this.mFiles=mFiles;
    }

    @NonNull
    @Override
    public MusicSongAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.music_song_items,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        try{
            byte[] image=getSongsArt(mFiles.get(position).getPath());
            if(image !=null){
                Glide.with(mcontext).asBitmap()
                        .load(image) //to load drawable resource replace image with R.drawable.name
                        .apply(new RequestOptions()
                                .transform(
                                        new BorderTransformation(7,mcontext.getResources().getColor(android.R.color.white))
                                ))//add a 10-pixel white border
                        .diskCacheStrategy(DiskCacheStrategy.NONE)//prevent caching
                        .skipMemoryCache(true) //skip memory cache as well
                        .into(holder.song_art);
            }
            else{
                holder.song_art.setBackgroundResource(R.drawable.song_list_bg);
                Glide.with(mcontext)
                        .load(R.drawable.baseline_music_note_24)
                        .into(holder.song_art);
            }
        }catch (IOException e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView file_name;
        ImageView song_art;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name=itemView.findViewById(R.id.music_file_name);
            song_art=itemView.findViewById(R.id.music_img);

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

    //to change the border of song's embedded
    private class BorderTransformation extends BitmapTransformation{
        private final int borderSize;
        private final int borderColor;

        public BorderTransformation(int borderSize,int borderColor){
            this.borderColor=borderColor;
            this.borderSize=borderSize;
        }

        @Override
        protected Bitmap transform(@NonNull Context context, @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            if(toTransform==null)return null;
            int newWidth = toTransform.getWidth() + borderSize * 2;
            int newHeight = toTransform.getHeight() + borderSize * 2;

            Bitmap result=Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);
            Canvas canvas=new Canvas(result);

            //draw the border
            Paint borderPaint=new Paint();
            borderPaint.setColor(borderColor);
            borderPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0,0,newWidth,newHeight,borderPaint);

            //draw the image
            canvas.drawBitmap(toTransform,borderSize,borderSize,null);

            return result;
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
            messageDigest.update(("BorderTransformation"+borderSize+borderColor).getBytes());
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }
}
