package com.example.musicapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicapp.Models.SharedViewModel;
import com.example.musicapp.Models.User;
import com.example.musicapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class InfoAccountActivity extends AppCompatActivity {
    private SharedViewModel sharedViewModel; // Thay đổi name cùng lúc giữa 2 InfoAccountActivity và UserFragment
    private User user;
    private String email;
    ImageButton back;
    ImageButton viewEmail, hideEmail;
    ImageView avatarOfInfo;
    ImageButton editAvatarOfInfo;
    ImageButton editNameOfInfo, saveNameOfInfo;
    TextView nameOfInfo;
    EditText name,phone;
    TextView tvEmailOfInfo;
    Button btn_Adminpage,btn_adminRequest;
    ImageButton editPhoneNumbers, savePhoneNumbers;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_account);

        // Khi dữ liệu name thay đổi trong ViewModel, cập nhật nameOfInfo(UserFragment)
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.getName().observe(this, newName -> {
            nameOfInfo.setText(newName);
        });

        //Ánh sạ
        back = findViewById(R.id.backInfoActivity);
        //header information
        avatarOfInfo = findViewById(R.id.avatar);
        editAvatarOfInfo = findViewById(R.id.editAvatarOfInfo);
        nameOfInfo = findViewById(R.id.nameOfInfo);
        //infomation && edit
        name = findViewById(R.id.name);
        editNameOfInfo = findViewById(R.id.editNameOfInfo);
        saveNameOfInfo = findViewById(R.id.saveNameOfInfo);

        tvEmailOfInfo = findViewById(R.id.tvEmailOfInfo);
        hideEmail = findViewById(R.id.hideEmail);
        viewEmail = findViewById(R.id.viewEmail);

        phone = findViewById(R.id.phone);
        editPhoneNumbers = findViewById(R.id.editPhoneNumbers);
        savePhoneNumbers = findViewById(R.id.savePhoneNumbers);

        //Admin hide
        btn_Adminpage = findViewById(R.id.btn_adminPage);
        btn_adminRequest = findViewById(R.id.btn_adminRequest);


        // Lấy tham chiếu đến node "users" trên Firebase (Realtime Database)
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();




        if (currentUser != null) {
            String userId = currentUser.getUid();
            usersRef.child(userId).child("isAdmin").getDatabase();
            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        user = snapshot.getValue(User.class); // Gán dữ liệu từ snapshot cho biến user ở cấp độ lớp
                        if(user != null) {
                            Boolean isAdmin = snapshot.child("isAdmin").getValue(Boolean.class);
                            if (isAdmin != null && isAdmin) {
                                // Nếu người dùng là Admin, hiển thị buttonDat
                                btn_Adminpage.setVisibility(View.VISIBLE);
                                btn_adminRequest.setVisibility(View.VISIBLE);
                            }
                            // Lấy URL của avatar từ dữ liệu người dùng
                            String avatarUrl = user.getAvatarUser();
                            // Nếu URL không rỗng
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                // Sử dụng thư viện Picasso để tải và hiển thị ảnh
                                Picasso.get().load(avatarUrl).into(avatarOfInfo);
                            }
                            //Lấy email từ dữ liệu user
                            email = user.getEmail(); // Gán giá trị email cho biến ở cấp độ lớp
                            if(email != null && !email.isEmpty()){
                                int atIndex = email.indexOf('@');
                                if (atIndex > 1) {
                                    String hiddenPart = email.substring(1, atIndex);
                                    String visiblePart = email.substring(atIndex);
                                    String maskedEmail = hiddenPart.replaceAll(".", "*") + visiblePart;
                                    tvEmailOfInfo.setText(maskedEmail);
                                } else {
                                    tvEmailOfInfo.setText(email);
                                }
                            } else {
                                tvEmailOfInfo.setText("NoName@gmail.com");
                            }
                            //Lấy số điện thoại từ user
                            String phoneNumber = user.getPhone();
                            if(phoneNumber != null){
                                phone.setText(phoneNumber);
                            }else {
                                phone.setHint("Phone numbers");
                            }
                            //Lấy tên người dùng từ dữ liệu user
                            String userName = user.getNameUser();
                            name.setText(userName);
                            nameOfInfo.setText(userName); // Cập nhật cả TextView này khi tên thay đổi

                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu có
                }
            });
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editPhoneNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cho phép chỉnh sửa EditText
                phone.setFocusable(true);
                phone.setFocusableInTouchMode(true);
                phone.requestFocus(); // Đặt trạng thái focus để người dùng có thể nhập liệu ngay lập tức nếu cần
                // Hiển thị nút lưu và ẩn nút chỉnh sửa
                savePhoneNumbers.setVisibility(View.VISIBLE);
                editPhoneNumbers.setVisibility(View.GONE);
            }
        });

        savePhoneNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra xem EditText có rỗng hay không
                String newPhone = phone.getText().toString().trim();
                if (newPhone.length() > 14) {
                    // Hiển thị thông báo cho người dùng nếu sdt quá dài
                    Toast.makeText(InfoAccountActivity.this, "Số điện thoại không được quá 14 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                };
                // Ngừng chỉnh sửa EditText
                phone.setFocusable(false);
                phone.setFocusableInTouchMode(false);
                // Ẩn nút lưu và hiển thị nút chỉnh sửa
                savePhoneNumbers.setVisibility(View.GONE);
                editPhoneNumbers.setVisibility(View.VISIBLE);
                // Lưu thông tin vào Firebase hoặc nơi lưu trữ dữ liệu khác nếu cần.
                saveUserInfoToDatabase();
            }
        });


        viewEmail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Hiển thị địa chỉ email đầy đủ
                tvEmailOfInfo.setText(email);
                return true; // Trả về true để chỉ định rằng sự kiện đã được xử lý
            }
        });

        viewEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Hiển thị địa chỉ email đầy đủ khi nhấn giữ
                        tvEmailOfInfo.setText(email);
                        // Ẩn viewEmail và hiện hideEmail
                        viewEmail.setVisibility(View.GONE);
                        hideEmail.setVisibility(View.VISIBLE);
                        return true; // Trả về true để chỉ định rằng sự kiện đã được xử lý
                    case MotionEvent.ACTION_UP:
                        // Thả tay ra
                        // Ẩn hideEmail và hiện lại viewEmail
                        hideEmail.setVisibility(View.GONE);
                        viewEmail.setVisibility(View.VISIBLE);
                        // Ẩn địa chỉ email một lần nữa khi thả tay
                        if (email != null && !email.isEmpty()) {
                            int atIndex = email.indexOf('@');
                            if (atIndex > 1) {
                                String hiddenPart = email.substring(1, atIndex);
                                String visiblePart = email.substring(atIndex);
                                String maskedEmail = hiddenPart.replaceAll(".", "*") + visiblePart;
                                tvEmailOfInfo.setText(maskedEmail);
                            } else {
                                tvEmailOfInfo.setText(email);
                            }
                        } else {
                            tvEmailOfInfo.setText("NoName@gmail.com");
                        }
                        return true;
                }
                return false;
            }
        });


        editAvatarOfInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoAccountActivity.this,EditAvatarActivity.class);
                startActivity(intent);
            }
        });

        editNameOfInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cho phép chỉnh sửa EditText
                name.setFocusable(true);
                name.setFocusableInTouchMode(true);
                name.requestFocus(); // Đặt trạng thái focus để người dùng có thể nhập liệu ngay lập tức nếu cần
                // Hiển thị nút lưu và ẩn nút chỉnh sửa
                saveNameOfInfo.setVisibility(View.VISIBLE);
                editNameOfInfo.setVisibility(View.GONE);
            }
        });

        saveNameOfInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra xem EditText có rỗng hay không
                String newName = name.getText().toString().trim();
                if (newName.isEmpty()) {
                    // Hiển thị thông báo cho người dùng nếu tên trống
                    Toast.makeText(InfoAccountActivity.this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Kiểm tra xem tên có vượt quá 16 ký tự hay không
                if (newName.length() > 16) {
                    // Hiển thị thông báo cho người dùng nếu tên quá dài
                    Toast.makeText(InfoAccountActivity.this, "Tên không được quá 16 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                };
                // Ngừng chỉnh sửa EditText
                name.setFocusable(false);
                name.setFocusableInTouchMode(false);
                // Ẩn nút lưu và hiển thị nút chỉnh sửa
                saveNameOfInfo.setVisibility(View.GONE);
                editNameOfInfo.setVisibility(View.VISIBLE);
                // Lưu thông tin vào Firebase hoặc nơi lưu trữ dữ liệu khác nếu cần.
                saveUserInfoToDatabase();
            }
        });

        btn_Adminpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoAccountActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });

        btn_adminRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoAccountActivity.this, AdminApprovalActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveUserInfoToDatabase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String newName = name.getText().toString().trim(); // Lấy tên mới từ EditText
            String newPhone = phone.getText().toString().trim();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            usersRef.child(userId).child("phone").setValue(newPhone);
            usersRef.child(userId).child("nameUser").setValue(newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Dữ liệu đã được cập nhật thành công trên máy chủ Firebase
                        // Theo dõi sự thay đổi của dữ liệu trên máy chủ Firebase
                        usersRef.child(userId).child("nameUser").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // Lấy giá trị mới của tên từ máy chủ Firebase
                                String updatedName = snapshot.getValue(String.class);
                                // Cập nhật TextView nameOfInfo với tên mới
                                nameOfInfo.setText(updatedName);
                                SharedViewModel.setName(updatedName);// Cập nhập nhanh dữ liệu cho UserFragment thông qua SharedViewModel
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Xử lý lỗi nếu có
                            }
                        });
                    } else {
                        // Xử lý lỗi nếu có
                    };
                }
            });
        }
    }
}
