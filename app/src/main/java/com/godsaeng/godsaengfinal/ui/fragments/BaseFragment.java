package com.godsaeng.godsaengfinal.ui.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.godsaeng.godsaengfinal.R;
import com.godsaeng.godsaengfinal.ui.utilities.Util;

public class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Util.setStatusBarColor(requireActivity(), requireContext().getColor(R.color.white));
    }

    void navigateUp() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    void navigate(int destinationId) {
        if (isAdded()) NavHostFragment.findNavController(this).navigate(destinationId, null, getNaviOption());
    }

    void navigate(int destinationId, Bundle args) {
        if (isAdded()) NavHostFragment.findNavController(this).navigate(destinationId, args, getNaviOption());
    }

    void replace(int destinationId) {
        NavController navController = NavHostFragment.findNavController(this);
        NavDestination navDestination = navController.getCurrentDestination();
        if (navDestination != null) {
            int currentId = navDestination.getId();
            if (currentId == destinationId) return;
        }
        if (destinationId != R.id.navigation_third) popCurrentBackStack();
        if (isAdded()) navController.navigate(destinationId);
    }



    private NavOptions getNaviOption() {
        NavOptions.Builder builder = new NavOptions.Builder();
        builder.setEnterAnim(R.anim.slide_in_right);
        builder.setExitAnim(R.anim.slide_out_left);
        builder.setPopEnterAnim(R.anim.slide_in_left);
        builder.setPopExitAnim(R.anim.slide_out_right);
        return builder.build();
    }

    void setMenuListener(View view, int destinationId) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replace(destinationId);
            }
        });
    }

    void popCurrentBackStack() {
        NavController navController = NavHostFragment.findNavController(this);
        NavDestination navDestination = navController.getCurrentDestination();
        if (navDestination != null) {
            int currentId = navDestination.getId();
            navController.popBackStack(currentId, true);
        }
    }

    void popBackStack(int id) {
        NavHostFragment.findNavController(this).popBackStack(id, true);
    }
}
