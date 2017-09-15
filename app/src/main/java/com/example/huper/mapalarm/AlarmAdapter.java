package com.example.huper.mapalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

/**
 * Created by Huper on 2017/4/7.
 */

public class AlarmAdapter extends ArrayAdapter<AlarmData> {

    private int mResourceId;
    private boolean check_info;
    private int temp_position;

    public AlarmAdapter(Context context, int textViewResourceId, List<AlarmData> objects) {
        super(context, textViewResourceId, objects);
        this.mResourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view;
        temp_position = position;
        final AlarmData item = getItem(position);
        ViewHolder viewHolder = new ViewHolder();
        LayoutInflater minflater = LayoutInflater.from(getContext());
        if (convertView == null) {
            view = minflater.inflate(mResourceId, null);
            viewHolder.address = (TextView) view.findViewById(R.id.Address);
            viewHolder.todo = (TextView) view.findViewById(R.id.DoWhat);
            viewHolder.switchButton = (SwitchButton) view.findViewById(R.id.Switch);
            view.setTag(viewHolder);
        }
        else {
            view = convertView;
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.address.setText(item.address);
        viewHolder.todo.setText(item.todo);
        viewHolder.switchButton.setOnCheckedChangeListener(null);

        if(item.state && !viewHolder.switchButton.isChecked()){
            viewHolder.switchButton.setCheckedImmediately(true);
        }
        else if(!item.state && viewHolder.switchButton.isChecked()){
            viewHolder.switchButton.setCheckedImmediately(false);
        }

        viewHolder.switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //Toast.makeText(getContext(),"已激活",Toast.LENGTH_SHORT).show();
                    item.state = true;
                    if (MainActivity.db.updateActive(MainActivity.db ,item, item.state)){
                        Toast.makeText(getContext(), "已开启此事件提醒", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "修改失败", Toast.LENGTH_SHORT).show();
                    }
                }

                else{
                    item.state = false;
                    if (MainActivity.db.updateActive(MainActivity.db ,item, item.state)){
                        Toast.makeText(getContext(), "已关闭此事件提醒", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "修改失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return view;
    }

    public class ViewHolder{
        TextView address;
        TextView todo;
        SwitchButton switchButton;
    }
}
