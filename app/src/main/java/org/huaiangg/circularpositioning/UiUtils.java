package org.huaiangg.circularpositioning;

import android.content.Context;

/**
 * @description: 屏幕的工具类
 * @author: HuaiAngg
 * @create: 2019-04-15 8:49
 */
public class UiUtils {

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
