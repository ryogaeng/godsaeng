package com.godsaeng.godsaengfinal.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.godsaeng.godsaengfinal.R;
import com.godsaeng.godsaengfinal.databinding.FragmentThirdBinding;
import com.godsaeng.godsaengfinal.ui.dialogs.DialogInput;
import com.godsaeng.godsaengfinal.ui.dialogs.DialogLoading;
import com.godsaeng.godsaengfinal.ui.dialogs.DialogSpending;
import com.godsaeng.godsaengfinal.ui.utilities.FBAuth;
import com.godsaeng.godsaengfinal.ui.utilities.Util;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ThirdFragment extends BaseFragment {

    public static final String CIGARETTE = "cigarette";
    public static final String LIQUOR = "liquor";
    public static final String OTHERS = "others";

    private FragmentThirdBinding binding;

    private static final long[] values = new long[3];

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentThirdBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.buttonBack.setOnClickListener(v -> navigateUp());

        setMenuListener(binding.menuLayout.menu1, R.id.navigation_first);
        setMenuListener(binding.menuLayout.menu2, R.id.navigation_second);
        setMenuListener(binding.menuLayout.menu3, R.id.navigation_third);
        setMenuListener(binding.menuLayout.menu4, R.id.navigation_fourth);

        binding.layout1.getRoot().setOnClickListener(v -> showDialogSpending(CIGARETTE));
        binding.layout2.getRoot().setOnClickListener(v -> showDialogSpending(LIQUOR));
        binding.layout3.getRoot().setOnClickListener(v -> showDialogSpending(OTHERS));

        binding.text1.setOnClickListener(v -> showInputDialog(CIGARETTE));
        binding.text2.setOnClickListener(v -> showInputDialog(LIQUOR));
        binding.text3.setOnClickListener(v -> showInputDialog(OTHERS));

        getSpending();
        return root;
    }

    private void getSpending() {
        String[] today = Util.getToday();

        FirebaseUser user = FBAuth.getInstance().getUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference spendingRef = rootRef.child(user.getUid()).child("spending");
        DatabaseReference ref1 = spendingRef.child(CIGARETTE).child(today[0]).child(today[1]).child(today[2]);
        ValueEventListener eventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() instanceof Long) {
                    long value = (Long) dataSnapshot.getValue();
                    values[0] = value;
                    String text = "흡연자 이신가요? 그렇다면 오늘 하루 얼마나 피우셨나요? " + (value / 20) + "갑 " + (value % 20) + "개비";
                    if (binding != null) binding.text1.setText(text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        ref1.addListenerForSingleValueEvent(eventListener1);

        DatabaseReference ref2 = spendingRef.child(LIQUOR).child(today[0]).child(today[1]).child(today[2]);
        ValueEventListener eventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() instanceof Long) {
                    long value = (Long) dataSnapshot.getValue();
                    values[1] = value;
                    String text = "오늘 하루 술값이 얼마나 나왔나요?\n" + value + "원";
                    if (binding != null)  binding.text2.setText(text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        ref2.addListenerForSingleValueEvent(eventListener2);

        DatabaseReference ref3 = spendingRef.child(OTHERS).child(today[0]).child(today[1]).child(today[2]);
        ValueEventListener eventListener3 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() instanceof Long) {
                    long value = (Long) dataSnapshot.getValue();
                    values[2] = value;
                    String text = "이 외에 오늘 얼마를 지출하셨나요?\n" + value + "원";
                    if (binding != null) binding.text3.setText(text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        ref3.addListenerForSingleValueEvent(eventListener3);
    }

    private void showInputDialog(String what) {
        long v = (what.equals(CIGARETTE)) ? values[0] : (what.equals(LIQUOR)) ? values[1] : values[2];
        DialogInput dialogInput = DialogInput.newInstance(what, v);
        dialogInput.setCompleteListener(new DialogInput.OnCompleteListener() {
            @Override
            public void onComplete(long value) {
                FirebaseUser user = FBAuth.getInstance().getUser();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference spendingRef = rootRef.child(user.getUid()).child("spending").child(what);
                String[] date = Util.getToday();
                spendingRef.child(date[0]).child(date[1]).child(date[2]).setValue(value);
                String text;
                switch (what) {
                    case CIGARETTE:
                        values[0] = value;
                        text = "흡연자 이신가요? 그렇다면 오늘 하루 얼마나 피우셨나요? " + (value / 20) + "갑 " + (value % 20) + "개비";
                        binding.text1.setText(text);
                        break;
                    case LIQUOR:
                        values[1] = value;
                        text = "오늘 하루 술값이 얼마나 나왔나요?\n" + value + "원";
                        binding.text2.setText(text);
                        break;
                    case OTHERS:
                        values[2] = value;
                        text = "이 외에 오늘 얼마를 지출하셨나요?\n" + value + "원";
                        binding.text3.setText(text);
                        break;
                }
            }
        });
        Util.showDialogFragment(requireActivity().getSupportFragmentManager(), "DialogInput", dialogInput);
    }

    private DialogLoading dialog;

    private void showDialogSpending(String what) {
        if (dialog == null) dialog = new DialogLoading(requireContext());
        dialog.show();

        String[] lastMonth = Util.getLastMonth();

        FirebaseUser user = FBAuth.getInstance().getUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference spendingRef = rootRef.child(user.getUid()).child("spending").child(what);
        DatabaseReference lastMonthRef = spendingRef.child(lastMonth[0]).child(lastMonth[1]);
        ValueEventListener eventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long total = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue() instanceof Long) {
                        long value = (long) ds.getValue();
                        total += value;
                    }
                }
                long lastMonth = total;
                String[] thisMonth = Util.getToday();
                DatabaseReference thisMonthRef = spendingRef.child(thisMonth[0]).child(thisMonth[1]);
                ValueEventListener eventListener2 = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long thisMonth = 0;
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.getValue() instanceof Long) {
                                long value = (long) ds.getValue();
                                thisMonth += value;
                            }
                        }
                        if (dialog != null) dialog.dismiss();
                        DialogSpending dialogSpending = DialogSpending.newInstance(what, lastMonth, thisMonth);
                        Util.showDialogFragment(requireActivity().getSupportFragmentManager(), "DialogSpending", dialogSpending);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (dialog != null) dialog.dismiss();
                    }
                };
                thisMonthRef.addListenerForSingleValueEvent(eventListener2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (dialog != null) dialog.dismiss();
            }
        };
        lastMonthRef.addListenerForSingleValueEvent(eventListener1);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}