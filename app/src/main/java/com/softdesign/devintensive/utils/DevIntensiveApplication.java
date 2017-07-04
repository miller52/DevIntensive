package com.softdesign.devintensive.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.stetho.Stetho;
import com.softdesign.devintensive.data.storage.model.DaoMaster;
import com.softdesign.devintensive.data.storage.model.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by roman on 28.06.16.
 */
public class DevIntensiveApplication extends Application {

    private static Context sContext;
    private static SharedPreferences sSharedPreferences;
    private static DaoSession sDaoSession;

    public static SharedPreferences getsSharedPreferences() {
        return sSharedPreferences;
    }

    public static Context getAppContext() {
        return DevIntensiveApplication.sContext;
    }

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DevIntensiveApplication.sContext = getApplicationContext();
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "devintensive-db");
        Database db = helper.getWritableDb();
        sDaoSession = new DaoMaster(db).newSession();

        Stetho.initializeWithDefaults(this);
    }
}
