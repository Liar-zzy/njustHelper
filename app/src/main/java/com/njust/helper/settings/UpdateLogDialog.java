package com.njust.helper.settings;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.njust.helper.BuildConfig;
import com.njust.helper.R;

public class UpdateLogDialog {
    private static CharSequence getMessage() {
        StringBuilder builder = new StringBuilder()
                .append(BuildConfig.VERSION_CODE)
                .append(" v")
                .append(BuildConfig.VERSION_NAME)
                .append("更新日志：\n");
        builder.append("1.修复 修复闪退问题\n");
        return builder;
    }

    public static void showUpdateDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("更新日志：")
                .setNegativeButton(R.string.action_back, null)
                .setMessage(getMessage())
                .show();
    }
}
