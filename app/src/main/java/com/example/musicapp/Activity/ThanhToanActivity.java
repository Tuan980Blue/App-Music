package com.example.musicapp.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.musicapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ThanhToanActivity extends AppCompatActivity {
    Button btnThanhToan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        // Ánh xạ nút thanh toán
        btnThanhToan = findViewById(R.id.btnThanhToan);

        // Xử lý sự kiện khi người dùng nhấn nút thanh toán
        btnThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị hộp thoại xác nhận trước khi thanh toán
                showConfirmationDialog();
            }
        });
    }

    private void updateToPremium() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("isPremium").setValue(true);
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận thanh toán");
        builder.setMessage("Bạn có chắc chắn muốn thanh toán và nâng cấp lên Premium không?");
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Thực hiện thanh toán và cập nhật trạng thái Premium
                updateToPremium();

                // Hiển thị thông báo thành công
                Toast.makeText(ThanhToanActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();

                // Trả về trang chính
                returnToHomePage();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng hộp thoại nếu người dùng chọn hủy
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void returnToHomePage() {


        // Trở về trang chính (MainActivity)
        Intent intent = new Intent(ThanhToanActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Kết thúc Activity hiện tại
    }

}
