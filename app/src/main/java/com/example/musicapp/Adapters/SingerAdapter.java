package com.example.musicapp.Adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.Models.Singer;
import com.example.musicapp.R;

import java.util.List;

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.SingerViewHolder> {

    private List<Singer> singerList;
    private OnItemClickListener listener;

    // Interface định nghĩa phương thức xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Constructor với thêm một tham số kiểu OnItemClickListener
    public SingerAdapter(List<Singer> singerList, OnItemClickListener listener) {
        this.singerList = singerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SingerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_singer, parent, false);
        return new SingerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingerViewHolder holder, int position) {
        Singer singer = singerList.get(position);
        holder.bind(singer);
    }

    @Override
    public int getItemCount() {
        return singerList.size();
    }

    // ViewHolder cho các item trong RecyclerView
    public class SingerViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarSingerImageView;
        private TextView nameCasiTextView;
        private TextView textSingerTextView;

        public SingerViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarSingerImageView = itemView.findViewById(R.id.avatarSinger);
            nameCasiTextView = itemView.findViewById(R.id.nameCasi);
            textSingerTextView = itemView.findViewById(R.id.textSinger);

            // Xử lý sự kiện click trên itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Kiểm tra xem listener có tồn tại không trước khi gọi phương thức onItemClick
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void bind(Singer singer) {
            // Set dữ liệu của ca sĩ vào các thành phần giao diện
            nameCasiTextView.setText(singer.getName());
            // Nếu cần hiển thị các thông tin khác về ca sĩ, bạn có thể sử dụng các trường dữ liệu khác của đối tượng Singer
        }
    }
}