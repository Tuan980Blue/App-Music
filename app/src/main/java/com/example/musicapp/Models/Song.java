package com.example.musicapp.Models;

public class Song {
    private String id;
    private String title;
    private String album;
    private String artist;
    private String author;
    private String category;
    private String audioUrl;
    private String avatarSong;
    private int listens; // Trường mới để lưu trữ số lượt nghe

    public Song() {
        // Required default constructor for Firebase
    }

    public Song(String id, String title, String album, String artist, String author, String category, String audioUrl, String avatarSong) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.author = author;
        this.category = category;
        this.audioUrl = audioUrl;
        this.avatarSong = avatarSong;
        this.listens = 0;
    }

    public String getAvatarSong() {
        return avatarSong;
    }

    public void setAvatarSong(String avatarSong) {
        this.avatarSong = avatarSong;
    }

    //lấy số lượt nghe ra
    public int getListens() {
        return listens;
    }

    // Getters and setters...

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}
