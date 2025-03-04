package com.example.musicapp.Models;

public class SliderMoveModel {
    private final int Image;
    private final String slideName;

    public SliderMoveModel(int image, String slideName) {
        Image = image;
        this.slideName = slideName;
    }

    public int getImage() {
        return Image;
    }

    public String getSlideName() {
        return slideName;
    }
}
