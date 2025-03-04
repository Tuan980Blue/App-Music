package com.example.musicapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapp.Models.Song;
import com.example.musicapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SongCategoryAdapter extends RecyclerView.Adapter<SongCategoryAdapter.ViewHolder> {

    private Context context;
    private List<Song> songs;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    public SongCategoryAdapter(Context context, List<Song> songs, OnItemClickListener listener) {
        this.context = context;
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_category, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song, listener);

        // Hiển thị hình ảnh bài hát (sử dụng Picasso hoặc Glide để tải ảnh từ URL)
        // Picasso.with(context).load(song.getImageUrl()).into(holder.imageView);

        // Đặt nội dung cho các TextView
        holder.textViewSongTitle.setText(song.getTitle());

        Picasso.get().load(song.getAvatarSong()).into(holder.imageView);
        holder.textViewArtist.setText("Artist: " + song.getArtist());
        holder.textViewAuthor.setText("Author: " + song.getAuthor());
        holder.categoryTextView.setText("Thể loại: " + song.getCategory());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật lại giao diện
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewSongTitle;
        TextView textViewArtist;
        TextView textViewAuthor;
        TextView categoryTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.avatarsong);
            textViewSongTitle = itemView.findViewById(R.id.textSongTitle);
            textViewArtist = itemView.findViewById(R.id.textArtist);
            textViewAuthor = itemView.findViewById(R.id.textAuthor);
            categoryTextView = itemView.findViewById(R.id.category);
        }

        public void bind(Song song, OnItemClickListener listener) {
            // Sự kiện click cho item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(song);
                }
            });
        }
    }
}

