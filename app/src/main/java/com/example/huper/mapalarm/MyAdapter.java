package com.example.huper.mapalarm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

/**
 * Created by Huper on 2017/4/15.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{

    private List<AlarmData> Info;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public MyAdapter(Context context , List<AlarmData> Info){
        this.context = context;
        this.Info = Info;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AlarmData item =  Info.get(position);
        holder.address.setText(item.address);
        holder.todo.setText(item.todo);
        holder.switchButton.setOnCheckedChangeListener(null);
        holder.locateIcon.setImageResource(R.drawable.ic_room_orange_a100_18dp);

        if(item.state){
            holder.switchButton.setCheckedImmediately(true);
            holder.enabledAnim.setVisibility(View.VISIBLE);
            holder.locateIcon.setVisibility(View.GONE);
            holder.tempDis.setText(showDis(item.nowdis));
        }
        else{
            holder.switchButton.setCheckedImmediately(false);
            holder.tempDis.setText("未开启");
            holder.enabledAnim.setVisibility(View.GONE);
            holder.locateIcon.setVisibility(View.VISIBLE);
        }


        holder.switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //Toast.makeText(context,"已激活",Toast.LENGTH_SHORT).show();
                    holder.enabledAnim.setVisibility(View.VISIBLE);
                    holder.locateIcon.setVisibility(View.GONE);
                    holder.tempDis.setText(showDis(item.nowdis));
                    item.state = true;
                    notifyItemChanged(position);
                    if (MainActivity.db.updateActive(MainActivity.db ,item, item.state)){
                        Toast.makeText(context, "已开启此事件提醒", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
                    }
                    if (BackgroundLocation.getActiveEventNum()>=0){
                        MainActivity.backgroundLocation.startLocation();
                    }
                }

                else{
                    holder.tempDis.setText("未开启");
                    holder.enabledAnim.setVisibility(View.GONE);
                    holder.locateIcon.setVisibility(View.VISIBLE);
                    item.state = false;
                    notifyItemChanged(position);
                    if (MainActivity.db.updateActive(MainActivity.db ,item, item.state)){
                        Toast.makeText(context, "已关闭此事件提醒", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
                    }
                    if (BackgroundLocation.getActiveEventNum()==0){
                        MainActivity.backgroundLocation.stopLocation();
                    }
                }
            }
        });

        if(mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }
        if(mOnItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(holder.itemView,position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return Info == null ? 0 : Info.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView address;
        TextView todo;
        TextView tempDis;
        ImageView locateIcon;
        AVLoadingIndicatorView enabledAnim;
        SwitchButton switchButton;

        public ViewHolder(View view){
            super(view);
            address = (TextView) view.findViewById(R.id.Address);
            todo = (TextView) view.findViewById(R.id.DoWhat);
            switchButton = (SwitchButton) view.findViewById(R.id.Switch);
            locateIcon= (ImageView) view.findViewById(R.id.image);
            tempDis = (TextView) view.findViewById(R.id.TempDis);
            enabledAnim = (AVLoadingIndicatorView) view.findViewById(R.id.anim);
        }
    }

    public String showDis(double initDis){
        String ret;
        if (initDis<0){
            return "努力定位中(｡・`ω´･)";
        }
        else if (initDis<10000&&initDis>=0){
            java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
            return "目前距离："+df.format(initDis)+" 米";
        }else if (initDis>=10000){
            java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
            return "目前距离："+df.format(initDis/1000.0)+" 公里";
        }
        return "";
    }
}