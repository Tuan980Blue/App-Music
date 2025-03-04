package com.example.musicapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class HeartSongAdapter extends RecyclerView.Adapter<HeartSongAdapter.SongViewHolder> {

    private List<Song> songList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Song song);
        void onDeleteClick(int position);
    }

    public HeartSongAdapter(Context context, List<Song> songList, OnItemClickListener listener) {
        this.context = context;
        this.songList = songList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_heartsong, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.bind(song, listener);

        // Set thông tin của bài hát vào ViewHolder
        Picasso.get().load(song.getAvatarSong()).into(holder.avatarsong);
        holder.textSongTitle.setText(song.getTitle());
        holder.textArtist.setText(song.getArtist());
        holder.textAuthor.setText(song.getAuthor());
        holder.category.setText(song.getCategory());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarsong;
        public TextView textSongTitle;
        public TextView textArtist;
        public TextView textAuthor;
        public TextView category;
        public ImageView deleteheartsong;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarsong = itemView.findViewById(R.id.avatarsong);
            deleteheartsong = itemView.findViewById(R.id.deleteheartsong);
            textSongTitle = itemView.findViewById(R.id.textSongTitle);
            textArtist = itemView.findViewById(R.id.textArtist);
            textAuthor = itemView.findViewById(R.id.textAuthor);
            category = itemView.findViewById(R.id.category);

            // Đăng ký sự kiện click cho nút deleteheartsong
            deleteheartsong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Lấy vị trí của item trong danh sách
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Hiển thị hộp thoại xác nhận xóa
                        showDeleteConfirmationDialog(position);
                    }
                }
            });
        }
        private void showDeleteConfirmationDialog(int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Xác nhận xóa");
            builder.setMessage("Bạn muốn xóa bài hát này khỏi danh sách yêu thích của bạn?");
            builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Gọi phương thức xử lý sự kiện xóa
                    listener.onDeleteClick(position);
                }
            });
            builder.setNegativeButton("Hủy", null);
            builder.show();
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
    // Phương thức xóa bài hát khỏi danh sách
    public void removeItem(int position) {
        songList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, songList.size());
    }
}