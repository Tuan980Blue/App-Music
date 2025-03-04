package com.example.musicapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.Utils.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    EditText edt_username, edt_Email, edt_password, edt_confirmPass;
    // Biến để kiểm tra xem mật khẩu có đang hiển thị hay không
    private boolean isPasswordVisible,isConfirmPasswordVisible = false;
    Button btn_createAcc;
    ProgressBar progressBar;
    TextView txt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edt_username = findViewById(R.id.edt_username);
        edt_Email = findViewById(R.id.edt_Email);
        edt_password = findViewById(R.id.edt_password);
        edt_confirmPass = findViewById(R.id.edt_confirmPass);
        ImageView showPass = findViewById(R.id.showPassword);
        ImageView showCfPass =findViewById(R.id.showCfPassword);

        btn_createAcc = findViewById(R.id.btn_CreateAcc);
        txt_login = findViewById(R.id.txt_login);
        progressBar = findViewById(R.id.progress_bar);

        showPass.setOnClickListener(v -> {showPassword();
            if (!isPasswordVisible)
            {showPass.setImageResource(R.drawable.icon_eyesx);}
            else
                showPass.setImageResource(R.drawable.icon_eyes);
        });
        showCfPass.setOnClickListener(v -> {showConfirmPassword();
            if (!isConfirmPasswordVisible)
            {showCfPass.setImageResource(R.drawable.icon_eyesx);}
            else
                showCfPass.setImageResource(R.drawable.icon_eyes);
        });

        btn_createAcc.setOnClickListener(v -> createAccount());
        txt_login.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }
    void createAccount(){
        String UserName = edt_username.getText().toString();
        String Email = edt_Email.getText().toString();
        String PassWord = edt_password.getText().toString();
        String ConfirmPassWord = edt_confirmPass.getText().toString();

        boolean validate = validateData(UserName,Email,PassWord,ConfirmPassWord);
        if (!validate)  //gia tri sai
        {
            return;
        }
        createAccountInFirebase(Email,PassWord,UserName);
    }

    //
    void createAccountInFirebase(String email, String password, String username) {
        changeInProgress(true); // Đánh dấu rằng quá trình tạo tài khoản đang diễn ra

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    changeInProgress(false); // Đánh dấu rằng quá trình tạo tài khoản đã hoàn thành
                    if (task.isSuccessful()) {
                        // Quá trình tạo tài khoản thành công
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Lưu thông tin người dùng vào Firebase Realtime Database hoặc Firestore
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                            userRef.child("email").setValue(email);
                            userRef.child("nameUser").setValue(username);
                            userRef.child("isAdmin").setValue(false);
                        }
                        // Hiển thị thông báo thành công
                        Utility.showToast(RegisterActivity.this, "Tạo tài khoản thành công. Vui lòng kiểm tra email để xác nhận.");
                        firebaseAuth.getCurrentUser().sendEmailVerification();
                        // Gửi email xác nhận đến người dùng
                        if (user != null && user.isEmailVerified()) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Utility.showToast(RegisterActivity.this, "Email xác thực đã được gửi đến " + email);
                                        } else {
                                            Utility.showToast(RegisterActivity.this, "Không thể gửi email xác thực. Vui lòng thử lại sau.");
                                        }
                                    });
                        } else {
                            Utility.showToast(RegisterActivity.this, "Không thể gửi email xác thực. Vui lòng thử lại sau.");
                        }
                        // Đăng xuất người dùng hiện tại
                        firebaseAuth.signOut();
                        // Kết thúc Activity đăng ký
                        finish();
                    } else {
                        // Quá trình tạo tài khoản thất bại
                        Utility.showToast(RegisterActivity.this, task.getException().getLocalizedMessage());
                    }
                });
    }
    //Hàm đợi tạo tài khoản
    void changeInProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            btn_createAcc.setVisibility(View.VISIBLE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            btn_createAcc.setVisibility(View.VISIBLE);
        }
    }

    //hàm kiểm tra giá trị
    boolean validateData(String UserName,String Email,String PassWord,String ConfirmPassWord ){
        if (UserName.length() < 4 || !Pattern.compile("[a-zA-Z]").matcher(UserName).find()) {
            edt_username.setError("Tên không hợp lệ!phải chứa ít nhất một ký tự chữ.");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            edt_Email.setError("Email khong hop le!");
            return false;
        }
        if (PassWord.length() < 6 || !PassWord.matches(".*[A-Z].*")) {
            edt_password.setError("PassWord không đủ độ dài hoặc không chứa chữ cái hoa!");
            return false;
        }
        if (!PassWord.equals(ConfirmPassWord)){
            edt_confirmPass.setError("PassWord khong khop!");
            return false;
        }
        return  true;
    }

    private void showPassword() {
        if (!isPasswordVisible) {
            // Nếu mật khẩu không hiển thị, hiển thị mật khẩu
            edt_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = true;
        } else {
            // Nếu mật khẩu đang hiển thị, ẩn mật khẩu
            edt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = false;
        }
        // Di chuyển con trỏ tới cuối văn bản
        edt_password.setSelection(edt_password.getText().length());
    }
    private void showConfirmPassword() {
        if (!isConfirmPasswordVisible) {
            // Nếu mật khẩu không hiển thị, hiển thị mật khẩu
            edt_confirmPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isConfirmPasswordVisible = true;
        } else {
            // Nếu mật khẩu đang hiển thị, ẩn mật khẩu
            edt_confirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isConfirmPasswordVisible = false;
        }
        // Di chuyển con trỏ tới cuối văn bản
        edt_confirmPass.setSelection(edt_confirmPass.getText().length());
    }
}