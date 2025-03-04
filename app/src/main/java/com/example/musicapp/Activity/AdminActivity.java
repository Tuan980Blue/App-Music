package com.example.musicapp.Activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.Models.Song;
import com.example.musicapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;

public class AdminActivity extends AppCompatActivity {
    private static final int PICK_AUDIO_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    private Button btnOpenAudioFiles, btnUpload;
    private TextView tVSongFileSelect;
    private EditText titleSong, album, artist, author;
    private ImageView imageSong;
    private Button chosseavatar;
    private Spinner spinner;
    private ProgressBar progressBar;

    private Uri audioUri;
    private StorageReference storageReference;
    private StorageReference imageStorageReference;
    Button btn_listsong;
    private Uri selectedImageUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        storageReference = FirebaseStorage.getInstance().getReference("audio_files");
        imageStorageReference = FirebaseStorage.getInstance().getReference().child("avatarSinger");

        btnOpenAudioFiles = findViewById(R.id.openAudioFiles);
        btnUpload = findViewById(R.id.btnUpload);
        tVSongFileSelect = findViewById(R.id.tVSongFileSelect);
        titleSong = findViewById(R.id.title_song);
        album = findViewById(R.id.album);
        artist = findViewById(R.id.artist);
        author = findViewById(R.id.author);
        imageSong = findViewById(R.id.imageSong);
        chosseavatar = findViewById(R.id.chosseavatar);
        spinner = findViewById(R.id.spinner);
        progressBar = findViewById(R.id.progressBar);
        btn_listsong = findViewById(R.id.btn_listsong);

        Spinner spinner = findViewById(R.id.spinner);
        List<String> category = new ArrayList<>();
        category.add("Love Songs");
        category.add("Sad Songs");
        category.add("Party Songs");
        category.add("Birthday Songs");
        category.add("Rap Songs");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,category);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        btnOpenAudioFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAudioFiles();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFileToFirebase();
            }
        });

        //chọn ảnh cho bài hát
        chosseavatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btn_listsong.setOnClickListener(v-> xemlistsong());
    }
    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Chọn ảnh"), PICK_IMAGE_REQUEST);


    }
    private void xemlistsong() {
        Intent intent = new Intent(AdminActivity.this, ListSongActivity.class);
        startActivity(intent);
    }

    private void openAudioFiles() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //chọn file nhạc
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            audioUri = data.getData();
            tVSongFileSelect.setText(audioUri.getLastPathSegment());
        }

        //chọn ảnh
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Hiển thị hình ảnh ngay sau khi chọn từ thư viện
            Picasso.get().load(selectedImageUri).into(imageSong);
        }
    }

    private void uploadFileToFirebase() {
        if (selectedImageUri != null && audioUri != null) {
            /// Kiểm tra xem tất cả các trường thông tin đã được điền đầy đủ chưa
            if (TextUtils.isEmpty(titleSong.getText().toString()) ||
                    TextUtils.isEmpty(album.getText().toString()) ||
                    TextUtils.isEmpty(artist.getText().toString()) ||
                    TextUtils.isEmpty(author.getText().toString()) ||
                    spinner.getSelectedItem() == null ||
                    selectedImageUri == null ||
                    audioUri == null) {
                // Thông báo lỗi nếu có trường thông tin nào đó còn thiếu
                Toast.makeText(AdminActivity.this, "Vui lòng điền đầy đủ thông tin bài hát.", Toast.LENGTH_LONG).show();
            } else {
                // Hiển thị ProgressBar khi bắt đầu tải lên
                progressBar.setVisibility(View.VISIBLE);

                // Tạo một StorageReference cho tệp ảnh và lưu vào thư mục "avatarSinger"
                final StorageReference imageFileReference = imageStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(selectedImageUri));

                // Tạo một StorageReference cho tệp nhạc và lưu vào thư mục "audio_files"
                final StorageReference audioFileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(audioUri));

                // Tiến hành tải lên ảnh
                UploadTask imageUploadTask = imageFileReference.putFile(selectedImageUri);
                imageUploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Lấy URL của ảnh
                            imageFileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri imageUri) {
                                    // Tiến hành tải lên file nhạc
                                    UploadTask audioUploadTask = audioFileReference.putFile(audioUri);
                                    audioUploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Hiển thị tiến trình upload
                                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                            progressBar.setProgress((int) progress);
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            // Ẩn ProgressBar khi tải lên hoàn thành hoặc gặp lỗi
                                            progressBar.setVisibility(View.GONE);

                                            if (task.isSuccessful()) {
                                                // Lấy URL của nhạc
                                                audioFileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri audioUri) {
                                                        // Lưu thông tin bài hát vào Firebase Database
                                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");
                                                        String songId = databaseReference.push().getKey();
                                                        // Tất cả các trường thông tin đã được điền đầy đủ, tiến hành tạo đối tượng Song và tải lên
                                                        // Tạo một đối tượng Song
                                                        Song song = new Song(songId,
                                                                titleSong.getText().toString(),
                                                                album.getText().toString(),
                                                                artist.getText().toString(),
                                                                author.getText().toString(),
                                                                spinner.getSelectedItem().toString(),
                                                                audioUri.toString(), // Sử dụng uri của hình ảnh
                                                                imageUri.toString()); // Sử dụng uri của file nhạc

                                                        // Lưu thông tin bài hát vào Firebase Database
                                                        databaseReference.child(songId).setValue(song);

                                                        // Thông báo upload thành công
                                                        Toast.makeText(AdminActivity.this, "Upload thành công.", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            } else {
                                                // Upload file nhạc thất bại
                                                Toast.makeText(AdminActivity.this, "Upload audio file failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            // Upload ảnh thất bại
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AdminActivity.this, "Upload image failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
        else {
                Toast.makeText(this, "Vui lòng chọn đủ file nhạc và file ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    private String getFileExtension(Uri audioUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }

}