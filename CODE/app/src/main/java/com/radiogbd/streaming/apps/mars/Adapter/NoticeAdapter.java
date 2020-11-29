package com.radiogbd.streaming.apps.mars.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.NoticeModel;
import com.radiogbd.streaming.apps.mars.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NoticeAdapter extends BaseAdapter {

    Context context;
    Utility utility;
    List<NoticeModel> noticeModels;

    public NoticeAdapter(Context context, List<NoticeModel> noticeModels){
        this.context = context;
        utility = new Utility(this.context);
        this.noticeModels = noticeModels;
    }

    @Override
    public int getCount() {
        return noticeModels.size();
    }

    @Override
    public Object getItem(int position) {
        return noticeModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_notice, null);
        }
        LinearLayout llNotice = (LinearLayout)convertView.findViewById(R.id.ll_notice);
        TextView tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
        View vLine = (View)convertView.findViewById(R.id.v_line);
        ImageView ivImage = (ImageView)convertView.findViewById(R.id.iv_image);
        TextView tvBody = (TextView)convertView.findViewById(R.id.tv_body);
        utility.setFonts(new View[]{tvTitle, tvBody});
        if(noticeModels.get(position).getUrl_image().length()>0) {
            ivImage.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(noticeModels.get(position).getUrl_image())
                    .into(ivImage);
        }
        else{
            ivImage.setVisibility(View.GONE);
        }
        llNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noticeModels.get(position).getUrl_redirection().length()>0){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(noticeModels.get(position).getUrl_redirection()));
                    context.startActivity(i);
                }
            }
        });
        tvTitle.setText(utility.getLangauge().equals("bn")? Html.fromHtml(noticeModels.get(position).getTitle_bn()):noticeModels.get(position).getTitle());
        tvBody.setText(utility.getLangauge().equals("bn")? Html.fromHtml(noticeModels.get(position).getBody_bn()):noticeModels.get(position).getBody());
        return convertView;
    }
}
