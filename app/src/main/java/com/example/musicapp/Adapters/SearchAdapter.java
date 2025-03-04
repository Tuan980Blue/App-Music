package com.example.musicapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.Models.Song;
import com.example.musicapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private Context mContext;
    private List<Song> mSongList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    public SearchAdapter(Context context, List<Song> songs, OnItemClickListener listener) {
        this.mContext = context;
        this.mSongList = songs;
        this.listener = listener;
    }

    public void setSongs(List<Song> songs) {
        mSongList = songs;
        notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật lại giao diện
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarsong;
        TextView textViewSongTitle;
        TextView textViewArtist;
        TextView textViewAuthor;
        TextView categoryTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarsong = itemView.findViewById(R.id.avatarsong);
            textViewSongTitle = itemView.findViewById(R.id.textSongTitle);
            textViewArtist = itemView.findViewById(R.id.textArtist);
            textViewAuthor = itemView.findViewById(R.id.textAuthor);
            categoryTextView = itemView.findViewById(R.id.category);

        }
        public void bind(Song song, SearchAdapter.OnItemClickListener listener) {
            // Sự kiện click cho item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(song);
                }
            });
        }
    }
    public SearchAdapter(Context context, List<Song> songList) {
        mContext = context;
        mSongList = songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_category, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = mSongList.get(position);

        // Đặt nội dung cho các TextView
        Picasso.get().load(song.getAvatarSong()).into(holder.avatarsong);
        holder.textViewSongTitle.setText(song.getTitle());
        holder.textViewArtist.setText("Artist: " + song.getArtist());
        holder.textViewAuthor.setText("Author: " + song.getAuthor());
        holder.categoryTextView.setText("Thể loại: " + song.getCategory());

        holder.bind(song, listener);
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }
}
