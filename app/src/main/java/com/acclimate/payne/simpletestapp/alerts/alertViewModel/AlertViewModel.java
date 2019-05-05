package com.acclimate.payne.simpletestapp.alerts.alertViewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.acclimate.payne.simpletestapp.alerts.AlertWrapper;
import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.Getter;

/**
 * Used to gather and updates Alert related UI stuff
 */
public class AlertViewModel extends ViewModel {

    public static final int MAX_HISTO_PIN_QUEUE_CAPACITY = 256;

    private MutableLiveData<List<UserAlert>>    userAlerts;
    private MutableLiveData<List<BasicAlert>>   liveAlerts;
    private MutableLiveData<LinkedBlockingQueue<BasicAlert>>   histoAlerts;

    @Getter
    private int currentCount;
    public static int count = 0;

    public AlertViewModel(){

        this.currentCount = count++;
//        Log.i("APPFLOW", count + "");

        List<BasicAlert> liveAlertsList = new ArrayList<>();
        List<UserAlert> userAlertsList = new ArrayList<>();
        LinkedBlockingQueue<BasicAlert> histAlertsList = new LinkedBlockingQueue<>(MAX_HISTO_PIN_QUEUE_CAPACITY);

        liveAlerts = new MutableLiveData<>();
        liveAlerts.setValue(liveAlertsList);

        userAlerts = new MutableLiveData<>();
        userAlerts.setValue(userAlertsList);

        histoAlerts = new MutableLiveData<>();
        histoAlerts.setValue(histAlertsList);

    }


    public synchronized MutableLiveData<List<UserAlert>> getUserAlerts() {
        return userAlerts;
    }

    public synchronized MutableLiveData<List<BasicAlert>> getLiveAlerts() {
        return liveAlerts;
    }

    public synchronized MutableLiveData<LinkedBlockingQueue<BasicAlert>> getHistAlerts() {
        return histoAlerts;
    }



    public synchronized void setValues(@NonNull AlertWrapper wrapper){

//        Log.i(AppTag.VIEWMODEL, String.format("\nsetting alert view model value !\nuser alerts length = %s\nlive alerts length = %s", wrapper.getUser().length, wrapper.getLive().length));
        userAlerts.postValue(new ArrayList<>(Arrays.asList(wrapper.getUser())));
        liveAlerts.postValue(new ArrayList<>(Arrays.asList(wrapper.getLive())));

    }


    //todo : make sure no bugs ... quite hackish......
    public void forceUpdate(){

        userAlerts.setValue(userAlerts.getValue());
        liveAlerts.setValue(liveAlerts.getValue());
        histoAlerts.setValue(histoAlerts.getValue());

    }


}
