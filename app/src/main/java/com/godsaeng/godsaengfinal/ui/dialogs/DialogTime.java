package com.godsaeng.godsaengfinal.ui.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.godsaeng.godsaengfinal.databinding.DialogTimeBinding;
import com.godsaeng.godsaengfinal.databinding.VhTimeBinding;
import com.godsaeng.godsaengfinal.ui.utilities.Util;

import java.util.ArrayList;

public class DialogTime extends BaseDialog {

    @Override
    float getWidthFactor() {
        return 0.95f;
    }

    private OnCompleteListener mListener;


    public interface OnCompleteListener {
        void onComplete(int hour, int minute, String value);
    }

    public void setCompleteListener(OnCompleteListener listener) {
        mListener = listener;
    }

    public static DialogTime newInstance(boolean isDailyGoal, int hour, String value){
        DialogTime fragment = new DialogTime();
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_daily_goal", isDailyGoal);
        bundle.putInt("hour", hour);
        bundle.putString("value", value);
        fragment.setArguments(bundle);
        return fragment;
    }

    public DialogTime() { }

    private DialogTimeBinding binding;
    private boolean isDailyGoal;
    private String value;
    private int hour;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogTimeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            isDailyGoal = getArguments().getBoolean("is_daily_goal");
            value = getArguments().getString("value");
            hour = getArguments().getInt("hour");
            binding.editText.setText(value);
        }

        if (isDailyGoal) {
            binding.textTitle.setText("Daily Goal");
            binding.textHour.setText("시");
            binding.textMinute.setVisibility(View.GONE);
            binding.rvMinute.setVisibility(View.GONE);
        } else {
            binding.editText.setHint("ex) 오픽 인강");
        }

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onComplete();
            }
        });

        initRecyclerView(binding.rvHour, true);
        initRecyclerView(binding.rvMinute, false);

        binding.rvHour.scrollToPosition(hour);
    }

    private void onComplete() {
        String value = Util.getText(binding.editText);
        if (value.isEmpty()) {
            Toast.makeText(requireContext(), "Goal을 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }
        if (mListener != null) mListener.onComplete(adapters.get(0).position - 1, adapters.get(1).position - 1, value);
        dismiss();
    }

    private final ArrayList<Adapter> adapters = new ArrayList<>();

    private void initRecyclerView(RecyclerView recyclerView, boolean isHour) {
        Adapter adapter = new Adapter(isHour);
        adapters.add(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int currentPosition = RecyclerView.NO_POSITION;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View v = snapHelper.findSnapView(layoutManager);
                if (v != null) {
                    int position = layoutManager.getPosition(v);
                    if (currentPosition != position) {
                        currentPosition = position;
                        adapter.position = position;
                        Log.d("Dialog", "currentPosition=" + currentPosition);
                    }
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.rvHour.setAdapter(null);
        binding.rvMinute.setAdapter(null);
        adapters.clear();
        binding = null;
        mListener = null;
    }

    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        private final boolean isHour;
        private int position;

        Adapter(boolean isHour) {
            this.isHour = isHour;
        }

        @Override
        @NonNull
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(VhTimeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return isHour ? 26 : 62;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final VhTimeBinding binding;
            public ViewHolder(VhTimeBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            private void bind(int position) {
                if (position == 0 || position == (getItemCount() - 1)) {
                    binding.textTime.setText("");
                } else {
                    int i = position - 1;
                    String time = (i < 10) ? "0" + i : String.valueOf(i);
                    binding.textTime.setText(time);
                }

            }
        }
    }
}
