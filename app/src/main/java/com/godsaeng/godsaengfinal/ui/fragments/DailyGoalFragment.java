package com.godsaeng.godsaengfinal.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.godsaeng.godsaengfinal.R;
import com.godsaeng.godsaengfinal.databinding.FragmentGodGoalBinding;

public class DailyGoalFragment extends BaseFragment {

    private FragmentGodGoalBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGodGoalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.text1.setText("갓생을 살기 위해 매일매일 해보자.");
        binding.text2.setText("매일매일 규칙적인 삶!\nDaily goal을 설정해보세요");
        binding.text3.setText("당신의 Daily Goal은\n일정표에 ■ ← 이 색상으로 표시됩니다.");
        binding.buttonNext.setText("→ 일별 Daily goal 설정하러 가기");
        binding.editText.setVisibility(View.INVISIBLE);
        binding.text3.setTextColor(requireContext().getColor(R.color.daily_goal));


        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle(0);
                bundle.putInt("position", 0);
                navigate(R.id.navigation_add_daily, bundle);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}