package com.acclimate.payne.simpletestapp.backgroundTask;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.PeriodicWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.acclimate.payne.simpletestapp.appUtils.AppTag.BACK_TASK;


public class BackgroundRequestWorker extends Worker {

    public BackgroundRequestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * todo : find a better way to keep track of the request
     */
    public static PeriodicWorkRequest backgroundRequest;

    @NonNull
    @Override
    public Result doWork() {
        Log.i(BACK_TASK, "doWork() called");
        return Result.success();
    }

}
