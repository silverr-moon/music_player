package com.example.music;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Album_details extends AppCompatActivity {

    ImageView albumCover,backbtn;
    TextView albumName,timespan;
    List<albumDetailsModel> allSongs;
    List<albumDetailsModel> filteredSongs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_album_details);

        // Initialize Views
        initViews();

        //initialise allSongs with dummy data
        allSongs=new ArrayList<>();
        allSongs.add(new albumDetailsModel("Song1", Arrays.asList("Artist 1","Artist 2"),"3.45"));
        allSongs.add(new albumDetailsModel("Song 2", Arrays.asList("Artist 2"), "4:00"));
        allSongs.add(new albumDetailsModel("Song 3", Arrays.asList("Arijit Singh"), "5:30"));
        allSongs.add(new albumDetailsModel("Song 4", Arrays.asList("Shreya Ghoshal"), "4:15"));
        allSongs.add(new albumDetailsModel("Song 5", Arrays.asList("A.R. Rahman"), "6:00"));
        //load all songs


        // Receive Data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("albumName");
            String coverUrl = intent.getStringExtra("albumCover");


            // Set Text
            albumName.setText(name != null ? "Artist: " + name : "Unknown Album");



            // Load Image using Glide
            if (coverUrl != null && !coverUrl.isEmpty()) {
                Glide.with(this).load(coverUrl).into(albumCover);
            } else {
                albumCover.setImageResource(R.drawable.musiclogo); // Add a default image
            }
            //filter songs based on artist name
            filterSongsByartist(name);

            //set total album duration
            getDUration(name);
        }

        //back btn
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(getApplicationContext(), AlbumsFragment.class);
                startActivity(intent1);
            }
        });


    }
    private void initViews() {
        albumCover = findViewById(R.id.album_cover);
        albumName = findViewById(R.id.artist_name);
        backbtn=findViewById(R.id.back_btn);
        timespan=findViewById(R.id.timespan);
    }

    //get total album duration
    private void getDUration(String artistName) {
        int totalDurinsec = 0;

        for (albumDetailsModel song : allSongs) {
            if (song.hasArtist(artistName))
                totalDurinsec += convertDurationtosec(song.getDuration());
            String totalTimeformatted = formatDuration(totalDurinsec);
            timespan.setText("Duration: " + totalTimeformatted);
        }
    }
    //helper methods of album duration
    private int convertDurationtosec(String duration){
        //in mm:ss format
        String[] parts=duration.split(":");
        int mins=Integer.parseInt(parts[0]);
        int sec=Integer.parseInt(parts[1]);
        return (mins*60)+sec;
    }
    private String formatDuration(int totalsec){
        int hr=totalsec/3600;
        int mins=(totalsec%3600)/60;
        return hr+" hr "+mins+" mins";
    }

    //get songs that has this artist
    private void filterSongsByartist(String artistName){
        filteredSongs=new ArrayList<>();
        for (albumDetailsModel song:allSongs){
            if(song.hasArtist(artistName)){
                filteredSongs.add(song);
            }
        }
        //update recyclerview with songs
        AlbumDetailAdapter adapter=new AlbumDetailAdapter(filteredSongs);
        RecyclerView recyclerView=findViewById(R.id.albumlistrecycleview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}