package com.you.company.rtcpgvd;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<String> {

    private ArrayList<String> mDatas;

    public SpinnerAdapter(@NonNull Context context, ArrayList<String> datas) {
        super(context, R.layout.custom_spinner_item,datas);
        mDatas = datas;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view =  super.getView(position, convertView, parent);
        TextView textView = view.findViewById(R.id.txt);
        textView.setTextColor(Color.BLACK);
        textView.setText(mDatas.get(position));
        textView.setTextSize(18);
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable @org.jetbrains.annotations.Nullable View convertView, @NonNull @NotNull ViewGroup parent) {
        View view =  super.getDropDownView(position, convertView, parent);
        TextView textView = view.findViewById(R.id.txt);
        textView.setTextColor(Color.BLACK);
        textView.setText(mDatas.get(position));
        textView.setTextSize(18);
        return view;
    }
}
