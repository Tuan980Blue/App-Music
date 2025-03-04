package com.example.musicapp.Models;

public class Singer {
    private String name;
    private String imageUrl; // Đường dẫn đến hình ảnh của ca sĩ (nếu cần)
    // Các trường thông tin khác về ca sĩ (nếu cần)

    public Singer() {
        // Constructor mặc định cần thiết cho Firebase
    }

    public Singer(String name) {
        this.name = name;
    }

    // Getter và setter cho các trường thông tin của ca sĩ (nếu cần)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}