package com.acclimate.payne.simpletestapp.alerts.alertViewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

public class AlertViewModelSingletonFactory extends ViewModelProvider.NewInstanceFactory {

    private static AlertViewModelSingletonFactory instance;
    private AlertViewModel alertViewModel;

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {

        return (T) alertViewModel;
    }


    public static AlertViewModelSingletonFactory getInstance(){

        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (AlertViewModelSingletonFactory.class) {
                if (instance == null) {
                    instance = new AlertViewModelSingletonFactory();
                }
            }
        }
        return instance;
    }


    public void initViewModel(AlertViewModel model){
//        Log.i(AppTag.VIEWMODEL, "init");
        alertViewModel = model;
    }




}
