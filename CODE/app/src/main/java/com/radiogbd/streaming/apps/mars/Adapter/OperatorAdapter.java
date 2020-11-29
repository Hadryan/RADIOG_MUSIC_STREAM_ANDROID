package com.radiogbd.streaming.apps.mars.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.R;

import java.util.List;

public class OperatorAdapter extends BaseAdapter {

    Context context;
    Utility utility;
    String[] operators;

    public OperatorAdapter(Context context, String[] operators){
        this.context = context;
        utility = new Utility(this.context);
        this.operators = operators;
    }

    @Override
    public int getCount() {
        return operators.length;
    }

    @Override
    public Object getItem(int position) {
        return operators[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_operator, null);
        }
        TextView tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
        View view = (View)convertView.findViewById(R.id.v_line);
        utility.setFont(tvTitle);
        tvTitle.setText(operators[position]);
        /*if(position==(operators.length-1)){
            view.setVisibility(View.GONE);
        }
        else{
            view.setVisibility(View.VISIBLE);
        }*/
        return convertView;
    }
}
