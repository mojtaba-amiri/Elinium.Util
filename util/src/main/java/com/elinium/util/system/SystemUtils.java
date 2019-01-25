package com.elinium.util.system;

import android.content.res.Resources;

import java.util.UUID;

public class SystemUtils {
    public static long getMostSignificantOf(String uuid) {
        return UUID.nameUUIDFromBytes(uuid.getBytes()).getMostSignificantBits();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
