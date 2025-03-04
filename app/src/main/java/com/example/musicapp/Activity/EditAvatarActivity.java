package com.example.musicapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.Models.User;
import com.example.musicapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditAvatarActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView avatar;
    Button changeAvatar;
    Button saveAvatar;
    ImageButton back;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private Uri selectedImageUri;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_avatar);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        back = findViewById(R.id.back);
        avatar = findViewById(R.id.avatar);
        changeAvatar = findViewById(R.id.changeAvatar);
        saveAvatar = findViewById(R.id.saveAvatar);

        // Load avatar từ Firebase nếu có
        loadAvatarFromFirebase();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        saveAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAvatar();
            }
        });
    };

    // Phương thức để tải và hiển thị hình ảnh avatar từ Firebase
    private void loadAvatarFromFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = databaseReference.child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User userData = snapshot.getValue(User.class);
                        if (userData != null) {
                            String avatarUrl = userData.getAvatarUser();
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                // Load hình ảnh từ URL sử dụng Picasso
                                Picasso.get().load(avatarUrl).into(avatar);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu có
                }
            });
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    private void saveAvatar() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Nếu có chọn ảnh mới, upload ảnh lên Firebase Storage và cập nhật đường dẫn vào Realtime Database
            if (selectedImageUri != null) {
                uploadImage(selectedImageUri, userId);
            } else {
                // Nếu không có ảnh mới, có thể thực hiện các bước khác (nếu cần)
                Toast.makeText(EditAvatarActivity.this, "Không có ảnh mới để lưu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(Uri imageUri, String userId) {
        StorageReference imageRef = storageReference.child("avatars/" + userId + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy đường dẫn ảnh sau khi upload thành công
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Cập nhật đường dẫn ảnh vào Realtime Database
                        String imageUrl = uri.toString();
                        databaseReference.child(userId).child("avatarUser").setValue(imageUrl);

                        // Hiển thị ảnh bằng thư viện Picasso
                        Picasso.get().load(imageUrl).into(avatar);

                        Toast.makeText(EditAvatarActivity.this, "Lưu ảnh thành công", Toast.LENGTH_SHORT).show();

                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditAvatarActivity.this, "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Hiển thị hình ảnh ngay sau khi chọn từ thư viện
            Picasso.get().load(selectedImageUri).into(avatar);
        }
    }
}
