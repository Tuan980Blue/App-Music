package com.example.musicapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.musicapp.Adapters.SongCategoryAdapter;
import com.example.musicapp.Models.Song;
import com.example.musicapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CategorySongsActivity extends AppCompatActivity implements SongCategoryAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private List<Song> songList;
    private SongCategoryAdapter adapter;
    private TextView category1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_songs);

        category1 = findViewById(R.id.category1);
        recyclerView = findViewById(R.id.recyclerView);
        songList = new ArrayList<>();
        adapter = new SongCategoryAdapter((Context) this, songList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Cần thiết để hiển thị danh sách dọc

        // Nhận dữ liệu category từ Intent
        String category = getIntent().getStringExtra("category");

        // Set tiêu đề
        category1.setText(category);

        // Thực hiện truy vấn dữ liệu từ Firebase Database để lấy danh sách các bài hát có category tương ứng
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        databaseReference.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Lấy dữ liệu từ mỗi child và chuyển đổi thành đối tượng Song
                    Song song = snapshot.getValue(Song.class);
                    // Thêm đối tượng Song vào danh sách
                    songList.add(song);
                }
                // Cập nhật danh sách hiển thị trên RecyclerView
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xảy ra lỗi, xử lý lỗi ở đây
                Log.e("CategorySongsActivity", "Error: " + databaseError.getMessage());
            }
        });


        // Khởi tạo và lắng nghe sự kiện tìm kiếm
        SearchView searchView = findViewById(R.id.searchView3);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Xử lý sự kiện khi người dùng nhấn nút tìm kiếm
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);

                if (newText.isEmpty()) {
                    // Nếu query trống, hiển thị tất cả các bài hát một lần nữa
                    adapter.setSongs(songList);
                }
                return false;
            }
        });
    }

    // Phương thức search để tìm kiếm bài hát với từ khóa query
    private void search(String query) {
        List<Song> searchResult = new ArrayList<>();
        for (Song song : songList) {
            if (song.getTitle().toLowerCase().contains(query.toLowerCase())) {
                searchResult.add(song);
            }
        }
        adapter.setSongs(searchResult);
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
//        String jsonSongList = gson.toJson(songList);
//        // Lưu chuỗi JSON vào SharedPreferences hoặc vào một tập tin trên thiết bị
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("jsonSongList", jsonSongList);
//        editor.apply();

        startActivity(intent);
    }
}