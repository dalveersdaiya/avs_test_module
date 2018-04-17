package com.awrtechnologies.androidvibratorservice.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class GeneralHelper {

    private static GeneralHelper generalHelper;
    private Context context;

    public static GeneralHelper getGeneralHelper() {
        return generalHelper;
    }

    public static void setGeneralHelper(GeneralHelper generalHelper) {
        GeneralHelper.generalHelper = generalHelper;
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public synchronized static GeneralHelper getInstance(Context context) {
        if (generalHelper == null) {
            generalHelper = new GeneralHelper(context);
        }
        return generalHelper;
    }

    public GeneralHelper(Context context) {
        this.context = context;
    }

    /**
     * Convert dp in pixels
     *
     * @param dp
     * @return
     */
    public int getPx(int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return ((int) (dp * scale + 0.5f));
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public Point getScreenSize() {

        Point size = new Point();
        WindowManager w = ((Activity) context).getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            w.getDefaultDisplay().getSize(size);
        } else {
            Display d = w.getDefaultDisplay();
            size.x = d.getWidth();
            size.y = d.getHeight();
        }
        return size;
    }

}
