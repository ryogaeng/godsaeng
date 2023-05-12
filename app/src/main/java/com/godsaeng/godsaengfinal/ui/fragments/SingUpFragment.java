package com.godsaeng.godsaengfinal.ui.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.godsaeng.godsaengfinal.R;
import com.godsaeng.godsaengfinal.databinding.FragmentLoginBinding;
import com.godsaeng.godsaengfinal.databinding.FragmentSignUpBinding;
import com.godsaeng.godsaengfinal.ui.dialogs.DialogLoading;
import com.godsaeng.godsaengfinal.ui.utilities.FBAuth;
import com.godsaeng.godsaengfinal.ui.utilities.Util;

public class SingUpFragment extends BaseFragment {

    private FragmentSignUpBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.buttonSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(v);
                singUp(binding.editEmail, binding.editPassword, binding.editPasswordConfirm);
            }
        });
        return root;
    }


    private final FBAuth fbAuth = FBAuth.getInstance();
    private DialogLoading dialog;

    private void singUp(EditText editEmail, EditText editPW, EditText editPWConfirm){
        if (dialog == null) dialog = new DialogLoading(requireContext());
        dialog.show();
        fbAuth.signUpWithEmail(requireActivity(), editEmail, editPW, editPWConfirm, new FBAuth.AuthResult() {
            @Override
            public void onSuccess() {
                if (dialog != null) dialog.dismiss();
                navigateUp();
            }

            @Override
            public void onFail(String msg) {
                Log.d("SingUp", "onFail - msg: " + msg);
                if (dialog != null) dialog.dismiss();
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Util.setStatusBarColor(requireActivity(), requireContext().getColor(R.color.green));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dialog != null) dialog.dismiss();
        binding = null;
    }

}