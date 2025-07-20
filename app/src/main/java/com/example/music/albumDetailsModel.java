package com.example.music;

import java.util.List;

public class albumDetailsModel {
    private String title;
    private List<String> artists; //change string to list<string>
    private String duration;

    public albumDetailsModel(String title, List<String> artists, String duration) {
        this.title = title;
        this.artists = artists;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getArtists() {
        return artists;
    }

    public String getDuration() {
        return duration;
    }

    public boolean hasArtist(String artistName){
        for(String artist:artists){
            if (artist.equalsIgnoreCase(artistName.trim())){
                return true;
            }
        }
        return false;
    }
}
