package com.example.music;

import static com.example.music.MainActivity.musicFiles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SongsFragment extends Fragment {

    RecyclerView songrecyclerView;
    MusicSongAdapter musicSongAdapter;
    public SongsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_songs, container, false);
        songrecyclerView=view.findViewById(R.id.SongrecyclerView);
        if (songrecyclerView == null) {
            Log.e("SongsFragment", "RecyclerView is null!");
        }
        songrecyclerView.setHasFixedSize(true);
        if(!(musicFiles.size()<1)){
            musicSongAdapter=new MusicSongAdapter(getContext(),musicFiles);
            songrecyclerView.setAdapter(musicSongAdapter);
            songrecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        }
        return view;
    }
}