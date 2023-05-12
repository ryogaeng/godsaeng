package com.godsaeng.godsaengfinal.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.godsaeng.godsaengfinal.databinding.FragmentScheduleBinding;
import com.godsaeng.godsaengfinal.databinding.VhScheduleBinding;
import com.godsaeng.godsaengfinal.ui.dialogs.DialogTime;
import com.godsaeng.godsaengfinal.ui.utilities.FBAuth;
import com.godsaeng.godsaengfinal.ui.utilities.Util;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class ScheduleFragment extends BaseFragment {

    private FragmentScheduleBinding binding;
    private String dayOfWeek;
    private Adapter adapter;
    private boolean isEditMode = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() != null) {
            dayOfWeek = getArguments().getString("DAY_OF_WEEK");
        }
        adapter = new Adapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        binding.buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTime(0, "");
            }
        });

        binding.buttonEdit.setOnClickListener(v -> {
            isEditMode = true;
            binding.buttonEdit.setVisibility(View.INVISIBLE);
            binding.buttonClose.setVisibility(View.VISIBLE);
        });

        binding.buttonClose.setOnClickListener(v -> {
            isEditMode = false;
            binding.buttonEdit.setVisibility(View.VISIBLE);
            binding.buttonClose.setVisibility(View.INVISIBLE);
        });

        binding.buttonDone.setOnClickListener(v -> navigateUp());

        getSchedule();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        binding = null;
    }

    private void getSchedule() {
        FirebaseUser user = FBAuth.getInstance().getUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dailyRef = rootRef.child(user.getUid()).child("daily_goals").child(dayOfWeek);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> map = new HashMap<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String hour = ds.getKey();
                    String activity = (String) ds.getValue();
                    map.put(hour, activity);
                }
                if (adapter != null) adapter.submitDailyGoals(map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        dailyRef.addListenerForSingleValueEvent(eventListener);
    }

    private void showDialogTime(int hour, String value) {
        DialogTime dialog = DialogTime.newInstance(true, hour, value);
        dialog.setCompleteListener(new DialogTime.OnCompleteListener() {
            @Override
            public void onComplete(int hour, int minute, String value) {
                String h = String.valueOf(hour);
                uploadValue(h, value);
                adapter.addDailyGoal(h, value);
            }
        });
        Util.showDialogFragment(requireActivity().getSupportFragmentManager(), "DialogTime", dialog);
    }

    private void uploadValue(String hour, String value) {
        FirebaseUser user = FBAuth.getInstance().getUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(user.getUid()).child("daily_goals").child(dayOfWeek)
                .child(hour).setValue(value);
    }

    private void onItemClicked(int hour, String value) {
        if (!isEditMode) return;
        showDialogTime(hour, value);
    }

    @SuppressLint("NotifyDataSetChanged")
    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        private final WeakReference<ScheduleFragment> fragment;
        private final int heightNormal = Util.dpToPx(40f);
        private final int heightLast = Util.dpToPx(20f);
        private final int colorDaily = 0xFF95DE64;
        private final int colorGod = 0xFF389E0D;
        private final int colorNormal = 0xFFD9F7BE;

        Adapter(ScheduleFragment secondFragment) {
            fragment = new WeakReference<>(secondFragment);
        }

        private final HashMap<String, String> dailyGoalMap = new HashMap<>();

        private void submitDailyGoals(HashMap<String, String> map) {
            dailyGoalMap.putAll(map);
            notifyDataSetChanged();
        }

        private void addDailyGoal(String hour, String value) {
            dailyGoalMap.put(hour, value);
            notifyDataSetChanged();
        }

        private final String[] list = new String[] { "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일" };

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(VhScheduleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return 25;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final VhScheduleBinding binding;
            public ViewHolder(VhScheduleBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                binding.getRoot().setOnClickListener(v -> {
                    int hour = getAdapterPosition();
                    String key = String.valueOf(hour);
                    String value = dailyGoalMap.get(key);
                    if (value == null) value = "";
                    if (fragment.get() != null) fragment.get().onItemClicked(hour, value);
                });
            }

            private void bind(int position) {
                String key = String.valueOf(position);
                String value = dailyGoalMap.get(key);
                if (value != null) {
                    binding.textSchedule.setText(value);
                    binding.textSchedule.setBackgroundColor(colorDaily);
                } else {
                    binding.textSchedule.setText("");
                    binding.textSchedule.setBackgroundColor(colorNormal);
                }
                if (position > 0) {
                    String prevKey = String.valueOf(position - 1);
                    int color = (dailyGoalMap.get(prevKey) == null) ? colorNormal : colorDaily;
                    binding.background.setBackgroundColor(color);
                }

                String time = (position < 10) ? "0" + position : String.valueOf(position);
                binding.textTime.setText(time);

                int visibility = (position % 2 == 0) ? View.VISIBLE : View.INVISIBLE;
                binding.textTime.setVisibility(visibility);
                binding.line.setVisibility(visibility);

                int textVisibility = (position == 24) ? View.INVISIBLE : View.VISIBLE;
                binding.textSchedule.setVisibility(textVisibility);

                int height = (position == 24) ? heightLast : heightNormal;
                Util.setLayoutSize(binding.constraint, -1, height);
            }
        }
    }
}