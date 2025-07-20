package com.example.music;

import static com.example.music.MainActivity.musicFiles;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Grid;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class AlbumsFragment extends Fragment {

    RecyclerView gridRecycleView;
    RecyclerView categoryRecyclerView;
    AlbumCategoryAdapter categoryAdapter;
    List<AlbumcategoryModel> categoryList;
    List<AlbumMusicItem> gridList;

    public AlbumsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_albums, container, false);
        categoryRecyclerView=view.findViewById(R.id.recyclerViewCategory);

        //prepare grid list(liked songs)
        gridList=getAlbumsList();

        //prep horizontal categories
        categoryList=new ArrayList<>();
        categoryList.add(new AlbumcategoryModel("Artists",getArtistsList()));
        categoryList.add(new AlbumcategoryModel("Recommended", getRecommendedList()));

        categoryAdapter=new AlbumCategoryAdapter(getContext(),categoryList,gridList);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        categoryRecyclerView.setAdapter(categoryAdapter);

        gridRecycleView=view.findViewById(R.id.recyclerviewGrid);
        gridRecycleView.setLayoutManager(new GridLayoutManager(getContext(),2)); //2 columns
        //set adapter with click listener
        gridRecycleView.setAdapter(new AlbumGridAdapter(getContext(),gridList){
            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder,int position){
                super.onBindViewHolder(holder,position);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int currentPosition = holder.getAdapterPosition();
                        if (currentPosition != RecyclerView.NO_POSITION) { // Make sure position is valid
                            AlbumMusicItem item = gridList.get(currentPosition);
                            Intent intent = new Intent(getContext(), Album_details.class);
                            intent.putExtra("albumName", item.getTitle());
                            intent.putExtra("albumCover", item.getMusicUrl());
                            startActivity(intent);
                        }
                    }
                });
            }
        });
        return view;
    }

    private  List<AlbumMusicItem> getArtistsList(){
        List<AlbumMusicItem> artistsList = new ArrayList<>();

        // Add artist names and images (replace with actual data)
        artistsList.add(new AlbumMusicItem("Arijit Singh", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTMhnVw8TC5dASgNZFawfRTG11GZotevNp0mg&s"));
        artistsList.add(new AlbumMusicItem("Shreya Ghoshal", "https://today.ganganews.com/wp-content/uploads/2023/11/Shreya-Ghoshal.jpg"));
        artistsList.add(new AlbumMusicItem("A.R. Rahman", "https://upload.wikimedia.org/wikipedia/commons/3/3b/AR_Rahman_At_The_%E2%80%98Marvel_Anthem%E2%80%99_Launch_%283x4_cropped%29.jpg"));
        artistsList.add(new AlbumMusicItem("Chase Atlantic", "https://wallpapers.com/images/featured/chase-atlantic-d9bypp95qzxqipvm.jpg"));

        return artistsList;
    }
    private List<AlbumMusicItem> getRecommendedList() {
        List<AlbumMusicItem> recommendedList = new ArrayList<>();
        recommendedList.add(new AlbumMusicItem("Song 1", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Arijit_5th_GiMA_Awards.jpg/640px-Arijit_5th_GiMA_Awards.jpg"));
        recommendedList.add(new AlbumMusicItem("Song 2", "https://upload.wikimedia.org/wikipedia/commons/b/bd/Shreya_Ghoshal_at_Filmfare_Awards_South.jpg"));
        return recommendedList;
    }

    private List<AlbumMusicItem> getAlbumsList() {
        List<AlbumMusicItem> albumList = new ArrayList<>();
        albumList.add(new AlbumMusicItem("Liked Song 1", "https://upload.wikimedia.org/wikipedia/commons/b/bd/Shreya_Ghoshal_at_Filmfare_Awards_South.jpg"));
        albumList.add(new AlbumMusicItem("Liked Song 2", "https://upload.wikimedia.org/wikipedia/commons/b/bd/Shreya_Ghoshal_at_Filmfare_Awards_South.jpg"));
        return albumList;
    }

}