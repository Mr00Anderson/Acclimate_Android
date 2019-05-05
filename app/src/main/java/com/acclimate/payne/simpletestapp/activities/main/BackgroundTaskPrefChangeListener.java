package com.acclimate.payne.simpletestapp.activities.main;

import android.content.SharedPreferences;

import com.acclimate.payne.simpletestapp.appUtils.AppTag;
import com.acclimate.payne.simpletestapp.appUtils.InternetCheck;
import com.acclimate.payne.simpletestapp.backgroundTask.BackgroundConstriant;
import com.acclimate.payne.simpletestapp.backgroundTask.BackgroundRequestWorker;
import com.acclimate.payne.simpletestapp.backgroundTask.BackgroundTaskConfig;

import java.util.concurrent.TimeUnit;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkInfo; // the v1.0.1 equivalent of "WorkStatus"
import static androidx.work.WorkInfo.State.CANCELLED;
import static androidx.work.WorkInfo.State.ENQUEUED;
import static com.acclimate.payne.simpletestapp.backgroundTask.BackgroundRequestWorker.backgroundRequest;
import static com.acclimate.payne.simpletestapp.backgroundTask.BackgroundTaskConfig.FAST_REFRESH_VALUE;
import static com.acclimate.payne.simpletestapp.backgroundTask.BackgroundTaskConfig.MEDUIM_REFRESH_VALUE;
import static com.acclimate.payne.simpletestapp.backgroundTask.BackgroundTaskConfig.NO_REFRESH_VALUE;
import static com.acclimate.payne.simpletestapp.backgroundTask.BackgroundTaskConfig.SLOW_REFRESH_VALUE;


public class BackgroundTaskPrefChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {

    MainActivity act;

    public BackgroundTaskPrefChangeListener(MainActivity act){
        this.act = act;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sP, String key) {

        if (key.equals(AppTag.NETWORK_AUTO_REFRESH)) {
            updateRefreshRates(sP, key);
            return;
        }

        if (key.equals(AppTag.NETWORK_WIFI_ONLY)){
            updateWifiUsage(sP);
        }

    }


    private void updateRefreshRates(SharedPreferences sP, String key){

        String value = sP.getString(key, "0");
        PeriodicWorkRequest currentWorker = getCurrentWorker();

        if (value.equals(NO_REFRESH_VALUE)) {
            WorkManager.getInstance().cancelWorkById(currentWorker.getId());
            return;
        }

        long refreshRate = getRefreshRateValue(value);
        createNewWorkerTask(refreshRate);

    }


    public void updateWifiUsage(SharedPreferences sP) {

        boolean wifiIsOn = InternetCheck.isWifiAvailable(act);
        boolean onlyOnWifi = sP.getBoolean(AppTag.NETWORK_WIFI_ONLY, true);
        long refreshRate = getRefreshRateValue(sP.getString(AppTag.NETWORK_AUTO_REFRESH, "1"));

        // if onlyOnWifi and wifi is off, cancel task
        if (!wifiIsOn && onlyOnWifi) {
            WorkManager.getInstance().cancelWorkById(getCurrentWorker().getId());
            return;
        }

        try {
            WorkInfo status = WorkManager.getInstance().getWorkInfoById(getCurrentWorker().getId()).get();

            // if task is cancelled and on wifi, create a new task
            if (wifiIsOn
                    && status != null
                    && (status.getState().equals(CANCELLED) || status.getState().equals(ENQUEUED))) {
                createNewWorkerTask(refreshRate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    private void createNewWorkerTask(long refreshRate){

        // creates new Worker
        PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(
                BackgroundRequestWorker.class,
                refreshRate,
                TimeUnit.MINUTES)
                .setConstraints(BackgroundConstriant.basic());


        PeriodicWorkRequest back = builder.build();
        WorkManager.getInstance().enqueueUniquePeriodicWork(
                BackgroundRequestWorker.class.getName(),
                ExistingPeriodicWorkPolicy.REPLACE,
                back);

    }

    private long getRefreshRateValue(String value){
        switch (value) {
            case SLOW_REFRESH_VALUE: default:
                return BackgroundTaskConfig.SLOW_REFRESH;
            case MEDUIM_REFRESH_VALUE:
                return BackgroundTaskConfig.MEDIUM_REFRESH;
            case FAST_REFRESH_VALUE:
                return BackgroundTaskConfig.FAST_REFRESH;
        }
    }

    private PeriodicWorkRequest getCurrentWorker(){
        return backgroundRequest;
    }

}
