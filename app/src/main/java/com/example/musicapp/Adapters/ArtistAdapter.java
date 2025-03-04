package com.example.musicapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.Models.Artist;
import com.example.musicapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    private List<Artist> artistList;
    private Context context;

    public ArtistAdapter(List<Artist> artistList, Context context) {
        this.artistList = artistList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artist artist = artistList.get(position);
        holder.bind(artist);
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewArtistName;
        ImageView imageViewAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewArtistName = itemView.findViewById(R.id.nameSingerAlbum);
            imageViewAvatar = itemView.findViewById(R.id.backgroundSingerAlbum);
        }

        public void bind(Artist artist) {
            textViewArtistName.setText(artist.getArtist());
            // Call method to fetch image data from Firebase
            getImageDataFromDatabase(artist.getAvatarSong(), imageViewAvatar);
        }
    }

    private void getImageDataFromDatabase(String avatarUrl, ImageView imageViewAvatar) {
        // Sử dụng thư viện Picasso để tải hình ảnh từ URL và hiển thị nó trong ImageView
        Picasso.get().load(avatarUrl).into(imageViewAvatar);
    }
}
