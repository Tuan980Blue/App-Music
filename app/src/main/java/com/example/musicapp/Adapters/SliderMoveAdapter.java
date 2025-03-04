package com.example.musicapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.Models.SliderMoveModel;
import com.example.musicapp.R;

import java.util.List;

public class SliderMoveAdapter extends RecyclerView.Adapter<SliderMoveAdapter.ViewHolder> {
    private final Context context;
    private final List<SliderMoveModel> sliderModelList;

    public SliderMoveAdapter(Context context, List<SliderMoveModel> sliderModelList){
        this.context = context;
        this.sliderModelList = sliderModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.slider_move, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SliderMoveModel sliderMoveModel = sliderModelList.get(position);
        holder.imageView.setImageResource(sliderMoveModel.getImage());
//        holder.textView.setText(sliderMoveModel.getSlideName());
    }

    @Override
    public int getItemCount() {
        return sliderModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
//        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.sliderMove_image);
//            textView = itemView.findViewById(R.id.textView);
        }
    }
}
