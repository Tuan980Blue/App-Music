package com.example.musicapp.Models;

public class Artist {
    private String artist;
    private String avatarSong;

    public Artist() {
        // Constructor mặc định cần thiết cho Firebase
    }

    public Artist(String artist, String avatarSong) {
        this.artist = artist;
        this.avatarSong = avatarSong;
    }

    public String getAvatarSong() {
        return avatarSong;
    }

    public void setAvatarSong(String avatarSong) {
        this.avatarSong = avatarSong;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

}
