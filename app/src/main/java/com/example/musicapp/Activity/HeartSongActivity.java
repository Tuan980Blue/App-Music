package com.example.musicapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.Adapters.HeartSongAdapter;
import com.example.musicapp.Adapters.SongCategoryAdapter;
import com.example.musicapp.Models.Song;
import com.example.musicapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HeartSongActivity extends AppCompatActivity implements HeartSongAdapter.OnItemClickListener{
    TextView numberSong;

    private RecyclerView recyclerView;
    private HeartSongAdapter favoriteSongsAdapter; // Sửa tên adapter thành favoriteSongsAdapter
    private List<Song> favoriteSongs = new ArrayList<>(); // Danh sách bài hát yêu thích

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_song);

        numberSong = findViewById(R.id.numberSong);
        // Khởi tạo RecyclerView và Adapter
        recyclerView = findViewById(R.id.rcHertSong);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteSongsAdapter = new HeartSongAdapter(this, favoriteSongs, this);
        recyclerView.setAdapter(favoriteSongsAdapter);

        // Load danh sách bài hát yêu thích
        loadFavoriteSongs();
    }

    // Hàm load danh sách bài hát yêu thích
    private void loadFavoriteSongs() {
        // Thực hiện truy vấn hoặc lấy dữ liệu từ cơ sở dữ liệu của bạn
        DatabaseReference favoriteSongsRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(getCurrentUserId()).child("favoriteSongs");

        favoriteSongsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot songSnapshot : dataSnapshot.getChildren()) {
                    String songId = songSnapshot.getKey();
                    // Lấy thông tin chi tiết của bài hát từ cơ sở dữ liệu dựa trên songId
                    DatabaseReference songRef = FirebaseDatabase.getInstance().getReference()
                            .child("songs").child(songId);
                    songRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Song song = dataSnapshot.getValue(Song.class);
                                favoriteSongs.add(song);
                                // Gọi notifyDataSetChanged() để thông báo rằng dữ liệu đã thay đổi
                                favoriteSongsAdapter.notifyDataSetChanged();
                                //set tong so bai hát
                                numberSong.setText(String.valueOf(favoriteSongs.size())+ " bài hát");
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Xử lý lỗi nếu có
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    // Phương thức để lấy ID của người dùng hiện tại
    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            // Trường hợp không có người dùng nào đang đăng nhập
            return null;
        }
    }

    @Override
    public void onItemClick(Song song) {
        Intent intent = new Intent(this, PlaySongActivity.class);

        // Truyền thông tin về bài hát sang Activity mới (ví dụ: title, artist, audioUrl)
        intent.putExtra("title", song.getTitle());
        intent.putExtra("artist", song.getArtist());
        intent.putExtra("audioUrl", song.getAudioUrl());
        intent.putExtra("id", song.getId());

        // Truyền danh sách bài hát qua Intent
        // Chuyển đổi danh sách bài hát thành chuỗi JSON
//        Gson gson = new Gson();
//        String jsonSongList = gson.toJson(favoriteSongs);
//        // Lưu chuỗi JSON vào SharedPreferences hoặc vào một tập tin trên thiết bị
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("jsonSongList", jsonSongList);
//        editor.apply();

        startActivity(intent);
    }
    @Override
    public void onDeleteClick(int position) {
        // Lấy vị trí của item trong danh sách
        Song song = favoriteSongs.get(position);
        if (position != RecyclerView.NO_POSITION) {
            // Xóa ID của bài hát khỏi cơ sở dữ liệu Firebase của người dùng
            DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(getCurrentUserId()).child("favoriteSongs");
            userFavoritesRef.child(song.getId()).removeValue();

            // Xóa khỏi danh sách hiển thị
            favoriteSongsAdapter.removeItem(position);
            // Cập nhật số lượng bài hát
            numberSong.setText(String.valueOf(favoriteSongs.size())+ " bài hát");
        }
    }
}