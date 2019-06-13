package com.healthymedium.arc.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.Config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class NetworkUtil {

    public static boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) Application.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void checkIfServerReachable(final URL url, final ServerListener listener){
        if(listener==null){
            return;
        }
        if(!isNetworkConnected()) {
            listener.onFailed();
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.setConnectTimeout(400);
                    urlConnection.connect();
                    listener.onReached();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    listener.onFailed();
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onFailed();
                }
            }
        });
    }

    public interface ServerListener{
        void onReached();
        void onFailed();
    }

}
