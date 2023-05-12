package com.godsaeng.godsaengfinal.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.godsaeng.godsaengfinal.R;
import com.godsaeng.godsaengfinal.databinding.FragmentAddDailyGoalBinding;
import com.godsaeng.godsaengfinal.databinding.VhAddDailyBinding;
import com.godsaeng.godsaengfinal.ui.dialogs.DialogTime;
import com.godsaeng.godsaengfinal.ui.utilities.Constants;
import com.godsaeng.godsaengfinal.ui.utilities.FBAuth;
import com.godsaeng.godsaengfinal.ui.utilities.Util;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class AddDailyGoalFragment extends BaseFragment {

    private FragmentAddDailyGoalBinding binding;
    private Adapter adapter;
    private int position;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddDailyGoalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() != null) {
            position = getArguments().getInt("position");
        }
        String text = Constants.DAY_OF_WEEKS_KR[position] + "마다 규칙적으로 할 일 설정"; //389E0D
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(0xFF389E0D), 0, 3, 0);
        binding.text3.setText(spannable);
        String next;
        if (position < 6) {
            next = "→ " + Constants.DAY_OF_WEEKS_KR[position + 1] + " 설정하러 가기";
        } else {
            next = "완료";
        }
        binding.buttonNext.setText(next);

        adapter = new Adapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        int nextPosition = position + 1;
        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextPosition == 7) {
                    popBackStack(R.id.navigation_login);
                    popBackStack(R.id.navigation_god_goal);
                    popCurrentBackStack();
                    navigate(R.id.navigation_first);
                } else {
                    Bundle bundle = new Bundle(0);
                    bundle.putInt("position", nextPosition);
                    navigate(R.id.navigation_add_daily, bundle);
                }
            }
        });

        binding.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTime();
            }
        });

        return root;
    }

    private void showDialogTime() {
        DialogTime dialog = DialogTime.newInstance(true, 0, "");
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
        reference.child(user.getUid()).child("daily_goals").child(Constants.DAY_OF_WEEKS[position])
                .child(hour).setValue(value);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        binding = null;
    }

    private void onItemClicked(int position) {

    }

    @SuppressLint("NotifyDataSetChanged")
    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        private final WeakReference<AddDailyGoalFragment> fragment;
        private final HashMap<String, String> dailyGoalMap = new HashMap<>();

        Adapter(AddDailyGoalFragment secondFragment) {
            fragment = new WeakReference<>(secondFragment);
        }

        private void addDailyGoal(String hour, String value) {
            dailyGoalMap.put(hour, value);
            notifyDataSetChanged();
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(VhAddDailyBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
            private final VhAddDailyBinding binding;
            public ViewHolder(VhAddDailyBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                binding.getRoot().setOnClickListener(v -> {
                    if (fragment.get() != null) fragment.get().onItemClicked(getAdapterPosition());
                });
            }

            private void bind(int position) {

                String key = String.valueOf(position);
                String value = dailyGoalMap.get(key);
                if (value != null) {
                    binding.textSchedule.setText(value);
                } else {
                    binding.textSchedule.setText("");
                }

                String time = (position < 10) ? "0" + position : String.valueOf(position);
                binding.textTime.setText(time);

                int visibility = (position % 2 == 0) ? View.VISIBLE : View.INVISIBLE;
                binding.textTime.setVisibility(visibility);
                binding.line.setVisibility(visibility);

                visibility = (position == 24) ? View.INVISIBLE : View.VISIBLE;
                binding.textSchedule.setVisibility(visibility);

                visibility = (position == 0) ? View.INVISIBLE : View.VISIBLE;
                binding.background.setVisibility(visibility);

            }
        }
    }
}