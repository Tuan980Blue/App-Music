package com.example.musicapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.musicapp.Models.AdminRequest;
import com.example.musicapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterAdminActivity extends AppCompatActivity {
    private AdminRequest adminRequest;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText emailAdminRegister, passwordAdminRegister, cfpasswordAdminRegister, userAdminRegister;
    private TextView moveRegisterAdmin;
    private Button registerAdmin;
    private static final String TAG = "RegisterAdminActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        TextView textView = findViewById(R.id.registeradmin);

        userAdminRegister = findViewById(R.id.userAdminRegister);
        emailAdminRegister = findViewById(R.id.emailAdminRegister);
        passwordAdminRegister = findViewById(R.id.passwordAdminRegister);
        cfpasswordAdminRegister = findViewById(R.id.cfpasswordAdminRegister);
        moveRegisterAdmin = findViewById(R.id.moveLoginAdmin);
        registerAdmin = findViewById(R.id.registerAdmin);

        //Make color
        int startColor = Color.parseColor("#2196F3");
        int endColor = Color.parseColor("#B356D4DF");
        int centerColor = Color.parseColor("#B3568BCC");
        TextPaint paint = textView.getPaint();
        float textWidth = paint.measureText(textView.getText().toString());
        Shader textShader = new LinearGradient(0, 0, textWidth, textView.getTextSize(), new int[]{startColor, centerColor, endColor}, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(textShader);
        Drawable originalDrawable1 = ContextCompat.getDrawable(this, R.drawable.user);
        Drawable originalDrawable2 = ContextCompat.getDrawable(this, R.drawable.baseline_email_24);
        Drawable originalDrawable3 = ContextCompat.getDrawable(this, R.drawable.key);
        if (originalDrawable1 != null && originalDrawable2 != null && originalDrawable3 != null) {
            Drawable wrappedDrawable1 = DrawableCompat.wrap(originalDrawable1).mutate();
            Drawable wrappedDrawable2 = DrawableCompat.wrap(originalDrawable2).mutate();
            Drawable wrappedDrawable3 = DrawableCompat.wrap(originalDrawable3).mutate();
            DrawableCompat.setTint(wrappedDrawable1, centerColor);
            DrawableCompat.setTint(wrappedDrawable2, centerColor);
            DrawableCompat.setTint(wrappedDrawable3, centerColor);
            userAdminRegister.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable1, null, null, null);
            emailAdminRegister.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable2, null, null, null);
            passwordAdminRegister.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable3, null, null, null);
            cfpasswordAdminRegister.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable3, null, null, null);
        } else {
            Log.e(TAG, "Drawable resource not found");
        }

        moveRegisterAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterAdminActivity.this, LoginAdminActivity.class);
                startActivity(intent);
            }
        });

        registerAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAdmin();
            }
        });
    }

    private void registerAdmin() {
        String email = emailAdminRegister.getText().toString().trim();
        String password = passwordAdminRegister.getText().toString().trim();
        String confirmPassword = cfpasswordAdminRegister.getText().toString().trim();
        String nameUser = userAdminRegister.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || nameUser.isEmpty()) {
            Toast.makeText(RegisterAdminActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(RegisterAdminActivity.this, "Mật khẩu phải chứa ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterAdminActivity.this, "Mật khẩu và xác nhận mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }


        // Create an Intent to start AdminApprovalActivity
        Intent intent = new Intent(RegisterAdminActivity.this, AdminApprovalActivity.class);

        // Pass AdminRequest object to AdminApprovalActivity using Intent
//        intent.putExtra("adminRequest", adminRequest);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterAdminActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser FirebaseUser = mAuth.getCurrentUser();
                            if (FirebaseUser != null) {
                                // Gửi email xác minh
                                FirebaseUser.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegisterAdminActivity.this, "Đăng ký thành công! Vui lòng kiểm tra email để xác minh tài khoản.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.e(TAG, "sendEmailVerification", task.getException());
                                                    Toast.makeText(RegisterAdminActivity.this, "Gửi email xác minh thất bại.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                            String userId = mAuth.getCurrentUser().getUid();
                            mDatabase.child(userId).child("email").setValue(email);
                            mDatabase.child(userId).child("nameUser").setValue(nameUser);
                            mDatabase.child(userId).child("isAdmin").setValue(false); // Thiết lập isAdmin thành false



                            // Chuyển hướng người dùng trở lại trang LoginAdminActivity
                            Intent intent = new Intent(RegisterAdminActivity.this, LoginAdminActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterAdminActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
