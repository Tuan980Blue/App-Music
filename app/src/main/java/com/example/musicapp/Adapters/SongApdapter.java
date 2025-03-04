package com.example.musicapp.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.Activity.EditSongActivity;
import com.example.musicapp.Activity.PlaySongActivity;
import com.example.musicapp.Models.Song;
import com.example.musicapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import com.squareup.picasso.Picasso;

public class SongApdapter extends RecyclerView.Adapter<SongApdapter.ViewHolder>{
    private Context mContext;
    private List<Song> mSongList;
    public void setSongs(List<Song> songs) {
        mSongList = songs;
        notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật lại giao diện
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView avatarSongad;
        public TextView titleTextView;
        public TextView artistTextView;
        public TextView categoryTextView;
        Button editButton, deleteButton;
        public ViewHolder(View itemView) {
            super(itemView);
            avatarSongad = itemView.findViewById(R.id.avatarSongad);
            titleTextView = itemView.findViewById(R.id.songTitle);
            artistTextView = itemView.findViewById(R.id.songArtist);
            categoryTextView = itemView.findViewById(R.id.songCategory);

            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public SongApdapter(Context context, List<Song> songList) {
        mContext = context;
        mSongList = songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return  new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Song currentSong = mSongList.get(position);
        holder.titleTextView.setText(currentSong.getTitle());
        holder.artistTextView.setText(currentSong.getArtist());
        holder.categoryTextView.setText(currentSong.getCategory());
        Picasso.get().load(currentSong.getAvatarSong()).into(holder.avatarSongad);

        //click nghe nhạc
        // Xử lý sự kiện click trên itemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang trang phát nhạc và truyền ID của bài hát
                Intent intent = new Intent(mContext, PlaySongActivity.class);
                intent.putExtra("title", currentSong.getTitle());
                intent.putExtra("artist", currentSong.getArtist());
                intent.putExtra("audioUrl", currentSong.getAudioUrl());
                intent.putExtra("id", currentSong.getId());
                mContext.startActivity(intent);
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy ID của bài hát được nhấn
                String songId = mSongList.get(position).getId();
                Intent intent = new Intent(mContext, EditSongActivity.class);
                intent.putExtra("songId", songId);

                mContext.startActivity(intent);
            }
        });
        //xu ly su kien xoa
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Song songToDelete = mSongList.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Xác nhận xóa");
                    builder.setMessage("Bạn có chắc chắn muốn xóa bài hát này?");
                    builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("songs").child(songToDelete.getId());
                            databaseReference.removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Xóa thành công, thông báo và cập nhật giao diện
                                            Toast.makeText(mContext, "Đã xóa bài hát", Toast.LENGTH_SHORT).show();
                                            mSongList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, mSongList.size());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Xóa thất bại, xử lý lỗi nếu cần
                                            Log.e("ListSongActivity", "Error deleting song", e);
                                            Toast Toast = null;
                                            Toast.makeText(mContext, "Xóa bài hát thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }
}
