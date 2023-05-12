package com.godsaeng.godsaengfinal.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.godsaeng.godsaengfinal.R;
import com.godsaeng.godsaengfinal.databinding.FragmentLoginBinding;
import com.godsaeng.godsaengfinal.ui.dialogs.DialogLoading;
import com.godsaeng.godsaengfinal.ui.utilities.FBAuth;
import com.godsaeng.godsaengfinal.ui.utilities.Util;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends BaseFragment {

    private FragmentLoginBinding binding;
    private final FBAuth fbAuth = FBAuth.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(binding.editTextId, binding.editTextPw);
            }
        });

        binding.textSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate(R.id.navigation_sign_up);
            }
        });

        return root;
    }

    private DialogLoading dialog;

    private void login(EditText editEmail, EditText editPW){
        if (dialog == null) dialog = new DialogLoading(requireContext());
        dialog.show();
        binding.buttonLogin.setEnabled(false);
        fbAuth.signInWithEmail(editEmail, editPW, new FBAuth.AuthResult() {
            @Override
            public void onSuccess() {
                onLoggedIn();
            }

            @Override
            public void onFail(String msg) {
                if (dialog != null) dialog.dismiss();
                binding.buttonLogin.setEnabled(true);
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onLoggedIn() {
        FirebaseUser user = FBAuth.getInstance().getUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference settingRef = rootRef.child(user.getUid()).child("settings");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dialog != null) dialog.dismiss();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    if ("is_first_run".equals(key)) {
                        Object value = ds.getValue();
                        if (value instanceof Boolean && (Boolean) value) {
                            settingRef.child("is_first_run").setValue(false);
                            navigate(R.id.navigation_god_goal);
                        } else {
                            popCurrentBackStack();
                            navigate(R.id.navigation_first);
                        }
                        return;
                    }
                }
                settingRef.child("is_first_run").setValue(false);
                navigate(R.id.navigation_god_goal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (dialog != null) dialog.dismiss();
                Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        };
        settingRef.addListenerForSingleValueEvent(eventListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        Util.setStatusBarColor(requireActivity(), requireContext().getColor(R.color.green));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}