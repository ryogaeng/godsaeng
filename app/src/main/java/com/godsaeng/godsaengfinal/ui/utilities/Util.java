package com.godsaeng.godsaengfinal.ui.utilities;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Insets;
import android.graphics.Point;
import android.os.Build;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.godsaeng.godsaengfinal.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static String[] getToday() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String date = format.format(new Date(System.currentTimeMillis()));
        return date.split("-");
    }

    public static String[] getLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String date = format.format(new Date(calendar.getTimeInMillis()));
        return date.split("-");
    }

    public static String getText(EditText editText) {
        Editable editable = editText.getText();
        if (editable == null) {
            return "";
        } else {
            return editable.toString().trim();
        }
    }

    public static long parseLong(String value){
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void setStatusBarColor(Activity activity, int color){
        if (activity != null) {
            Window window = activity.getWindow();
            window.setStatusBarColor(color);
        }
    }

    public static void hideKeyboard(View view) {
        ((InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @SuppressWarnings("Deprecation")
    public static Point getDeviceSize(Activity activity, boolean minusInsets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            int width = windowMetrics.getBounds().width();
            int height = windowMetrics.getBounds().height();
            if (minusInsets) {
                Insets insets = windowMetrics.getWindowInsets()
                        .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
                width = width - insets.left - insets.right;
                height = height - insets.top - insets.bottom;
            }
            return new Point(width, height);
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
            return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
        }
    }

    public static void setLayoutSize(View view, int width, int height) {
        if (view == null) return;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (width > 0) params.width = width;
        if (height > 0) params.height = height;
        view.setLayoutParams(params);
    }

    public static void showDialogFragment(FragmentManager fm, String TAG, DialogFragment dialogFragment) {
        if (fm != null) {
            Fragment prevFragment = fm.findFragmentByTag(TAG);
            if (prevFragment!=null) {
                FragmentTransaction tr = fm.beginTransaction();
                tr.remove(prevFragment);
                tr.commit();
            }
            dialogFragment.show(fm, TAG);
        }
    }
}
