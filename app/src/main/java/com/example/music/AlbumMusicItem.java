package com.example.music;

public class AlbumMusicItem {
    private String title; //music title
    private String imageUrl; //cover image for artist albums

    public AlbumMusicItem(String title,String imageUrl){
        this.title=title;
        this.imageUrl=imageUrl;
    }
    public String getTitle(){
        return title;
    }
    public String getMusicUrl(){
        return imageUrl;
    }
}
