package com.example.musicapp.Models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private static MutableLiveData<String> name = new MutableLiveData<>();

    public static void setName(String newName) {
        name.setValue(newName);
    }

    public LiveData<String> getName() {
        return name;
    }
}