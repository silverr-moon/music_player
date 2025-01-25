package com.example.music;

import static com.example.music.MainActivity.musicFiles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    TextView song_name,artist_name,duration_played,duration_total,nowplaying_top_layout,album_name_top_layout;
    ImageView cover_art,nextbtn,prevbtn,backbtn,shufflebtn, repeatbtn,menubtn;
    FloatingActionButton playpauebtn;
    SeekBar seekBar;
    int position=-1;
    static ArrayList<MusicFiles> listSongs=new ArrayList<>();
    static MediaPlayer mediaPlayer;
    static Uri uri;
    private Handler handler=new Handler();
    private Thread playThread,prevThread,nextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mediaplayer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //initiaize and link all variables to id
        intitViews();
        getIntentMethod();
        //setting text for song_name and artist name
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser)
                    mediaPlayer.seekTo(progress*1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });
    }


    //button config
    @Override
    protected void onResume() {
        playThreadbtn();
        prevThreadbtn();
        nexrThreadbtn();
        super.onResume();
    }

    //nextbtn config
    private void nexrThreadbtn() {
        nextThread= new Thread(){
            @Override
            public void run(){
                super.run();
                nextbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }
    private void nextBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            position=((position+1)%listSongs.size());
            uri=Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaDeta(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playpauebtn.setImageResource(R.drawable.baseline_pausebtn);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            position=((position+1)%listSongs.size());
            uri=Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaDeta(uri);

            //update the texts on song change
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            //change seekbar
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playpauebtn.setImageResource(R.drawable.baseline_playbtn);
        }
    }
    //prevbtnconfig
    private void prevThreadbtn() {
        prevThread= new Thread(){
            @Override
            public void run(){
                super.run();
                prevbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }
    private void prevBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            position=((position-1) < 0 ? (listSongs.size()-1) : (position-1));
            uri=Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaDeta(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playpauebtn.setImageResource(R.drawable.baseline_pausebtn);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            position=((position-1)<0 ? (listSongs.size()-1) : (position-1));
            uri=Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaDeta(uri);

            //update the texts on song change
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            //change seekbar
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
            playpauebtn.setImageResource(R.drawable.baseline_playbtn);
        }
    }
    //playbtnconfig
    private void playThreadbtn() {
        playThread= new Thread(){
            @Override
            public void run(){
                super.run();
                playpauebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }
    private void playPauseBtnClicked() {
        if(mediaPlayer.isPlaying()){
            playpauebtn.setImageResource(R.drawable.baseline_playbtn);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        else{
            playpauebtn.setImageResource(R.drawable.baseline_pausebtn);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }

    private String formattedTime(int mCurrentPosition) {

        String seconds=String.valueOf(mCurrentPosition%60);
        String minutes=String.valueOf(mCurrentPosition/60);
        String totalOut=minutes+":"+seconds;
        String totalNew=minutes+":"+"0"+seconds;
        if(seconds.length()==1)
            return totalNew;
        else
            return totalOut;
    }

    private void getIntentMethod() {
        position=getIntent().getIntExtra("position",-1);
        listSongs=musicFiles;
        if(listSongs!=null){
            playpauebtn.setImageDrawable(getResources().getDrawable(R.drawable.baseline_pausebtn));
        }
        Uri newUri=Uri.parse(listSongs.get(position).getPath());
        if(mediaPlayer!=null) {
            //if same song is already playing
            if (mediaPlayer.isPlaying() && uri.equals(newUri))
                return;

            //stop and release current song to new song
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        uri=newUri;//update class level uri
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        if(mediaPlayer!=null){
            mediaPlayer.start();
            playpauebtn.setImageDrawable(getResources().getDrawable(R.drawable.baseline_pausebtn));
        }else{
            Toast.makeText(this, "error playing song", Toast.LENGTH_SHORT).show();
        }

        seekBar.setMax(mediaPlayer.getDuration()/1000);
        metaDeta(uri);

    }

    private void intitViews() {
        song_name=findViewById(R.id.song_name);
        artist_name=findViewById(R.id.song_artist);
        backbtn=findViewById(R.id.back_btn);
        nowplaying_top_layout=findViewById(R.id.playingtxt);
        album_name_top_layout=findViewById(R.id.playlist_name);
        menubtn=findViewById(R.id.menu_btn);
        cover_art=findViewById(R.id.cover_art);
        seekBar=findViewById(R.id.seekbar);
        duration_played=findViewById(R.id.txtSongStart);
        duration_total=findViewById(R.id.txtSongEnd);
        shufflebtn=findViewById(R.id.shufflebtn);
        repeatbtn=findViewById(R.id.repeatbtn);
        playpauebtn=findViewById(R.id.playpausebtn);
        nextbtn=findViewById(R.id.nextbtn);
        prevbtn=findViewById(R.id.prevbtn);
    }

    private  void metaDeta(Uri uri){

        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal=Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art=retriever.getEmbeddedPicture();

        //palette api
        Bitmap bitmap;
        if(art!=null){
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(cover_art);
            bitmap= BitmapFactory.decodeByteArray(art,0,art.length);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch=palette.getDominantSwatch();
                    if(swatch!=null){
                        RelativeLayout gradient=findViewById(R.id.card);
                        RelativeLayout toolbar=findViewById(R.id.top_btns_layout);
                        ScrollView mContainer=findViewById(R.id.mediaplayer);

                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{
                                swatch.getRgb(),Color.WHITE,swatch.getRgb()
                        });
                        gradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawablebg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{
                               swatch.getRgb(),swatch.getRgb()
                        });
                        mContainer.setBackground(gradientDrawablebg);
                        toolbar.setBackground(gradientDrawablebg);

                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.LTGRAY);
                    }
                    else{
                        //fallback color
                        RelativeLayout gradient=findViewById(R.id.card);
                        RelativeLayout mContainer=findViewById(R.id.mediaplayer_layout);
                        gradient.setBackgroundResource(R.drawable.gradient_player_pg);
                        mContainer.setBackgroundResource(R.color.dark_colour_primary);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }
            });
        } else{
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.musiclogo)
                    .into(cover_art);

        }
    }
    

}