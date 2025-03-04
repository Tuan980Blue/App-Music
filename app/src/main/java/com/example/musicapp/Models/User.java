package com.example.musicapp.Models;

public class User {
    public User() {
        // Constructor mặc định cần phải có, thậm chí là trống.
    }
    private String userId;
    private String email;
    private String password;
    private String avatarUser;
    private String nameUser;
    private String phone;
    private boolean isAdmin; // Phân quyền
    private boolean isPremium;//dịch vụ perimium

    public User(String userId, String email,String nameUser,String phone,Boolean isAdmin,Boolean isPremium) {
        this.userId = userId;
        this.email = email;
        this.nameUser = nameUser;
        this.phone = phone;
        this.isAdmin = isAdmin;
        this.isPremium = isPremium;
    }

    public String toString() {
        return nameUser;
    }
    public String getUserId() {
        return userId;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getAvatarUser() {
        return avatarUser;
    }
    public void setAvatarUser(String avatarUser) {
        this.avatarUser = avatarUser;
    }
    public String getNameUser() {
        return nameUser;
    }
    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }
    //Phân quyền
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }
}