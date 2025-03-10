package com.you.company.rtcpgvd.view;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.you.company.rtcpgvd.R;

import org.jetbrains.annotations.NotNull;

public class IntervalDialog extends DialogFragment implements View.OnClickListener{

    private EditText editText_h;
    private EditText editText_m;
    private EditText editText_s;
    private LinearLayout h_layout;
    private LinearLayout m_layout;
    private LinearLayout s_layout;
    private Button initBtn;
    private ImageView ic_close;

    public interface OnTimeSetFinish{
        void onTimeDone(String str);
    }

    OnTimeSetFinish timeSetListener;

    private InputFilter.LengthFilter lenFilter = new InputFilter.LengthFilter(2);


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_interval_task, null);
        editText_h = view.findViewById(R.id.edit_hour);
        editText_m = view.findViewById(R.id.edit_minute);
        editText_s = view.findViewById(R.id.edit_second);
        initBtn = view.findViewById(R.id.initial_btn);

        editText_h.setFilters(new InputFilter[] {
            lenFilter,
        });

        editText_m.setFilters(new InputFilter[]{
                lenFilter,
        });

        editText_s.setFilters(new InputFilter[] {
             lenFilter
        });

        editText_s.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s != null && s.toString().length() >0) {
                    if(Integer.parseInt(s.toString().trim()) > 59) {
                        editText_s.setText("59");
                    }
                }
            }
        });
        editText_m.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s != null && s.toString().length() >0) {
                    if(Integer.parseInt(s.toString().trim()) > 59) {
                        editText_m.setText("59");
                    }
                }
            }
        });

        editText_h.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s != null && s.toString().length() >0) {
                    if(Integer.parseInt(s.toString().trim()) > 23) {
                        editText_h.setText("23");
                    }
                }
            }
        });

        h_layout = view.findViewById(R.id.edit_layout_hour);
        m_layout = view.findViewById(R.id.edit_layout_minute);
        s_layout = view.findViewById(R.id.edit_layout_second);
        ic_close = view.findViewById(R.id.ic_interval_close);

        ic_close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        editText_h.requestFocus();

        initBtn.setOnClickListener(this);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.initial_btn) {
            if(timeSetListener != null) {
                String str_h = editText_h.getText().toString().trim();
                String str_m = editText_m.getText().toString().trim();
                String str_s = editText_s.getText().toString().trim();
                int h = Integer.parseInt(str_h.equals("")? "0": str_h);
                int m = Integer.parseInt(str_m.equals("")? "0": str_m);
                int s = Integer.parseInt(str_s.equals("")? "0" :str_s);
                String formattedTime = String.format("%02d:%02d:%02d", h, m, s );
                timeSetListener.onTimeDone(formattedTime);
            }
            this.dismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void show(@NonNull @NotNull FragmentManager manager, @Nullable @org.jetbrains.annotations.Nullable String tag) {
        super.show(manager, tag);
    }

    public void setTimeSetListener(OnTimeSetFinish setFinish) {
        timeSetListener = setFinish;
    }
}
