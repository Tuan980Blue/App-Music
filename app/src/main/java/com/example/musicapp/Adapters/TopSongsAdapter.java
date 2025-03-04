package com.example.musicapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicapp.Activity.PlaySongActivity;
import com.example.musicapp.Models.Song;
import com.example.musicapp.R;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class TopSongsAdapter extends ArrayAdapter<Song> {

    private Context context;
    private List<Song> songList;

    public TopSongsAdapter(Context context, List<Song> songList) {
        super(context, 0, songList);
        this.context = context;
        // Sắp xếp danh sách theo số lượt nghe giảm dần
        Collections.sort(songList, (song1, song2) -> song2.getListens() - song1.getListens());
        // Giới hạn danh sách chỉ hiển thị 10 bài hát đầu tiên
        this.songList = songList.subList(0, Math.min(songList.size(), 10));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                    R.layout.item_top_song, parent, false);
        }

        // Lấy ra bài hát tại vị trí position
        Song currentSong = getItem(position);

        TextView orderTextView = listItemView.findViewById(R.id.orderTextView);
        orderTextView.setText(String.valueOf(position + 1));

        // Hiển thị thông tin của bài hát trong item_top_song layout
        ImageView avatarsong = listItemView.findViewById(R.id.avatarsong);
        Picasso.get().load(currentSong.getAvatarSong()).into(avatarsong);
        TextView titleTextView = listItemView.findViewById(R.id.titleTextView);
        titleTextView.setText(currentSong.getTitle());

        TextView artistTextView = listItemView.findViewById(R.id.artistTextView);
        artistTextView.setText(currentSong.getArtist());

        TextView listensTextView = listItemView.findViewById(R.id.listensTextView);
        listensTextView.setText("Viewer: " + currentSong.getListens());

        // Thiết lập onClickListener cho mỗi item
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để mở PlaySongActivity
                Intent intent = new Intent(context, PlaySongActivity.class);
                // Truyền thông tin về bài hát được chọn qua Intent
                intent.putExtra("title", currentSong.getTitle());
                intent.putExtra("artist", currentSong.getArtist());
                intent.putExtra("audioUrl", currentSong.getAudioUrl());
                intent.putExtra("id", currentSong.getId());
                // Mở PlaySongActivity
                context.startActivity(intent);
            }
        });
        // Trả về listItemView đã được tạo để hiển thị
        return listItemView;
    }
}
