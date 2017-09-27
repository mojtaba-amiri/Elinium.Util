package com.elinium.util;

import android.os.Handler;
import android.util.Log;

/**
 * Created by amiri on 9/16/2017.
 */

public class Async {
    public static void Do(Runnable r) {
        try {
            new Thread(r).start();
        } catch (Exception e) {
            Log.e("Async Runnable", "Error:" + e.getMessage());
        }
    }
}
