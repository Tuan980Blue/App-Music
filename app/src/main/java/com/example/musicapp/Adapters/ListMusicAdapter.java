package com.example.musicapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.musicapp.Models.ListMusicModel;
import com.example.musicapp.R;

import java.util.List;

public abstract class ListMusicAdapter extends BaseAdapter {
    private final Context mContext;
    private final int mLayout;
    private final List<ListMusicModel> mlist_nhac;

    ListMusicAdapter(Context context, int Layout, List<ListMusicModel>list_nhac){
        mContext=context;
        mLayout=Layout;
        mlist_nhac=list_nhac;
    }

    @Override
    public int getCount(){
        return mlist_nhac.size();
    }

    @Override
    public Object getItem(int i) {return null;}

    @Override
    public long getItemId(int i) {return 0;}

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(mLayout, null);

        TextView Ten = (TextView) view.findViewById(R.id.ten_bai_hat);
        Ten.setText(mlist_nhac.get(i).ten);

        TextView timeout = (TextView) view.findViewById(R.id.sum_time);
        timeout.setText(mlist_nhac.get(i).timeout);

        return view;
    }
}
