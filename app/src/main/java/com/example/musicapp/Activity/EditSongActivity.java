package com.example.musicapp.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicapp.Models.Song;
import com.example.musicapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class EditSongActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 2;
    EditText titleSong, album, artist, author;
    ImageView avatarSong;
    Button doiavatar, buttonHuy, buttonLuu;
    private Uri selectedImageUri;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_song);
        titleSong = findViewById(R.id.titleSong);
        album = findViewById(R.id.album);
        artist = findViewById(R.id.artist);
        author = findViewById(R.id.author);
        avatarSong = findViewById(R.id.avatarSongedit);

        doiavatar = findViewById(R.id.doiavatar);
        buttonHuy = findViewById(R.id.buttonHuy);
        buttonLuu = findViewById(R.id.buttonLuu);

        Intent intent = new Intent();
        intent = getIntent();
        String songId = intent.getStringExtra("songId");


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs").child(songId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Lấy dữ liệu của bài hát từ dataSnapshot và set lên các TextView và ImageView tương ứng
                if (dataSnapshot.exists()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song != null) {
                        titleSong.setText(song.getTitle());
                        album.setText(song.getAlbum());
                        artist.setText(song.getArtist());
                        author.setText(song.getAuthor());
                        Picasso.get().load(song.getAvatarSong()).into(avatarSong);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi khi truy vấn dữ liệu bị hủy
                Toast.makeText(EditSongActivity.this, "Failed to load song data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //click đổi ảnh để chọn ảnh mới
        doiavatar.setOnClickListener(v -> chosseAvatar());

        //xử lí click Hủy để thoát
        buttonHuy.setOnClickListener(v -> finish());

        //xử lý click Lưu để lưu toàn bộ lại nội dung vừa chỉnh sửa
        buttonLuu.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Xác nhận lưu");
            builder.setMessage("Bạn có chắc muốn lưu những thay đổi?");
            builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SaveEditContent(songId);
                }
            });
            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });
    }

    private void chosseAvatar() {
        // Tạo một Intent để mở hộp thoại chọn ảnh từ thư viện
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Lấy URI của ảnh đã chọn
            selectedImageUri = data.getData();

            // Hiển thị ảnh đã chọn trong ImageView
            avatarSong.setImageURI(selectedImageUri);
        }
    }

    private void SaveEditContent(String songId) {
        String newTitle = titleSong.getText().toString();
        String newAlbum = album.getText().toString();
        String newArtist = artist.getText().toString();
        String newAuthor = author.getText().toString();

        // Kiểm tra xem các trường thông tin có rỗng không
        if (TextUtils.isEmpty(newTitle) || TextUtils.isEmpty(newAlbum) || TextUtils.isEmpty(newArtist) || TextUtils.isEmpty(newAuthor)) {
            // Nếu có trường thông tin nào đó rỗng, hiển thị thông báo cho người dùng
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xem đã chọn ảnh mới chưa
        if (selectedImageUri == null) {
            // Nếu không chọn ảnh mới, chỉ cập nhật thông tin văn bản
            updateTextData(songId ,newTitle, newAlbum, newArtist, newAuthor);
        } else {
            // Nếu đã chọn ảnh mới, cần lưu ảnh mới vào Firebase Storage và sau đó cập nhật URL ảnh vào Firebase Database
            uploadImageAndSaveData(songId,newTitle, newAlbum, newArtist, newAuthor);
        }
    }

    private void updateTextData(String songId,String newTitle, String newAlbum, String newArtist, String newAuthor) {
        // Tiến hành cập nhật dữ liệu bài hát trong Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs").child(songId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Kiểm tra xem bài hát có tồn tại không
                if (dataSnapshot.exists()) {
                    // Cập nhật dữ liệu mới cho bài hát
                    dataSnapshot.child("title").getRef().setValue(newTitle);
                    dataSnapshot.child("album").getRef().setValue(newAlbum);
                    dataSnapshot.child("artist").getRef().setValue(newArtist);
                    dataSnapshot.child("author").getRef().setValue(newAuthor);

                    // Hiển thị thông báo thành công và kết thúc activity
                    Toast.makeText(EditSongActivity.this, "Dữ liệu đã cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Hiển thị thông báo lỗi nếu không tìm thấy bài hát
                    Toast.makeText(EditSongActivity.this, "Song not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi khi truy vấn dữ liệu bị hủy
                Toast.makeText(EditSongActivity.this, "Failed to update song data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageAndSaveData(String songId,String newTitle, String newAlbum, String newArtist, String newAuthor) {
        // Lưu ảnh mới vào Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("avatarSinger/" + UUID.randomUUID().toString());
        storageReference.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy URL của ảnh đã tải lên từ Firebase Storage
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();

                                // Tiến hành cập nhật dữ liệu bài hát trong Firebase
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs").child(songId);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Kiểm tra xem bài hát có tồn tại không
                                        if (dataSnapshot.exists()) {
                                            // Cập nhật dữ liệu mới cho bài hát
                                            dataSnapshot.child("title").getRef().setValue(newTitle);
                                            dataSnapshot.child("album").getRef().setValue(newAlbum);
                                            dataSnapshot.child("artist").getRef().setValue(newArtist);
                                            dataSnapshot.child("author").getRef().setValue(newAuthor);
                                            dataSnapshot.child("avatarSong").getRef().setValue(imageUrl);

                                            // Hiển thị thông báo thành công và kết thúc activity
                                            Toast.makeText(EditSongActivity.this, "Song data updated successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            // Hiển thị thông báo lỗi nếu không tìm thấy bài hát
                                            Toast.makeText(EditSongActivity.this, "Song not found", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Xử lý lỗi khi truy vấn dữ liệu bị hủy
                                        Toast.makeText(EditSongActivity.this, "Failed to update song data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                // Xử lý lỗi nếu không thể lấy URL của ảnh từ Firebase Storage
                                Toast.makeText(EditSongActivity.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi nếu không thể tải ảnh lên Firebase Storage
                    Toast.makeText(EditSongActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}