package com.example.musicapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.musicapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginAdminActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "LoginAdminActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        TextView textView = findViewById(R.id.loginadmin);

        EditText emailAdminLogin = findViewById(R.id.emailAdminLogin);
        EditText passwordAdminLogin = findViewById(R.id.passwordAdminLogin);
        TextView moveRegisterAdmin = findViewById(R.id.moveRegisterAdmin);
        Button loginAdmin = findViewById(R.id.loginAdmin);
        TextView backAdmin = findViewById(R.id.backAdmin);

        //Make color
        int startColor = Color.parseColor("#2196F3");
        int endColor = Color.parseColor("#B356D4DF");
        int centerColor = Color.parseColor("#B3568BCC");
        TextPaint paint = textView.getPaint();
        float textWidth = paint.measureText(textView.getText().toString());
        Shader textShader = new LinearGradient(0, 0, textWidth, textView.getTextSize(), new int[]{startColor, centerColor, endColor}, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(textShader);
        Drawable originalDrawable1 = ContextCompat.getDrawable(this, R.drawable.baseline_email_24);
        Drawable originalDrawable2 = ContextCompat.getDrawable(this, R.drawable.key);
        if (originalDrawable1 != null && originalDrawable2 !=null) {
            Drawable wrappedDrawable1 = DrawableCompat.wrap(originalDrawable1).mutate();
            Drawable wrappedDrawable2 = DrawableCompat.wrap(originalDrawable2).mutate();
            DrawableCompat.setTint(wrappedDrawable1, centerColor);
            DrawableCompat.setTint(wrappedDrawable2, centerColor);
            emailAdminLogin.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable1, null, null, null);
            passwordAdminLogin.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable2, null, null, null);
        } else {
            Log.e("LoginAdminActivity", "Drawable resource not found");
        }

        backAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginAdminActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        moveRegisterAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginAdminActivity.this, RegisterAdminActivity.class);
                startActivity(intent);
            }
        });

        loginAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailAdminLogin.getText().toString();
                String password = passwordAdminLogin.getText().toString();

                // Kiểm tra xem email và mật khẩu có được nhập không
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginAdminActivity.this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Đăng nhập sử dụng email và mật khẩu
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginAdminActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null && user.isEmailVerified()) {
                                        // Kiểm tra quyền Admin từ Firebase Realtime Database hoặc Firestore
                                        checkAdmin(user.getUid());
                                    } else {
                                        // Nếu không phải Admin hoặc email chưa được xác minh, hiển thị thông báo lỗi
                                        Toast.makeText(LoginAdminActivity.this, "Email chưa được xác minh hoặc bạn không có quyền Admin!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Đăng nhập thất bại, hiển thị thông báo lỗi
                                    Toast.makeText(LoginAdminActivity.this, "Đăng nhập không thành công. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void checkAdmin(String userId) {
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isAdmin = dataSnapshot.child("isAdmin").getValue(Boolean.class);
                    if (isAdmin) {
                        // Nếu là Admin, chuyển hướng đến MainActivity
                        Intent mainIntent = new Intent(LoginAdminActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish(); // Đóng Activity hiện tại
                    } else {
                        // Nếu không phải Admin, hiển thị thông báo lỗi
                        Toast.makeText(LoginAdminActivity.this, "Bạn không có quyền Admin!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Nếu không tìm thấy dữ liệu người dùng, hiển thị thông báo lỗi
                    Toast.makeText(LoginAdminActivity.this, "Không tìm thấy thông tin người dùng trong thống đăng kí admin!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Toast.makeText(LoginAdminActivity.this, "Lỗi khi truy xuất dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
