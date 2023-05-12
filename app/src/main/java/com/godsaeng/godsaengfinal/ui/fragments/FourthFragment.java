package com.godsaeng.godsaengfinal.ui.fragments;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.godsaeng.godsaengfinal.R;
import com.godsaeng.godsaengfinal.databinding.FragmentFourthBinding;
import com.godsaeng.godsaengfinal.ui.dialogs.DialogReset;
import com.godsaeng.godsaengfinal.ui.utilities.Util;

public class FourthFragment extends BaseFragment {

    private FragmentFourthBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFourthBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setMenuListener(binding.menuLayout.menu1, R.id.navigation_first);
        setMenuListener(binding.menuLayout.menu2, R.id.navigation_second);
        setMenuListener(binding.menuLayout.menu3, R.id.navigation_third);
        setMenuListener(binding.menuLayout.menu4, R.id.navigation_fourth);

        SpannableString spannable = new SpannableString("완벽해요★");
        spannable.setSpan(new ForegroundColorSpan(0xFF1AFF00), 4, 5, 0);
        binding.textRating.setText(spannable);

        binding.textReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showDialogFragment(requireActivity().getSupportFragmentManager(), "DialogReset", new DialogReset());
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