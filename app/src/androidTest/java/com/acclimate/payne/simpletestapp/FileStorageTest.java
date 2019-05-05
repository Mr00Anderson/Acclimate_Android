package com.acclimate.payne.simpletestapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;


import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.deviceStorage.localStorage.FileLocalStorage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)

public class FileStorageTest {

    private BasicAlert testAlert;
    private Context ctx;

    @Before
    public void setup(){
        this.ctx = InstrumentationRegistry.getTargetContext();
    }


    @Test
    public void testLocalStorage() throws IOException {

        FileLocalStorage alertStorage = new FileLocalStorage<>("testAlert.json", testAlert, ctx);

//        Log.i("Path", ctx.getFilesDir().toString());

        alertStorage.write();
        BasicAlert returnAlert = (BasicAlert) alertStorage.read();

        Assert.assertNotNull("Retrieved alert is null", returnAlert);
        Assert.assertEquals("Same id", testAlert.getId(), returnAlert.getId());

//        Log.w("old object : ", testAlert.toString());
//        Log.w("new object : ", returnAlert.toString());

    }
}
