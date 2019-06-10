package com.lt3d.tools;

import android.content.Context;
import android.widget.Toast;

public class Alert {
    public static void show(Context context, String info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }
}
