package com.godsaeng.godsaengfinal.ui.dialogs;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.godsaeng.godsaengfinal.databinding.DialogResetBinding;
import com.godsaeng.godsaengfinal.ui.utilities.FBAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DialogReset extends BaseDialog {

    @Override
    float getWidthFactor() {
        return 0.95f;
    }

    public DialogReset() { }

    private DialogResetBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogResetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SpannableString spannable = new SpannableString("※경고※ 정말 초기화 하시겠습니까?");
        spannable.setSpan(new RelativeSizeSpan(1.5f), 0, 4, 0);
        spannable.setSpan(new ForegroundColorSpan(0xFFFF0404), 0, 4, 0);
        binding.textTitle.setText(spannable);

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetData();
                dismiss();
            }
        });


    }

    private void resetData() {
        FirebaseUser user = FBAuth.getInstance().getUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(user.getUid()).child("daily_goals").removeValue();
        reference.child(user.getUid()).child("god_goal").removeValue();
        reference.child(user.getUid()).child("settings").child("is_first_run").setValue(true);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}
