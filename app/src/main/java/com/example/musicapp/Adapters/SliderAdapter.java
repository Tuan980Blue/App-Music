package com.example.musicapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.Models.SliderModel;
import com.example.musicapp.R;

import java.util.ArrayList;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<SliderModel> sliderModelsList;

    public SliderAdapter(Context context, ArrayList<SliderModel> sliderModelsList) {
        this.context = context;
        this.sliderModelsList = sliderModelsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.slider_item, parent, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SliderModel sliderModel = sliderModelsList.get(position);
        holder.imageView.setImageResource(sliderModel.getImage());
//        holder.textView.setText(sliderModel.getSlideName());
    }

    @Override
    public int getItemCount() {
        return sliderModelsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
//        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slider_image);
//            textView = itemView.findViewById(R.id.slider_title);
        }
    }
}
