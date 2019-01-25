package com.elinium.util.time;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by amiri on 11/5/2017.
 */

public class Convert {
    public static Date localToGMT() {
        return localToGMT(new Date());
    }

    public static Date localToGMT(long time) {
        return localToGMT(new Date(time));
    }

        public static Date gmtToLocalDate(long time) {
        return gmtToLocalDate(new Date(time));
    }

    public static Date localToGMT(Date date) {
        // String timeZone = Calendar.getInstance().getTimeZone().getID();
        String timeZone = Calendar.getInstance().getTimeZone().getID();
        return new Date(date.getTime() - TimeZone.getTimeZone(timeZone).getOffset(date.getTime()));
//        int offset = TimeZone.getTimeZone("UTC").getOffset(date.getTime());
//        return new Date(date.getTime() + TimeZone.getTimeZone("UTC").getOffset(date.getTime()));
//
//        TimeZone.getTimeZone("UTC").getID();
//
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        try {
//            return sdf.parse(sdf.format(date));
//        } catch (ParseException e) {
//            Log.e("time.Convert.localToGMT", "error:" + e.getLocalizedMessage());
//        }
//        return new Date(sdf.format(date));
    }

    public static Date gmtToLocalDate(Date date) {
        String timeZone = Calendar.getInstance().getTimeZone().getID();
        return new Date(date.getTime() + TimeZone.getTimeZone(timeZone).getOffset(date.getTime()));
    }

    public static long getTimeLong(int y, int M, int d, int h, int m) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(y, M, d, h, m, 0);
//      long time = calendar.getTimeInMillis();
//      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//      Log.e("getTimeLong", " getTimeLong: " + sdf.format(new Date(time)) + " dayOfWeek:" + calendar.get(Calendar.DAY_OF_WEEK));
//      return time;
        return calendar.getTimeInMillis();
    }
}
