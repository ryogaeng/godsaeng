package com.godsaeng.godsaengfinal.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.godsaeng.godsaengfinal.R;
import com.godsaeng.godsaengfinal.databinding.FragmentGodGoalBinding;
import com.godsaeng.godsaengfinal.ui.utilities.FBAuth;
import com.godsaeng.godsaengfinal.ui.utilities.Util;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GodGoalFragment extends BaseFragment {

    private FragmentGodGoalBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGodGoalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadValue();
            }
        });

        return root;
    }

    private void uploadValue() {
        String value = Util.getText(binding.editText);
        if (value.isEmpty()) {
            Toast.makeText(requireContext(), "God Goal을 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseUser user = FBAuth.getInstance().getUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(user.getUid()).child("god_goal").child("name").setValue(value);
        navigate(R.id.navigation_daily_goal);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}