package com.example.musicapp.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.Utils.Utility;
import com.example.musicapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText edt_Email, edt_password;
    private boolean isPasswordVisible = false;
    Button btn_login;
    ProgressBar progressBar;
    TextView txt_register;
    ImageButton admin;
    private boolean isAdmin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt_Email = findViewById(R.id.edt_Email);
        edt_password = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_login);
        txt_register = findViewById(R.id.txt_register);
        progressBar = findViewById(R.id.progress_bar);
        ImageView showPass = findViewById(R.id.showPasswordlg);
        admin = findViewById(R.id.admin);

        showPass.setOnClickListener(v -> {showPasswordlg();
            if (!isPasswordVisible)
            {showPass.setImageResource(R.drawable.icon_eyes);}
            else
                showPass.setImageResource(R.drawable.icon_eyesx);
        });

        btn_login.setOnClickListener(v -> loginUser());
        txt_register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị dialog khi nhấn vào nút "admin"
                showAdminInfoDialog();
            }
        });
    }

    TextView NoAdmin;
    TextView IsAdmin;
    CheckBox checkAdmin1,checkAdmin2;

    private void showAdminInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_admin, null);
        // Lấy ID của các phần tử trong dialog
        NoAdmin = dialogView.findViewById(R.id.NoAdmin);
        IsAdmin = dialogView.findViewById(R.id.IsAdmin);
        checkAdmin1 = dialogView.findViewById(R.id.checkAdmin1);
        checkAdmin2 = dialogView.findViewById(R.id.checkAdmin2);

        builder.setView(dialogView); // Sử dụng layout XML của dialog
        builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Đóng dialog khi nhấn nút "Đóng"
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        // Thêm sự kiện kiểm tra khi cả hai CheckBox được chọn
        IsAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra nếu cả hai CheckBox đều đã được chọn
                if (checkAdmin1.isChecked() && checkAdmin2.isChecked()) {
                    //Phân quyền
                    isAdmin = true;
                    // Chuyển sang LoginAdminActivity
                    Intent intent = new Intent(LoginActivity.this, LoginAdminActivity.class);
                    startActivity(intent);
                    // Đóng dialog
                    dialog.dismiss();
                } else {
                    // Hiển thị thông báo yêu cầu người dùng chọn cả hai quyền Admin
                    Toast.makeText(LoginActivity.this, "Vui lòng đọc kĩ và làm theo yêu cầu!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        NoAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Đóng dialog
            }
        });
    }

    void loginUser(){
        String Email = edt_Email.getText().toString();
        String PassWord = edt_password.getText().toString();

        boolean validate = validateData(Email,PassWord);
        if (!validate)  //gia tri sai
        {
            return;
        }
        loginAccountInFirebase(Email,PassWord);
    }
    //Login
    void loginAccountInFirebase(String email, String password) {
        changeInProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {
                                // Đăng nhập thành công và email đã được xác minh
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Đăng nhập thành công nhưng email chưa được xác minh
                                Utility.showToast(LoginActivity.this, "Email chưa được xác minh.");
                            }
                        } else {
                            // Đăng nhập không thành công
                            Utility.showToast(LoginActivity.this, task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    //Hàm đợi đăng nhập vào
    void changeInProgress(boolean inProgress)
    {
        if(inProgress)
        {
            progressBar.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.VISIBLE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            btn_login.setVisibility(View.VISIBLE);
        }
    }

    //hàm kiểm tra giá trị có khớp với giá trị trên firebafe không?
    boolean validateData(String Email,String PassWord){
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            edt_Email.setError("Email khong hop le!");
            return false;
        }
        if (PassWord.length()<6){
            edt_password.setError("PassWord khong dung!");
            return false;
        }
        return  true;
    }

    private void showPasswordlg() {
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

}