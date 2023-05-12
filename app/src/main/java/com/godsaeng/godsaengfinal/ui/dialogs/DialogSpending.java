package com.godsaeng.godsaengfinal.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.godsaeng.godsaengfinal.R;
import com.godsaeng.godsaengfinal.databinding.DialogResetBinding;
import com.godsaeng.godsaengfinal.databinding.DialogSpendingBinding;
import com.godsaeng.godsaengfinal.ui.fragments.ThirdFragment;

public class DialogSpending extends BaseDialog {

    @Override
    float getWidthFactor() {
        return 0.8f;
    }

    public DialogSpending() { }

    public static DialogSpending newInstance(String what, long value1, long value2){
        DialogSpending fragment = new DialogSpending();
        Bundle bundle = new Bundle();
        bundle.putString("what", what);
        bundle.putLong("value1", value1);
        bundle.putLong("value2", value2);
        fragment.setArguments(bundle);
        return fragment;
    }

    private DialogSpendingBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogSpendingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String what = getArguments().getString("what");
            long lastMonthSpending = getArguments().getLong("value1");
            long thisMonthSpending = getArguments().getLong("value2");
            String lastMonth;
            String thisMonth;
            if (ThirdFragment.CIGARETTE.equals(what)) {
                lastMonth = (lastMonthSpending / 20) + "갑 " + (lastMonthSpending % 20) + "개비";
                thisMonth = (thisMonthSpending / 20) + "갑 " + (thisMonthSpending % 20) + "개비";
            } else {
                lastMonth = lastMonthSpending + "원";
                thisMonth = thisMonthSpending + "원";
            }
            binding.textLastMonth.setText(lastMonth);
            binding.textThisMonth.setText(thisMonth);

            String result;
            int resId;
            long diff;

            if (lastMonthSpending > thisMonthSpending) {
                diff = lastMonthSpending - thisMonthSpending;
                if (ThirdFragment.CIGARETTE.equals(what)) {
                    result = "축하합니다!\n" + (diff / 20) + "갑 " + (diff % 20) + "개비를 줄이셨어요!";
                } else {
                    result = "축하합니다!\n" + diff + "원이나 절약하셨어요!";
                }
                resId = R.drawable.ic_squid;
            } else {
                diff = thisMonthSpending - lastMonthSpending;
                if (ThirdFragment.CIGARETTE.equals(what)) {
                    result = (diff / 20) + "갑 " + (diff % 20) + "개비를 더 피셨네요. 다음달은 줄여봅시다.";
                } else {
                    result = diff + "원을 더 쓰셨네요. 다음달은 줄여봅시다.";
                }
                resId = R.drawable.ic_errors;
            }
            binding.image.setImageResource(resId);
            binding.textResult.setText(result);
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}
