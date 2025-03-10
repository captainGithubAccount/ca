package com.you.company.rtcpgvd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;

import com.you.company.rtcpgvd.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntervalImageSaveLayout extends LinearLayout {


    private NumberPicker minutePicker;
    private NumberPicker secondsPicker;

    private int maxValue;

    private String minute;
    private String second;

    private int minuteOldVal = 0;

    private int secondOldVal = 0;

    private boolean minuteScrolled = false;

    private boolean secondScrolled = false;


    private List<String> displayTimes = new ArrayList<String>(Arrays.asList("00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"
    ));

    public IntervalImageSaveLayout(Context context) {
        super(context);
        init(context);
    }

    public IntervalImageSaveLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IntervalImageSaveLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_interval_image_saver,this,false);
        minutePicker = view.findViewById(R.id.picker_minutes);
        secondsPicker = view.findViewById(R.id.picker_seconds);

        setBackgroundResource(R.drawable.spinner_background);

        minutePicker.setDisplayedValues(displayTimes.toArray(new String[]{}));
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(displayTimes.size()-1);

        secondsPicker.setDisplayedValues(displayTimes.toArray(new String[]{}));

        minutePicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                minuteScrolled = true;
            }
        });

        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(displayTimes.size()-1);
        secondsPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                 secondScrolled = true;
            }
        });

        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.i("capture","oldVal: "+ oldVal + "newVal: "+ newVal);

                minute = displayTimes.get(newVal);
            }
        });


        secondsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.i("capture","oldVal: "+ oldVal + "newVal: "+ newVal);
                second = displayTimes.get(newVal);
            }
        });
        addView(view);
    }

    public String getTime() {
        StringBuilder stringBuilder = new StringBuilder();
        if(minuteScrolled) {
            stringBuilder.append(minute);
        } else {
            stringBuilder.append(displayTimes.get(minuteOldVal));
        }
        stringBuilder.append(":");
        if(secondScrolled) {
            stringBuilder.append(second);
        } else {
            stringBuilder.append(displayTimes.get(secondOldVal));
        }
        return stringBuilder.toString();
    }
}
