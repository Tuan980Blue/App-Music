package com.example.musicapp.Models;

import java.util.List;

public class AdminRequestList {
    List<AdminRequest> adminRequests;

    public List<AdminRequest> getAdminRequests(){
        return adminRequests;
    }
    public void setAdminRequests(List<AdminRequest> adminRequests){
        this.adminRequests = adminRequests;
    }
}
