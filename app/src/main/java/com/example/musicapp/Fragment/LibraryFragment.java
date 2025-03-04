package com.example.musicapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.musicapp.Activity.HeartSongActivity;
import com.example.musicapp.Activity.SingerSongsActivity;
import com.example.musicapp.Adapters.SingerAdapter;
import com.example.musicapp.Models.Singer;
import com.example.musicapp.Models.Song;
import com.example.musicapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout listsong_uathich;
    private SingerAdapter singerAdapter;
    private List<Singer> singerList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewLibrary);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)); // Đổi LinearLayoutManager thành chiều ngang

        singerList = new ArrayList<>();
        ViewPager viewPager = view.findViewById(R.id.viewPager);

        PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View itemView;
                if (position == 0) {
                    itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_playlist, container, false);
                } else {
                    itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_album, container, false);
                }
                container.addView(itemView);
                return itemView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        };

        viewPager.setAdapter(adapter);

        TextView playlistTextView = view.findViewById(R.id.playlistTextView);
        TextView albumTextView = view.findViewById(R.id.albumTextView);
        View playlistUnderline = view.findViewById(R.id.playlistUnderline);
        View albumUnderline = view.findViewById(R.id.albumUnderline);
        playlistTextView.setTextColor(getResources().getColor(R.color.black));
        albumTextView.setTextColor(getResources().getColor(R.color.gray));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Không cần thiết phải thực hiện hành động gì ở đây
            }

            @Override
            public void onPageSelected(int position) {
                // Nếu trang được chọn là trang đầu tiên (Playlist)
                if (position == 0) {
                    // Hiển thị dấu gạch dưới chữ "Playlist" và ẩn dấu gạch dưới chữ "Album"
                    playlistUnderline.setVisibility(View.VISIBLE);
                    albumUnderline.setVisibility(View.INVISIBLE);

                    playlistTextView.setTextColor(getResources().getColor(R.color.black));
                    albumTextView.setTextColor(getResources().getColor(R.color.gray));
                } else { // Nếu trang được chọn là trang thứ hai (Album)
                    // Hiển thị dấu gạch dưới chữ "Album" và ẩn dấu gạch dưới chữ "Playlist"
                    playlistUnderline.setVisibility(View.INVISIBLE);
                    albumUnderline.setVisibility(View.VISIBLE);

                    playlistTextView.setTextColor(getResources().getColor(R.color.gray));
                    albumTextView.setTextColor(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Không cần thiết phải thực hiện hành động gì ở đây
            }
        });

        playlistTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị dấu gạch dưới chữ "Playlist" và ẩn dấu gạch dưới chữ "Album"
                playlistUnderline.setVisibility(View.VISIBLE);
                albumUnderline.setVisibility(View.INVISIBLE);

                playlistTextView.setTextColor(getResources().getColor(R.color.black));
                albumTextView.setTextColor(getResources().getColor(R.color.gray));

                // Chuyển đến trang đầu tiên của ViewPager (page 0)
                viewPager.setCurrentItem(0);
            }
        });

        albumTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị dấu gạch dưới chữ "Album" và ẩn dấu gạch dưới chữ "Playlist"
                playlistUnderline.setVisibility(View.INVISIBLE);
                albumUnderline.setVisibility(View.VISIBLE);

                playlistTextView.setTextColor(getResources().getColor(R.color.gray));
                albumTextView.setTextColor(getResources().getColor(R.color.black));

                // Chuyển đến trang thứ hai của ViewPager (page 1)
                viewPager.setCurrentItem(1);
            }
        });

        // Tạo một implementation của interface OnItemClickListener
        SingerAdapter.OnItemClickListener itemClickListener = new SingerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Xử lý sự kiện click trên danh sách các ca sĩ
                Singer clickedSinger = singerList.get(position);
                Intent intent = new Intent(requireContext(), SingerSongsActivity.class);
                intent.putExtra("singerName", clickedSinger.getName());
                startActivity(intent);
            }
        };
        // Khởi tạo adapter và thiết lập listener
        singerAdapter = new SingerAdapter(singerList, itemClickListener);
        recyclerView.setAdapter(singerAdapter);

        // Load dữ liệu về các ca sĩ yêu thích
        loadFavoriteSingers();

        // Tạo sự kiện onClickListener cho phần tử mà khi nhấn vào sẽ chuyển sang HeartSongFragment
        listsong_uathich = view.findViewById(R.id.listsong_uathich);
        listsong_uathich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), HeartSongActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadFavoriteSingers() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(currentUser.getUid()).child("favoriteSongs");

            userFavoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot songSnapshot : dataSnapshot.getChildren()) {
                        String songId = songSnapshot.getKey();
                        // Truy vấn thông tin của ca sĩ từ cơ sở dữ liệu songs với songId
                        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference()
                                .child("songs").child(songId);
                        songRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Song song = dataSnapshot.getValue(Song.class);
                                    String singerName = song.getArtist(); // Giả sử có một phương thức để lấy tên ca sĩ
                                    // Thêm ca sĩ vào danh sách nếu chưa có trong danh sách
                                    if (!isSingerExist(singerName)) {
                                        Singer singer = new Singer(singerName);
                                        singerList.add(singer);
                                        singerAdapter.notifyDataSetChanged(); // Cập nhật giao diện người dùng
                                    }
                                }
                            }

                            //kiểm tra xem một ca sĩ đã tồn tại trong danh sách singerList hay chưa
                            private boolean isSingerExist(String singerName) {
                                for (Singer singer : singerList) {
                                    if (singer.getName().equals(singerName)) {
                                        return true; // Trả về true nếu ca sĩ đã tồn tại trong danh sách
                                    }
                                }
                                return false; // Trả về false nếu ca sĩ chưa tồn tại trong danh sách
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
    }
}
