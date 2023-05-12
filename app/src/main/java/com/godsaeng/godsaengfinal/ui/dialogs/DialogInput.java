package com.godsaeng.godsaengfinal.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.godsaeng.godsaengfinal.databinding.DialogInputBinding;
import com.godsaeng.godsaengfinal.databinding.DialogSpendingBinding;
import com.godsaeng.godsaengfinal.ui.fragments.ThirdFragment;
import com.godsaeng.godsaengfinal.ui.utilities.Util;

public class DialogInput extends BaseDialog {

    @Override
    float getWidthFactor() {
        return 0.8f;
    }

    private OnCompleteListener mListener;


    public interface OnCompleteListener {
        void onComplete(long value);
    }

    public void setCompleteListener(OnCompleteListener listener) {
        mListener = listener;
    }

    public static DialogInput newInstance(String what, long value){
        DialogInput fragment = new DialogInput();
        Bundle bundle = new Bundle();
        bundle.putString("what", what);
        bundle.putLong("value", value);
        fragment.setArguments(bundle);
        return fragment;
    }

    public DialogInput() { }

    private DialogInputBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogInputBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) return;
        String what = getArguments().getString("what");
        long value = getArguments().getLong("value");

        if (ThirdFragment.CIGARETTE.equals(what)) {
            long v1 = value / 20;
            long v2 = value % 20;
            binding.editText1.setText(String.valueOf(v1));
            binding.editText2.setText(String.valueOf(v2));
            binding.linear2.setVisibility(View.VISIBLE);
            binding.label1.setText("갑");
        } else {
            binding.editText1.setText(String.valueOf(value));
        }

        binding.button.setOnClickListener(v -> {
            if (mListener != null) {
                long value1 = Util.parseLong(Util.getText(binding.editText1));
                if (ThirdFragment.CIGARETTE.equals(what)) {
                    long value2 = Util.parseLong(Util.getText(binding.editText2));
                    mListener.onComplete(value1 * 20 + value2);
                } else {
                    mListener.onComplete(value1);
                }
            }
            dismiss();
        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}
