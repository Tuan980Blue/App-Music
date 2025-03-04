package com.example.musicapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PlaySongActivity extends AppCompatActivity {
    TextView titleTextView;
    TextView artistTextView;
    ImageView timSong;
    ImageView turnloa;
    private ImageButton playButton ;
    ImageView avatarsong;
    ImageButton backSong;
    ImageButton skipSong;
    ImageView backActivit;
    private SeekBar seekBar;
    private TextView currentTime, totalTime;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private boolean hearting = false;
    private boolean turnonVolume = false;
    private boolean isPlaying = false;
    private int currentSongIndex = 0;
    private float currentRotation = 0;

    private MediaPlayer mediaPlayer1;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        backActivit = findViewById(R.id.backActivit);
        timSong = findViewById(R.id.timSong);
        turnloa = findViewById(R.id.turnloa);
        // Khởi tạo mediaPlayer
        mediaPlayer = new MediaPlayer();

        avatarsong = findViewById(R.id.avatarsong);
        playButton = findViewById(R.id.playSong);
        backSong = findViewById(R.id.backSong);
        skipSong = findViewById(R.id.skipSong);
        seekBar = findViewById(R.id.seekBar);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String songTitle = intent.getStringExtra("title");
        String songArtist = intent.getStringExtra("artist");
        String songAudioUrl = intent.getStringExtra("audioUrl");
        String songId = intent.getStringExtra("id");
        // Truy cập vào Firebase Realtime Database để tăng số lượt nghe của bài hát lên 1
        increaseSongListens(songId);

        // Nhận songList từ Intent
        // Lấy chuỗi JSON từ SharedPreferences hoặc từ tập tin trên thiết bị
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String jsonSongList = ((SharedPreferences) preferences).getString("jsonSongList", "");
//        // Chuyển đổi chuỗi JSON thành danh sách bài hát
//        Gson gson = new Gson();
//        Type listType = new TypeToken<List<Song>>(){}.getType();
//        List<Song> songList = gson.fromJson(jsonSongList, listType);

        // Cập nhật giao diện người dùng với thông tin của bài hát
        titleTextView = findViewById(R.id.nameSong);
        titleTextView.setText(songTitle);
        artistTextView = findViewById(R.id.nameCasi);
        artistTextView.setText(songArtist);

        // Truy cập vào Firebase Realtime Database để lấy URL hình ảnh của bài hát dựa trên ID bài hát
        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference().child("songs").child(songId);

        // Lắng nghe sự thay đổi của dữ liệu trong Firebase
        songRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Kiểm tra xem dữ liệu có tồn tại không
                if (dataSnapshot.exists()) {
                    // Lấy URL hình ảnh từ dữ liệu Firebase
                    String songImageUrl = dataSnapshot.child("avatarSong").getValue(String.class); // Thay "imageUrl" bằng key thích hợp trong cơ sở dữ liệu của bạn

                    // Load hình ảnh vào ImageView hiện tại bằng Picasso
                    Picasso.get().load(songImageUrl).into(avatarsong);
                } else {
                    // Xử lý trường hợp không tìm thấy dữ liệu
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình truy xuất dữ liệu từ Firebase
            }
        });
        /// Phát nhạc từ URL được truyền qua Intent
        try {
            mediaPlayer.setDataSource(songAudioUrl);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Thanh chạy nhạc (điều chỉnh thời gian nhạc)
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Tính toán thời gian phát mới dựa trên vị trí của SeekBar
                    int newPosition = (int) (((float) progress / 100) * mediaPlayer.getDuration());
                    // Di chuyển bài hát đến thời gian phát mới
                    mediaPlayer.seekTo(newPosition);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Tạm dừng việc cập nhật thanh trạng thái khi người dùng bắt đầu di chuyển seekbar
                handler.removeCallbacks(updateSeekBarRunnable);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateSeekBar();
            }
        });

        // Tạo một tham chiếu đến nút yêu thích của bài hát hiện tại trong cơ sở dữ liệu
        DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(getCurrentUserId()).child("favoriteSongs").child(songId);

        // Lắng nghe sự thay đổi trạng thái yêu thích của bài hát từ cơ sở dữ liệu
        userFavoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Kiểm tra xem trạng thái của bài hát
                if (dataSnapshot.exists() && dataSnapshot.getValue(Boolean.class)) {
                    // Bài hát đã được yêu thích
                    hearting = true;
                    timSong.setImageResource(R.drawable.redheart);
                } else {
                    // Bài hát không được yêu thích
                    hearting = false;
                    timSong.setImageResource(R.drawable.timsong);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
        //goi ham
        timSong.setOnClickListener(v -> convertHeart(songId));

        //tat loa nhac
        turnloa.setOnClickListener(v -> convertVolume());

        // Play button click listener
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    mediaPlayer.start();
                    Drawable stopSongDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.stopsongsvg);
                    playButton.setImageDrawable(stopSongDrawable);
                    isPlaying = true;
                    // Áp dụng animation
                    Animation rotateAnimation = AnimationUtils.loadAnimation(PlaySongActivity.this, R.anim.rotate_anim);
                    avatarsong.startAnimation(rotateAnimation); // Áp dụng animation cho ImageView avatarsong
                    updateSeekBar();
                } else {
                    mediaPlayer.pause(); // Tạm dừng phát nhạc
                    Drawable playSongDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.playsongsvg);
                    playButton.setImageDrawable(playSongDrawable);
                    isPlaying = false;
                    // Dừng animation
                    avatarsong.clearAnimation();
                }
            }
        });

        backSong.setOnClickListener(v -> backToSong());
        skipSong.setOnClickListener(v -> skipToNextSong());


        //Back lai trang
        backActivit.setOnClickListener(v -> finish());
    }

    //Quay lại bài nhạc cũ
    private void backToSong() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    //Chuyển bài nhạc:
    private void skipToNextSong() {
        DatabaseReference songsRef = FirebaseDatabase.getInstance().getReference("songs");
        songsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Kiểm tra xem có dữ liệu nào hay không
                if (dataSnapshot.exists()) {
                    // Lấy số lượng bài hát trong danh sách
                    long songCount = dataSnapshot.getChildrenCount();

                    // Tạo một số ngẫu nhiên từ 0 đến số lượng bài hát - 1
                    Random random = new Random();
                    long randomIndex = random.nextInt((int) songCount);

                    // Duyệt qua danh sách và lấy bài hát ở vị trí ngẫu nhiên
                    long currentIndex = 0;
                    for (DataSnapshot songSnapshot : dataSnapshot.getChildren()) {
                        if (currentIndex == randomIndex) {
                            // Lấy dữ liệu của bài hát tại vị trí ngẫu nhiên
                            Song randomSong = songSnapshot.getValue(Song.class);

                            // Thực hiện xử lý để phát bài hát mới này, ví dụ:
                            playNewSong(randomSong);
                            break;
                        }
                        currentIndex++;
                    }
                } else {
                    // Hiển thị thông báo nếu không có bài hát nào trong danh sách
                    Toast.makeText(PlaySongActivity.this, "No songs found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi khi truy vấn dữ liệu bị hủy
                Toast.makeText(PlaySongActivity.this, "Failed to load songs: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Phương thức để phát bài hát mới
    private void playNewSong(Song song) {
        // Dừng phát bài hát hiện tại (nếu có)
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        // Tạo một MediaPlayer mới để phát bài hát mới
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(song.getAudioUrl());
            mediaPlayer.prepare();
            mediaPlayer.stop();

            // Cập nhật giao diện hoặc thực hiện các xử lý khác khi bắt đầu phát bài hát mới
            updateUIForNewSong(song);
        } catch (IOException e) {
            // Xử lý lỗi nếu không thể phát bài hát
            e.printStackTrace();
            Toast.makeText(this, "Failed to play song", Toast.LENGTH_SHORT).show();
        }
    }
    //Cập nhật lại giao diện
    private void updateUIForNewSong(Song song) {
        Intent intent = new Intent(PlaySongActivity.this, PlaySongActivity.class);
        intent.putExtra("title", song.getTitle());
        intent.putExtra("artist", song.getArtist());
        intent.putExtra("audioUrl", song.getAudioUrl());
        intent.putExtra("id", song.getId());
        startActivity(intent);
    }


    //Tang so luot nghe cho bai nhac
    private void increaseSongListens(String songId) {
        // Truy cập vào Firebase Realtime Database
        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference().child("songs").child(songId);

        // Đọc số lượt nghe hiện tại
        songRef.child("listens").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Lấy số lượt nghe hiện tại
                Integer currentListens = dataSnapshot.getValue(Integer.class);

                // Nếu giá trị không null
                if (currentListens != null) {
                    // Tăng số lượt nghe lên 1
                    currentListens++;

                    // Cập nhật lại giá trị số lượt nghe trong Firebase Realtime Database
                    songRef.child("listens").setValue(currentListens);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình truy xuất dữ liệu từ Firebase Realtime Database
            }
        });
    }

    private void convertVolume() {
        if (!turnonVolume) {
            // Tắt loa
            mediaPlayer.setVolume(0, 0); // Tắt âm thanh hoàn toàn
            turnloa.setImageResource(R.drawable.offloa);
            turnonVolume = true;
        } else {
            // Bật loa
            mediaPlayer.setVolume(1, 1); // Bật âm thanh
            turnloa.setImageResource(R.drawable.turnloa);
            turnonVolume = false;
        }
    }

    private void convertHeart(String songId) {
        if (!hearting) {
            // Nếu bài hát chưa được thêm vào danh sách yêu thích
            timSong.setImageResource(R.drawable.redheart);
            Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            hearting = true;

            // Lưu ID của bài hát vào cơ sở dữ liệu Firebase của người dùng
            DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(getCurrentUserId()).child("favoriteSongs");
            userFavoritesRef.child(songId).setValue(true);
        } else {
            // Nếu bài hát đã được thêm vào danh sách yêu thích
            timSong.setImageResource(R.drawable.timsong);
            Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
            hearting = false;

            // Xóa ID của bài hát khỏi cơ sở dữ liệu Firebase của người dùng
            DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(getCurrentUserId()).child("favoriteSongs");
            userFavoritesRef.child(songId).removeValue();
        }
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

    /////////////////////////////////
    private final Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            // Kiểm tra null trước khi sử dụng mediaPlayer
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int totalDuration = mediaPlayer.getDuration();

                // Tính toán giá trị của seekbar dựa trên thời gian hiện tại của bài hát
                int progress = (int)(((float)currentPosition / totalDuration) * 100);
                seekBar.setProgress(progress);

                // Cập nhật hiển thị thời gian hiện tại và tổng thời gian của bài hát
                currentTime.setText(getFormattedTime(currentPosition));
                totalTime.setText(getFormattedTime(totalDuration));
            }

            // Lặp lại việc cập nhật sau mỗi 1 giây
            handler.postDelayed(this, 700);
        }
    };

    // Gọi Runnable trong phương thức updateSeekBar
    private void updateSeekBar() {
        handler.postDelayed(updateSeekBarRunnable, 700);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release media player resources
        }
        handler.removeCallbacksAndMessages(null); // Remove any pending messages from the handler
    }

    // Helper method to convert milliseconds to formatted time (mm:ss)
    @SuppressLint("DefaultLocale")
    private String getFormattedTime(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(minutes);
        return  String.format("%02d:%02d", minutes, seconds);
    }
}
