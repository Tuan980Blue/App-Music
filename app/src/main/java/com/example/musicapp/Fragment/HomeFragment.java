package com.example.musicapp.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.musicapp.Activity.CategorySongsActivity;
import com.example.musicapp.Adapters.ArtistAdapter;
import com.example.musicapp.Adapters.SliderAdapter;
import com.example.musicapp.Adapters.SliderMoveAdapter;
import com.example.musicapp.Adapters.TopSongsAdapter;
import com.example.musicapp.Models.Artist;
import com.example.musicapp.Models.SliderModel;
import com.example.musicapp.Models.SliderMoveModel;
import com.example.musicapp.Models.Song;
import com.example.musicapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {
    CardView loveSong, sadSong, partySong, birthdaySong, rapSong;
    private GridView gridView;
    private List<Song> topSongsList;
    private TopSongsAdapter adapter;
    private List<Artist> artistList;
    private ArtistAdapter artistAdapter;
    RecyclerView recyclerViewAlbum;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Make colors
        TextView textView = view.findViewById(R.id.forYou);
        String text = "#4you";
        SpannableString spannableString = new SpannableString(text);

        int color1 = Color.parseColor("#2196F3");
        int color2 = Color.parseColor("#67B0EA");
        int color3 = Color.parseColor("#B39CFFB2");
        float textSize = 18; // sp
        boolean isBold = true;
        spannableString.setSpan(new ForegroundColorSpan(color1), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(color2), 2, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(color3), 5, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new AbsoluteSizeSpan((int) (textSize * getResources().getDisplayMetrics().scaledDensity)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (isBold) {
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(spannableString);

        //Avatar
        ImageButton imgBtnAvatar = view.findViewById(R.id.avatar);

        imgBtnAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
            }
        });

        // For SliderAdapter
        ViewPager2 slider1 = view.findViewById(R.id.slider1);
        TabLayout sliderIndicator = view.findViewById(R.id.slider_indicator); //SliderIndicator

        ArrayList<SliderModel> sliderModelList = new ArrayList<>();
        sliderModelList.add(new SliderModel(R.drawable.poster1, "Playlist #1"));
        sliderModelList.add(new SliderModel(R.drawable.poster2, "Playlist #2"));
        sliderModelList.add(new SliderModel(R.drawable.poster3, "Playlist #3"));
        sliderModelList.add(new SliderModel(R.drawable.poster4, "Playlist #4"));
        sliderModelList.add(new SliderModel(R.drawable.poster5, "Playlist #5"));

        SliderAdapter sliderAdapter = new SliderAdapter(getContext(), sliderModelList);
        slider1.setAdapter(sliderAdapter);
        //SliderIndicator
        new TabLayoutMediator(sliderIndicator, slider1, (tab, position) -> {
            //
        }).attach();
        //SliderMoveAnimation
        slider1.setPageTransformer((page, position) -> {
            float scale = Math.max(0.95f, 1 - Math.abs(position)); //max(small picture {0f -> 1f}, 1 - Math.abs(position) #= {1})
            page.setScaleY(scale);
        });

        // For SliderMoveAdapter
        ViewPager2 slider2 = view.findViewById(R.id.slider2);

        ArrayList<SliderMoveModel> sliderMoveModelList = new ArrayList<>();
        sliderMoveModelList.add(new SliderMoveModel(R.drawable.slider1, "Slide 1"));
        sliderMoveModelList.add(new SliderMoveModel(R.drawable.slider2, "Slide 2"));
        sliderMoveModelList.add(new SliderMoveModel(R.drawable.slider3, "Slide 3"));
        sliderMoveModelList.add(new SliderMoveModel(R.drawable.slider4, "Slide 4"));


        // Create a copy of the list for the infinite scrolling effect
        ArrayList<SliderMoveModel> infiniteList = new ArrayList<>(sliderMoveModelList);
        infiniteList.addAll(0, sliderMoveModelList.subList(sliderMoveModelList.size() - 2, sliderMoveModelList.size()));
        infiniteList.addAll(sliderMoveModelList.subList(0, 2));

        SliderMoveAdapter sliderMoveAdapter = new SliderMoveAdapter(getActivity(), infiniteList);
        slider2.setAdapter(sliderMoveAdapter);

        // Auto scroll
        int delayInMilliseconds = 3000; // 7 seconds
        final Handler handler = new Handler();
        final Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                // Get the current item
                int currentItem = slider2.getCurrentItem();
                // Check if the last item is reached
                if (currentItem >= infiniteList.size() - 2) {
                    currentItem = 2; // start over
                } else {
                    currentItem++;
                }
                // Set the next item
                slider2.setCurrentItem(currentItem, false);
                // Reset the runnable after a delay
                handler.postDelayed(this, delayInMilliseconds);
            }
        };
        // Execute the Runnable every X milliseconds
        handler.postDelayed(runnableCode, delayInMilliseconds);

        //List ra danh sach theo the loai Nhac
        loveSong = view.findViewById(R.id.loveSong);
        sadSong = view.findViewById(R.id.sadSong);
        partySong = view.findViewById(R.id.partySong);
        birthdaySong = view.findViewById(R.id.birthdaySong);
        rapSong = view.findViewById(R.id.rapSong);

        loveSong.setOnClickListener(v -> goToCategorySongsActivity("Love Songs"));
        sadSong.setOnClickListener(v -> goToCategorySongsActivity("Sad Songs"));
        partySong.setOnClickListener(v -> goToCategorySongsActivity("Party Songs"));
        birthdaySong.setOnClickListener(v -> goToCategorySongsActivity("Birthday Songs"));
        rapSong.setOnClickListener(v -> goToCategorySongsActivity("Rap Songs"));

        //Album
        recyclerViewAlbum = view.findViewById(R.id.recyclerViewAlbum);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewAlbum.setLayoutManager(layoutManager);
        artistList = new ArrayList<>();
        artistAdapter = new ArtistAdapter(artistList, getContext());
        recyclerViewAlbum.setAdapter(artistAdapter);

        loadArtistsFromFirebase();


        //Top 10 Bang Xep Hang
        gridView = view.findViewById(R.id.gridView);
        topSongsList = new ArrayList<>();
        adapter = new TopSongsAdapter(getContext(), topSongsList);
        gridView.setAdapter(adapter);

        loadTopSongs();

        return view;
    }

    private void loadArtistsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                artistList.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                for (DataSnapshot songSnapshot : dataSnapshot.getChildren()) {
                    String artistName = songSnapshot.child("artist").getValue(String.class);
                    String avatarUrl = songSnapshot.child("avatarSong").getValue(String.class); // Lấy URL hình ảnh từ Firebase
                    if (artistName != null && !artistName.isEmpty()) {
                        // Kiểm tra xem nghệ sĩ đã tồn tại trong danh sách chưa
                        boolean artistExists = false;
                        for (Artist artist : artistList) {
                            if (artist.getArtist() != null && artist.getArtist().equals(artistName)) {
                                artistExists = true;
                                break;
                            }
                        }
                        // Thêm nghệ sĩ mới nếu chưa tồn tại trong danh sách
                        if (!artistExists) {
                            Artist artist = new Artist(artistName, avatarUrl);
                            artistList.add(artist);
                        }
                    }
                }
                artistAdapter.notifyDataSetChanged(); // Thông báo cho adapter biết dữ liệu đã thay đổi
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }


    private void loadTopSongs() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Song> allSongs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Song song = snapshot.getValue(Song.class);
                    allSongs.add(song);
                }

                // Sắp xếp danh sách bài hát theo số lượt nghe giảm dần
                Collections.sort(allSongs, (song1, song2) -> song2.getListens() - song1.getListens());

                // Giới hạn danh sách chỉ hiển thị 10 bài hát đầu tiên
                topSongsList.addAll(allSongs.subList(0, Math.min(allSongs.size(), 5)));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });
    }

    //Chon the Loai Nhac
    private void goToCategorySongsActivity(String categorySong) {
        Intent intent = new Intent(requireContext(), CategorySongsActivity.class);
        intent.putExtra("category", categorySong);
        startActivity(intent);
    }
}
