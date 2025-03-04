package com.example.musicapp.Activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.musicapp.Adapters.SingerSongsAdapter;
import com.example.musicapp.Adapters.TopSongsAdapter;
import com.example.musicapp.Models.Song;
import com.example.musicapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SingerSongsActivity extends AppCompatActivity implements SingerSongsAdapter.OnItemClickListener {
    TextView tvsingerName;

    private RecyclerView rcSingerSong;
    private SingerSongsAdapter singerSongsAdapter; // Sửa tên adapter thành favoriteSongsAdapter
    private List<Song> songList = new ArrayList<>(); // Danh sách bài hát yêu thích
    private String singerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_songs);

        tvsingerName = findViewById(R.id.singerName);
        // Khởi tạo RecyclerView và Adapter
        rcSingerSong = findViewById(R.id.rcSingerSong);
        rcSingerSong.setLayoutManager(new LinearLayoutManager(this));
        singerSongsAdapter = new SingerSongsAdapter(this, songList, this);
        rcSingerSong.setAdapter(singerSongsAdapter);

        //Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        singerName = intent.getStringExtra("singerName");

        // Hiển thị tên ca sĩ
        tvsingerName.setText(singerName);

        // Load danh sách bài hát của ca sĩ lên
        loadSingerSongs();
    }

    // Hàm load danh sách bài hát của ca sĩ có tên đó
    private void loadSingerSongs() {
        // Thực hiện truy vấn hoặc lấy dữ liệu từ cơ sở dữ liệu của bạn
        DatabaseReference singerSongsRef = FirebaseDatabase.getInstance().getReference()
                .child("songs");
        Query query = singerSongsRef.orderByChild("artist").equalTo(singerName);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                songList.clear(); // Xóa danh sách bài hát cũ
                for (DataSnapshot songSnapshot : dataSnapshot.getChildren()) {
                    Song song = songSnapshot.getValue(Song.class);
                    songList.add(song);
                }
                // Gọi notifyDataSetChanged() để thông báo rằng dữ liệu đã thay đổi
                singerSongsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }

    @Override
    public void onItemClick(Song song) {
        Intent intent = new Intent(this, PlaySongActivity.class);

        // Truyền thông tin về bài hát sang Activity mới (ví dụ: title, artist, audioUrl)
        intent.putExtra("title", song.getTitle());
        intent.putExtra("artist", song.getArtist());
        intent.putExtra("audioUrl", song.getAudioUrl());
        intent.putExtra("id", song.getId());

        startActivity(intent);
    }
}