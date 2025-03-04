package com.example.musicapp.Models;

public class AdminRequest {
    private String nameUser;
    private String email;
    private String userId; // Nếu cần lưu trữ ID của người dùng
    private Boolean isAdmin;

    public AdminRequest() {}

    public AdminRequest(String nameUser, String email, String userId, Boolean isAdmin, boolean registeredFromAdminActivity) {
        this.email = email;
        this.userId = userId;
        this.nameUser = nameUser;
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin != null && isAdmin.booleanValue();
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

}
