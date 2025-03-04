package com.example.musicapp.Models;

public class SliderModel
{
    private final int Image;
    private final String slideName;

    public SliderModel(int image, String slideName) {
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
