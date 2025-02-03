package com.example.music;

import static com.example.music.MainActivity.musicFiles;
import static com.example.music.MainActivity.repeatBoolean;
import static com.example.music.MainActivity.shuffleboolean;

import android.content.Context;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
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
        initViews();
        getIntentMethod();
        //setting text for song_name and artist name
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());

        mediaPlayer.setOnCompletionListener(this);
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
        shufflebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleboolean){
                    shuffleboolean=false;
                    shufflebtn.setImageResource(R.drawable.baseline_shuffleoff);
                }else{
                    shuffleboolean=true;
                    shufflebtn.setImageResource(R.drawable.baseline_shuffleon);
                }
            }
        });
        repeatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeatBoolean){
                    repeatBoolean=false;
                    repeatbtn.setImageResource(R.drawable.baseline_replay_24);
                }else{
                    repeatBoolean=true;
                    repeatbtn.setImageResource(R.drawable.baseline_replayon);
                }
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
            if(shuffleboolean && !repeatBoolean){
                position=getRandom(listSongs.size()-1);
            }
            else if(!shuffleboolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
            //else its repeat on so position will be same
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
            mediaPlayer.setOnCompletionListener(this);
            playpauebtn.setImageResource(R.drawable.baseline_pausebtn);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleboolean && !repeatBoolean){
                position=getRandom(listSongs.size()-1);
            }
            else if(!shuffleboolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
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
            mediaPlayer.setOnCompletionListener(this);
            playpauebtn.setImageResource(R.drawable.baseline_playbtn);
        }
    }

    private int getRandom(int i) {
        int newPosition;
        do{
            newPosition=new Random().nextInt(i+1);
        }while (newPosition==position); //avoid repeating same song
        return newPosition;
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
            if(shuffleboolean && !repeatBoolean){
                position=getRandom(listSongs.size()-1);
            }
            else if(!shuffleboolean && !repeatBoolean) {
                position=((position-1) < 0 ? (listSongs.size()-1) : (position-1));
            }

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
            mediaPlayer.setOnCompletionListener(this);
            playpauebtn.setImageResource(R.drawable.baseline_pausebtn);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleboolean && !repeatBoolean){
                position=getRandom(listSongs.size()-1);
            }
            else if(!shuffleboolean && !repeatBoolean) {
                position=((position-1) < 0 ? (listSongs.size()-1) : (position-1));
            }
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
            mediaPlayer.setOnCompletionListener(this);
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

    private void initViews() {
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

    //modified to add colours to background if there is embedded pictur and want bkg of that colour
    private void applyBackgroundColour(int colour){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RelativeLayout gradient=findViewById(R.id.card);
                RelativeLayout toolbar=findViewById(R.id.top_btns_layout);
                ScrollView mContainer=findViewById(R.id.mediaplayer);
                RelativeLayout mainLayout=findViewById(R.id.mediaplayer_layout);

                //to card
                GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{
                        colour,Color.WHITE,colour
                });
                gradient.setBackground(gradientDrawable);

                //apply solid colour to bkgs
                mContainer.setBackgroundColor(colour);
                toolbar.setBackgroundColor(colour);
                mainLayout.setBackgroundColor(colour);
            }
        });
    }
    private  void metaDeta(Uri uri){

        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal=Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art=retriever.getEmbeddedPicture();

        //needed ids
        RelativeLayout gradient=findViewById(R.id.card);
        RelativeLayout toolbar=findViewById(R.id.top_btns_layout);
        ScrollView mContainer=findViewById(R.id.mediaplayer);
        RelativeLayout mainLayout=findViewById(R.id.mediaplayer_layout);

        //palette api
        Bitmap bitmap;
        if(art!=null){
            bitmap= BitmapFactory.decodeByteArray(art,0,art.length);
            ImageAnimation(this,cover_art,bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    if(palette!=null && palette.getDominantSwatch()!=null){
                        int color=palette.getDominantSwatch().getRgb();
                        applyBackgroundColour(color);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.LTGRAY);
                    }
                    else{
                        //fallback color
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
            gradient.setBackgroundResource(R.drawable.gradient_player_pg);
            mContainer.setBackgroundResource(R.color.dark_colour_primary);
            toolbar.setBackgroundResource(R.color.dark_colour_primary);
            mainLayout.setBackgroundResource(R.color.dark_colour_primary);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.LTGRAY);

        }
    }

    //to have animation to fade out and fade in on song change
    public void ImageAnimation(Context context,ImageView imageView,Bitmap bitmap){
        Animation animOut= AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn=AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {}

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        if (!listSongs.isEmpty()) { //prevent crash if no songs available
            //apply shuffle and repeat conditions
            if(shuffleboolean & !repeatBoolean){
                position=getRandom(listSongs.size()-1);
            } else if (!shuffleboolean && !repeatBoolean){
                position = (position + 1) % listSongs.size();
            }
            //if repeat on then same song will play again so position will be same
            uri = Uri.parse(listSongs.get(position).getPath());

            //run this on separate thread to avoid ui freeze
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.reset(); //reset instead of release(safer)
                    try {
                        mediaPlayer.setDataSource(getApplicationContext(),uri);
                        mediaPlayer.prepare();
                        metaDeta(uri);
                        song_name.setText(listSongs.get(position).getTitle());
                        artist_name.setText(listSongs.get(position).getArtist());

                        seekBar.setMax(mediaPlayer.getDuration() / 1000);
                        seekBar.setProgress(0); //reset seekbar to start position

                        playpauebtn.setImageResource(R.drawable.baseline_pausebtn);
                        mediaPlayer.start();

                        //ensure completion listerner set for next song too
                        mediaPlayer.setOnCompletionListener(PlayerActivity.this); //keep it looping
                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(PlayerActivity.this, "Error playing next Song", Toast.LENGTH_SHORT).show();
                    }

                }
            },500); //small delay to ensure smooth transition

        } else {
            Toast.makeText(this, "No songs available", Toast.LENGTH_SHORT).show();
        }
    }
}