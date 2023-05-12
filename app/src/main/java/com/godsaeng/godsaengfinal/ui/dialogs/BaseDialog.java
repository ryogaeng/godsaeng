package com.godsaeng.godsaengfinal.ui.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.godsaeng.godsaengfinal.ui.utilities.Util;

public abstract class BaseDialog extends AppCompatDialogFragment {

    public BaseDialog() {}

    abstract float getWidthFactor();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialog.STYLE_NORMAL, android.R.style.Theme_Material_Dialog_NoActionBar);
    }

    @Override
    public void onStart() {
        super.onStart();
        setWindowSize();
    }

    protected void setWindowSize() {
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null && getActivity() != null) {
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER);

            Point size = Util.getDeviceSize(requireActivity(), true);
            int width = (int) (size.x * getWidthFactor());
            window.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

}
