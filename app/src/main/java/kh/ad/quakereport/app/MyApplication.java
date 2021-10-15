package kh.ad.quakereport.app;

import android.annotation.SuppressLint;
import android.content.res.Configuration;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

/**
 * This class is responsible for prevent multiDex(or 64k reference exceed) build error
 */

@SuppressLint("Registered")
public class MyApplication extends MultiDexApplication {
    private static final String TAG = "dalilisouq";
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(getApplicationContext());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
