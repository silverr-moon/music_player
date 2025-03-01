package com.example.music;

import java.util.List;

public class AlbumcategoryModel {
    private String title; //category title like artists, recommendate,etc
    private List<AlbumMusicItem> items; //list of music items inside this category

    public AlbumcategoryModel(String title, List<AlbumMusicItem> items) {
        this.title = title;
        this.items = items;
    }

    public String getTitle(){
        return title;
    }
    public  List<AlbumMusicItem> getItems(){
        return items;
    }
}
