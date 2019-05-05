package com.acclimate.payne.simpletestapp;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;

import com.acclimate.payne.simpletestapp.alerts.AlertWrapper;
import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModel;
import com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModelSingletonFactory;
import com.acclimate.payne.simpletestapp.backgroundTask.BackgroundConstriant;
import com.acclimate.payne.simpletestapp.backgroundTask.BackgroundRequestWorker;
import com.acclimate.payne.simpletestapp.backgroundTask.BackgroundTaskConfig;
import com.acclimate.payne.simpletestapp.deviceStorage.localStorage.FileLocalStorage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import static com.acclimate.payne.simpletestapp.appUtils.AppConfig.ALERT_WRAPPER_FILENAME;
import static com.acclimate.payne.simpletestapp.backgroundTask.BackgroundRequestWorker.backgroundRequest;

// import com.example.payne.simpletestapp.appUtils.BackgroundRequestWorker;


/** ******************************************************************************************
 *
 *
 *
 * SOME NAMING CONVENTION :
 *
 *
 * Alerts : Three type of alerts
 * <ul>
 *     <li>Live : Currently live alerts confrimed from an official source like the governement.</li>
 *     <li>User : An alert that hjas been posted by a user of the application. Not yet confirmed.</li>
 *     <li>Historical : An alert that is NO LONGUER CURRENT and has been saved in the internal Database of Acclimete. Can come from<./li>
 * </ul>
 *
 * BasicAlerts : An alert that is NOT a user alert. Can either be a Historical
 *
 * request => implies a server connection with some asynchronous requirements
 * fetch => May or may not requires a server connection to retreive the data
 *
 *
 *
 *
 *
 *
 */
public final class MyApplication extends Application {


    /**
     * Task to be done on app launch :
     *  - Initialize AlertViewModel (persistent memory data for alerts)
     *  - start background task for alert updates
     */
    @Override
    public void onCreate() {
        super.onCreate();


        // ------------------------------
        // Init AlertViewModel
        AlertViewModelSingletonFactory.getInstance().initViewModel(new AlertViewModel());




        // ------------------------------
        // Background task for alert update

        PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(
                BackgroundRequestWorker.class,
                BackgroundTaskConfig.FAST_REFRESH,
                TimeUnit.MINUTES)
                .setConstraints(BackgroundConstriant.basic());


        backgroundRequest = builder.build();

        WorkManager wM = WorkManager.getInstance();

        try {
            List<WorkInfo> statuses = wM.getWorkInfosByTag(BackgroundRequestWorker.class.getName()).get();

            if (statuses == null) {
//                Log.i(BACK_TASK, "need to create new task");
                wM.enqueueUniquePeriodicWork(
                        BackgroundRequestWorker.class.getName(),
                        ExistingPeriodicWorkPolicy.REPLACE,
                        backgroundRequest);
            }
            /*
            else {
                Log.i(BACK_TASK, "task already exists !");
                WorkInfo ws = statuses.get(0);
                WorkInfo.State s = ws.getState();
                Log.i(BACK_TASK, "state = " + s.toString());
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Task to be done on app close :
     *  - save current alerts on local storage for them to be loaded on next launch
     *  -
     */
    @Override
    public void onTerminate() {

        try {

            AlertWrapper wrapper = new AlertWrapper();
            FileLocalStorage<AlertWrapper> storage =
                    new FileLocalStorage<>(ALERT_WRAPPER_FILENAME, wrapper, getApplicationContext());

            wrapper = storage.read();


            AlertViewModel alertModel = ViewModelProviders
                    .of((FragmentActivity) getApplicationContext() , AlertViewModelSingletonFactory.getInstance())
                    .get(AlertViewModel.class);

            List<UserAlert> userAlets = alertModel.getUserAlerts().getValue();
            wrapper.setUser(userAlets.toArray(new UserAlert[userAlets.size()]));

            List<BasicAlert> liveAlerts = alertModel.getLiveAlerts().getValue();
            wrapper.setLive(liveAlerts.toArray(new BasicAlert[liveAlerts.size()]));

            LinkedBlockingQueue<BasicAlert> histoAlerts = alertModel.getHistAlerts().getValue();
            wrapper.setHisto(histoAlerts.toArray(new BasicAlert[histoAlerts.size()]));

            storage.setData(wrapper);
            storage.write();

        } catch (IOException ioe) {
            ;
//            Log.e(STORAGE, "could not save alerts to local storage");
        }

        super.onTerminate();

    }




}
