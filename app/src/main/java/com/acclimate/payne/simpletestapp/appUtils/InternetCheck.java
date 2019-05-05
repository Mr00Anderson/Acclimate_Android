package com.acclimate.payne.simpletestapp.appUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class InternetCheck extends AsyncTask<Void, Void, Boolean> {

    private Consumer mConsumer;

    public interface Consumer {
        void accept(Boolean internet);
    }

    public InternetCheck(Consumer consumer) {
        mConsumer = consumer;
        execute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress("8.8.8.8", 53), 3000);
            sock.close();
//            Log.w("INTERNET CHECK", "has Internet");
            return true;
        } catch (IOException e) {
//            Log.w("INTERNET CHECK", "NO Internet");
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean internet) {
        mConsumer.accept(internet);
    }




    // TEST !!! [WIP], seems to work

    /**
     * *** Andoid docu says it's deprecated, bit
     * @param context
     * @return
     */
    public static boolean synchronous(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();

        } catch (NullPointerException npe){
//            Log.w("INTERNET CHECK", "NO Internet");
            return false;
        }

    }


    public static boolean isWifiAvailable(Context ctx){
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            return connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        } else {
            return false;
        }
    }

}

///////////////////////////////////////////////////////////////////////////////////
// Usage

// new InternetCheck(hasInternet -> { /* do something with boolean response */ });


// Autre méthode:

//    public boolean isInternetAvailable() {
//        try {
//            InetAddress ipAddr = InetAddress.getByName("google.com");
//            //You can replace it with your name
//            return !ipAddr.equals("");
//
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//
//
//
// SYNCHRONOUS USAGE :
//
//    if (InternetCheck.synchronous(getApplicationContext())) {
//       *** do internet stuff here***
//    } else {
//       *** do stuff without internet ***
//    }
