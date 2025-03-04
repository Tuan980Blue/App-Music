package com.example.musicapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d(TAG, "SplashActivity: onCreate");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "SplashActivity: run");
                checkUserAuthentication();
            }
        }, 2500);
    }

    private void checkUserAuthentication() {
        Log.d(TAG, "SplashActivity: checkUserAuthentication");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                Log.d(TAG, "SplashActivity: người dùng đã đăng nhập trước đó!");
                // Người dùng đã đăng nhập và đã xác thực email, chuyển hướng đến trang chính
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else{
                Log.d(TAG, "SplashActivity: người dùng chưa có tài khoản đăng nhập trước đó!");
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
        } else {
            Log.d(TAG, "SplashActivity: currentUser == null (người dùng rỗng)");
            // Người dùng chưa đăng nhập, chuyển hướng đến trang đăng nhập
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish();
    }
}
