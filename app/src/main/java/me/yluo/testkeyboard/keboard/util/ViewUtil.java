package me.yluo.testkeyboard.keboard.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * For wrap some utils for view.
 */
public class ViewUtil {

    private final static String TAG = "ViewUtil";

    public static boolean refreshHeight(final View view, final int aimHeight) {
        if (view.isInEditMode()) {
            return false;
        }
        Log.d(TAG, String.format("refresh Height %d %d", view.getHeight(), aimHeight));

        if (view.getHeight() == aimHeight) {
            return false;
        }

        if (Math.abs(view.getHeight() - aimHeight) ==
                StatusBarHeightUtil.getStatusBarHeight(view.getContext())) {
            return false;
        }

        final int validPanelHeight = KeyboardUtil.getKeyboardHeight(view.getContext());
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    validPanelHeight);
            view.setLayoutParams(layoutParams);
        } else {
            layoutParams.height = validPanelHeight;
            view.requestLayout();
        }

        return true;
    }

    public static boolean isFullScreen(final Activity activity) {
        return (activity.getWindow().getAttributes().flags &
                WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isTranslucentStatus(final Activity activity) {
        //noinspection SimplifiableIfStatement
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return (activity.getWindow().getAttributes().flags &
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) != 0;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static boolean isFitsSystemWindows(final Activity activity) {
        //noinspection SimplifiableIfStatement
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0).
                    getFitsSystemWindows();
        }

        return false;
    }

}
