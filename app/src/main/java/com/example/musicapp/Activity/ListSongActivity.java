package com.example.musicapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.musicapp.Adapters.SongApdapter;
import com.example.musicapp.Models.Song;
import com.example.musicapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListSongActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SongApdapter mSongAdapter;
    private List<Song> mSongList;
    private DatabaseReference mSongsRef;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_song);

        // Khởi tạo RecyclerView
        mRecyclerView = findViewById(R.id.songRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter và danh sách bài hát
        mSongList = new ArrayList<>();
        mSongAdapter = new SongApdapter(this, mSongList);
        mRecyclerView.setAdapter(mSongAdapter);

        // Lấy tham chiếu đến node "songs" trên Firebase
        mSongsRef = FirebaseDatabase.getInstance().getReference("songs");

        // Lắng nghe sự thay đổi dữ liệu từ Firebase
        mSongsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Xóa danh sách bài hát hiện tại
                mSongList.clear();

                // Lặp qua tất cả các bài hát và thêm vào danh sách
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Song song = snapshot.getValue(Song.class);
                    mSongList.add(song);
                }

                // Cập nhật giao diện người dùng
                mSongAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi đọc dữ liệu thất bại
                Toast.makeText(ListSongActivity.this, "Failed to load songs", Toast.LENGTH_SHORT).show();
            }
        });

        // Khởi tạo và lắng nghe sự kiện tìm kiếm
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Xử lý sự kiện khi người dùng nhấn nút tìm kiếm
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Nếu query trống, hiển thị tất cả các bài hát một lần nữa
                    mSongAdapter.setSongs(mSongList);
                }
                return false;
            }
        });
    }

    // Phương thức search để tìm kiếm bài hát với từ khóa query
    private void search(String query) {
        List<Song> searchResult = new ArrayList<>();
        for (Song song : mSongList) {
            if (song.getTitle().toLowerCase().contains(query.toLowerCase())) {
                searchResult.add(song);
            }
        }
        mSongAdapter.setSongs(searchResult);
    }
}