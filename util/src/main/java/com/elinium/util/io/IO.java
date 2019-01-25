package com.elinium.util.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by amiri on 10/27/2017.
 */

public class IO<T> {
    private static final String TAG = "util.IO";
    private Type type;

    protected IO() {
        type = getSuperclassTypeParameter(getClass());
    }

    public T readSharedPref(Context context, String key, T defaultValue) {
        T result = null;
        try {
            SharedPreferences sp =
                    context.getSharedPreferences(context.getApplicationContext().getPackageName(),
                            Context.MODE_PRIVATE);

            String str = sp.getString(key, null);
            GsonBuilder gson = new GsonBuilder();
            gson.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                        throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            if (!TextUtils.isEmpty(str)) {
                result = gson.create().fromJson(str, type);
                Log.i(TAG, "Reading Shared Pref Done.");
            } else {
                result = defaultValue;
            }

        } catch (Exception ex) {
            Log.e(TAG, "Reading Shared Pref Exception:" + ex.getMessage());
        }

        return result;
    }

    public T writeSharedPref(Context context, String key, T value) {

        SharedPreferences sp =
                context.getSharedPreferences(context.getApplicationContext().getPackageName(),
                        Context.MODE_PRIVATE);

        return writeSharedPref(sp, key, (Object) value);
    }

    private T writeSharedPref(SharedPreferences sp, String key, Object value) {
        T result = null;

        try {
            String str = new Gson().toJson(value);
            sp.edit().putString(key, str).apply();
            result = (T) value;
        } catch (Exception ex) {
            Log.e(TAG, "Writing Shared Pref Exception:" + ex.getMessage());
        }

        return result;
    }

    public T fromJsonFile(Context context, String jsonFileName) {
        T result = null;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(jsonFileName)));

            String line = "";

            StringBuilder buf = new StringBuilder();
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }

            GsonBuilder gson = new GsonBuilder();
            gson.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                        throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            result = gson.create().fromJson(buf.toString(), type);

            br.close();
            Log.i(TAG, "Reading JSON Done.");
        } catch (Exception ex) {
            Log.e(TAG, "Reading JSON Exception:" + ex.getMessage());
        }

        return result;
    }

    public T fromJsonString(String jsonStr) {
        T result = null;

        try {
            GsonBuilder gson = new GsonBuilder();
            gson.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                        throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });

            result = gson.create().fromJson(jsonStr, type);

            Log.i(TAG, "Reading JSON Done.");
        } catch (Exception ex) {
            Log.e(TAG, "Reading JSON Exception:" + ex.getMessage());
        }

        return result;
    }

    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

}
