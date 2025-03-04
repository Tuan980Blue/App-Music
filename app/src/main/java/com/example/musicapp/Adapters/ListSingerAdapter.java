package com.example.musicapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicapp.Models.ListSingerModel;
import com.example.musicapp.R;

import java.util.List;

public abstract class ListSingerAdapter extends BaseAdapter {
    private Context mContext;
    private int mLayout;
    private List<ListSingerModel> mlist_ca_si;

    ListSingerAdapter(Context Context, int Layout, List<ListSingerModel> list_ca_sis){
        mContext=Context;
        mLayout=Layout;
        mlist_ca_si=list_ca_sis;
    }

    @Override
    public int getCount(){
        return mlist_ca_si.size();
    }

    @Override
    public Object getItem(int i) {return null;}

    @Override
    public long getItemId(int i) {return 0;}

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater!=null;
        view=inflater.inflate(mLayout, null);

        ImageView IMG_ca_si = (ImageView) view.findViewById(R.id.IMG_ca_si);
        IMG_ca_si.setImageResource(mlist_ca_si.get(i).Hinh_ca_si);

        TextView txtCa_si=(TextView) view.findViewById(R.id.ten_ca_si);
        txtCa_si.setText(mlist_ca_si.get(i).ten);

        return view;
    }
}
