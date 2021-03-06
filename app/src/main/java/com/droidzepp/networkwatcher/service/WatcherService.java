package com.droidzepp.networkwatcher.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.droidzepp.networkwatcher.model.WatchedApp;
import com.droidzepp.networkwatcher.repository.DatabaseHandler;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nijat on 30/09/15.
 */
public class WatcherService extends Service {

    private Timer timer = new Timer();
    private DatabaseHandler db = new DatabaseHandler(this);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (db.getAllWatchedApps().isEmpty())
            stopservice();
        else
            startservice();
    }

    @Override
    public void onDestroy() {
        Log.d("d", "dessssssssssssssssssssssss");
        super.onDestroy();
        stopservice();
    }

    private void startservice() {




        Log.d("a", "first time");
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                List<WatchedApp> watchedApps = db.getAllWatchedApps();
                for (WatchedApp app : watchedApps) {
                    Long received1 = TrafficStats.getUidRxBytes(app.getUid());
                    Long transmitted1 = TrafficStats.getUidTxBytes(app.getUid());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Long received2 = TrafficStats.getUidRxBytes(app.getUid());
                    Long transmitted2 = TrafficStats.getUidTxBytes(app.getUid());
                    ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                    if ((!received1.equals(received2) || !transmitted1.equals(transmitted2)) &&
                            ((app.isWifiSelected() == (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) ||
                                    (app.isMobDataSelected() == (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)) ))
                        Log.d("dd", app.getAppName()+ " watched app using network!");
                }

            }
        }, 0, 10000);
    }

    private void stopservice() {

        if (timer != null) {
            Log.d("aaa", "fissssssssss");

            timer.cancel();

        }

    }
}
