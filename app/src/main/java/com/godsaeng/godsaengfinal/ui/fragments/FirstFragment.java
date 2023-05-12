package com.godsaeng.godsaengfinal.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.godsaeng.godsaengfinal.R;
import com.godsaeng.godsaengfinal.databinding.FragmentFirstBinding;
import com.godsaeng.godsaengfinal.ui.dialogs.DialogTime;
import com.godsaeng.godsaengfinal.ui.utilities.FBAuth;
import com.godsaeng.godsaengfinal.ui.utilities.Util;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Locale;

public class FirstFragment extends BaseFragment {

    private FragmentFirstBinding binding;
    private long startTime;
    private long resumeTime;
    private long pausedTime;
    private long targetTime;
    private long endTime = -1;
    private boolean isPaused = false;
    private boolean isTimerEnd = true;

    private DatabaseReference getGodGoalRef() {
        FirebaseUser user = FBAuth.getInstance().getUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        return reference.child(user.getUid()).child("god_goal");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setMenuListener(binding.menuLayout.menu1, R.id.navigation_first);
        setMenuListener(binding.menuLayout.menu2, R.id.navigation_second);
        setMenuListener(binding.menuLayout.menu3, R.id.navigation_third);
        setMenuListener(binding.menuLayout.menu4, R.id.navigation_fourth);
        setMenuListener(binding.imageLogo, R.id.navigation_second);

        binding.buttonAdd.setOnClickListener(v -> showDialogTime());
        binding.imagePause.setOnClickListener(v -> {
            if (isPaused || isTimerEnd) return;
            isPaused = true;
            pausedTime = System.currentTimeMillis();
            handler.removeCallbacks(mTimer);
            DatabaseReference ref = getGodGoalRef();
            ref.child("paused_time").setValue(pausedTime);
            ref.child("is_paused").setValue(isPaused);
        });

        binding.imageRestart.setOnClickListener(v -> {
            if (!isPaused || isTimerEnd) return;
            isPaused = false;
            resumeTime = System.currentTimeMillis();
            endTime = endTime + (resumeTime - pausedTime);
            DatabaseReference ref = getGodGoalRef();
            ref.child("resume_time").setValue(resumeTime);
            ref.child("end_time").setValue(endTime);
            ref.child("is_paused").setValue(isPaused);
            startTimer();
        });

        getGodGoal();
        return root;
    }

    private void getGodGoal() {
        DatabaseReference ref = getGodGoalRef();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    if ("name".equals(key) && ds.getValue() != null) {
                        String name = (String) ds.getValue();
                        binding.textGoal.setText(name);
                    }
                    if ("start_time".equals(key) && ds.getValue() != null) {
                        startTime = (Long) ds.getValue();
                    }
                    if ("end_time".equals(key) && ds.getValue() != null) {
                        endTime = (Long) ds.getValue();
                    }
                    if ("paused_time".equals(key) && ds.getValue() != null) {
                        pausedTime = (Long) ds.getValue();
                    }
                    if ("resume_time".equals(key) && ds.getValue() != null) {
                        resumeTime = (Long) ds.getValue();
                    }
                    if ("target_time".equals(key) && ds.getValue() != null) {
                        targetTime = (Long) ds.getValue();
                    }
                    if ("is_paused".equals(key) && ds.getValue() != null) {
                        isPaused = (Boolean) ds.getValue();
                    }
                }
                startTimer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        ref.addListenerForSingleValueEvent(eventListener);
    }

    private void showDialogTime() {
        DialogTime dialog = DialogTime.newInstance(false, 0, binding.textGoal.getText().toString());
        dialog.setCompleteListener(new DialogTime.OnCompleteListener() {
            @Override
            public void onComplete(int hour, int minute, String value) {
                uploadValue(hour, minute, value);
            }
        });
        Util.showDialogFragment(requireActivity().getSupportFragmentManager(), "DialogTime", dialog);
    }

    private void uploadValue(int hour, int minute, String value) {
        pausedTime = 0;
        resumeTime = 0;
        startTime = System.currentTimeMillis();
        endTime = startTime + (hour * 60L * 60L + minute * 60L) * 1000;
        targetTime = endTime;
        isPaused = false;
        DatabaseReference ref = getGodGoalRef();
        ref.child("name").setValue(value);
        ref.child("start_time").setValue(startTime);
        ref.child("end_time").setValue(endTime);
        ref.child("paused_time").setValue(pausedTime);
        ref.child("resume_time").setValue(resumeTime);
        ref.child("target_time").setValue(targetTime);
        ref.child("is_paused").setValue(false);
        startTimer();
    }

    private void startTimer() {
        if (endTime < 0) return;
        int remain = (int) (endTime - pausedTime);
        if (remain > 0) {
            isTimerEnd = false;
            String time = msToHour(remain);
            binding.textTime.setText(time);
        } else {
            isTimerEnd = true;
            binding.textTime.setText("00 : 00");
        }
        DecimalFormat df = new DecimalFormat("##%");
        float percent = (1f * (targetTime - startTime) / (endTime - startTime));
        String text = "달성도 " + df.format(percent);
        binding.textPercent.setText(text);
        if (!isPaused) {
            handler.removeCallbacks(mTimer);
            handler.post(mTimer);
        }
    }

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable mTimer = new Runnable() {
        @Override
        public void run() {
            if (binding == null) return;
            int remain = (int) (endTime - System.currentTimeMillis());
            if (remain >= 0) {
                isTimerEnd = false;
                String time = msToHour(remain);
                binding.textTime.setText(time);
                DecimalFormat df = new DecimalFormat("##%");
                float percent = (1f * (targetTime - startTime) / (endTime - startTime));
                String text = "달성도 " + df.format(percent);
                binding.textPercent.setText(text);
                if (!isPaused) handler.postDelayed(this, 1000);
            } else {
                isTimerEnd = true;
                binding.textTime.setText("00:00");
            }
        }
    };

    public String msToHour(long ms){
        long s = (ms/1000) % 60;
        long m = (ms/1000 / 60) % 60;
        long h = (ms/1000 / 60 / 60) % 60;
        String time;
        if (h > 0) {
            time = String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
        } else {
            time = String.format(Locale.getDefault(), "%02d:%02d", m, s);
        }
        return time;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}