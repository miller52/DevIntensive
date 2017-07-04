package com.softdesign.devintensive.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by roman on 12.07.16.
 */
public class NetworkHelper {

    /**
     * Метод проверяет соединение с Интернетом
     *
     * @param context контекст
     * @return булево значение, однозначно характеризующее соединение с Интернет.
     * TRUE - есть соединение, FALSE - нет соединения.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
