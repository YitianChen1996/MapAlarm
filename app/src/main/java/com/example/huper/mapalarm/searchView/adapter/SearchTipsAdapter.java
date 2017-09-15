package com.example.huper.mapalarm.searchView.adapter;

/**
 * Created by Huper on 2017/5/15.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.huper.mapalarm.R;
import com.example.huper.mapalarm.searchView.custom.IOnItemClickListener;

import java.util.ArrayList;

public class SearchTipsAdapter extends  RecyclerView.Adapter<SearchTipsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<LocationBean> tips = new ArrayList<>();
    private IOnItemClickListener iOnItemClickListener;

    public SearchTipsAdapter(Context context, ArrayList<LocationBean> tips) {
        this.context = context;
        this.tips = tips;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_search_tips, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.nameInfo.setText(tips.get(position).name);
        if(tips.get(position).address == null || tips.get(position).address.equals(""))
            holder.addressInfo.setText("暂无详细地址信息");
        else holder.addressInfo.setText(tips.get(position).address);
        holder.tipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iOnItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tips.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameInfo;
        TextView addressInfo;
        LinearLayout tipLayout;
        public MyViewHolder(View view) {
            super(view);
            nameInfo = (TextView) view.findViewById(R.id.tv_item_search_name);
            addressInfo = (TextView) view.findViewById(R.id.tv_item_search_address);
            tipLayout = (LinearLayout) view.findViewById(R.id.tipsLayout);
        }
    }

    public void setOnItemClickListener(IOnItemClickListener iOnItemClickListener) {
        this.iOnItemClickListener = iOnItemClickListener;
    }
}
