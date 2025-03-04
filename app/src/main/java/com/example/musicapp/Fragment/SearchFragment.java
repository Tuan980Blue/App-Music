package com.example.musicapp.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.Activity.PlaySongActivity;
import com.example.musicapp.Adapters.SearchAdapter;
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

public class SearchFragment extends Fragment {

    private RecyclerView rv1;
    private SearchAdapter mSearchAdapter;
    private List<Song> mSongList;
    private DatabaseReference mSongsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        rv1 = view.findViewById(R.id.rv1);
        rv1.setHasFixedSize(true);
        rv1.setLayoutManager(new LinearLayoutManager(getContext()));

        mSongList = new ArrayList<>();
        // Trong SearchFragment
        mSearchAdapter = new SearchAdapter(getContext(), mSongList, new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song) {
                Intent intent = new Intent(getActivity(), PlaySongActivity.class);

                // Truyền thông tin về bài hát sang Activity mới (ví dụ: title, artist, audioUrl)
                intent.putExtra("title", song.getTitle());
                intent.putExtra("artist", song.getArtist());
                intent.putExtra("audioUrl", song.getAudioUrl());
                intent.putExtra("id", song.getId());

                // Truyền danh sách bài hát qua Intent
                // Chuyển đổi danh sách bài hát thành chuỗi JSON
//                Gson gson = new Gson();
//                String jsonSongList = gson.toJson(mSongList);
//                // Lưu chuỗi JSON vào SharedPreferences hoặc vào một tập tin trên thiết bị
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putString("jsonSongList", jsonSongList);
//                editor.apply();

                startActivity(intent);
            }
        });
        rv1.setAdapter(mSearchAdapter);

        mSongsRef = FirebaseDatabase.getInstance().getReference("songs");
        mSongsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSongList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Song song = snapshot.getValue(Song.class);
                    mSongList.add(song);
                }
                mSearchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load songs", Toast.LENGTH_SHORT).show();
            }
        });

        SearchView searchView = view.findViewById(R.id.sv1);
        searchView.setQueryHint("Bạn muốn nghe nhạc ?");

        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Kích hoạt chế độ tìm kiếm khi bất kỳ vị trí nào của SearchView được bấm vào
                searchView.onActionViewExpanded();
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);

                if (newText.isEmpty()) {
                    // Nếu query trống, hiển thị tất cả các bài hát một lần nữa
                    mSearchAdapter.setSongs(mSongList);
                }
                return false;
            }
        });

        return view;
    }

    private void search(String query) {
        List<Song> searchResult = new ArrayList<>();
        for (Song song : mSongList) {
            if (song.getTitle().toLowerCase().contains(query.toLowerCase())) {
                searchResult.add(song);
            }
        }
        mSearchAdapter.setSongs(searchResult);
    }
}
