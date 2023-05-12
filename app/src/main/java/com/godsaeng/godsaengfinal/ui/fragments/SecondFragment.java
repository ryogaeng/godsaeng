package com.godsaeng.godsaengfinal.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.godsaeng.godsaengfinal.R;
import com.godsaeng.godsaengfinal.databinding.FragmentSecondBinding;
import com.godsaeng.godsaengfinal.databinding.VhSecondBinding;
import com.godsaeng.godsaengfinal.ui.utilities.Constants;

import java.lang.ref.WeakReference;

public class SecondFragment extends BaseFragment {

    private FragmentSecondBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(new Adapter(this));

        setMenuListener(binding.menuLayout.menu1, R.id.navigation_first);
        setMenuListener(binding.menuLayout.menu2, R.id.navigation_second);
        setMenuListener(binding.menuLayout.menu3, R.id.navigation_third);
        setMenuListener(binding.menuLayout.menu4, R.id.navigation_fourth);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        binding = null;
    }

    private void onItemClicked(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("DAY_OF_WEEK", Constants.DAY_OF_WEEKS[position]);
        navigate(R.id.navigation_schedule, bundle);
    }

    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        private final WeakReference<SecondFragment> fragment;

        Adapter(SecondFragment secondFragment) {
            fragment = new WeakReference<>(secondFragment);
        }

        private final String[] list = new String[] { "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일" };
        private final int[] imageIds = new int[] { R.drawable.ic_monday, R.drawable.ic_tuesday, R.drawable.ic_wendesday, R.drawable.ic_thursday, R.drawable.ic_friday, R.drawable.ic_saturday, R.drawable.ic_sunday };

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(VhSecondBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return list.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final VhSecondBinding binding;
            public ViewHolder(VhSecondBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                binding.getRoot().setOnClickListener(v -> {
                    if (fragment.get() != null) fragment.get().onItemClicked(getAdapterPosition());
                });
            }

            private void bind(int position) {
                binding.imageView.setImageResource(imageIds[position]);
                binding.textView.setText(list[position]);
            }
        }
    }
}