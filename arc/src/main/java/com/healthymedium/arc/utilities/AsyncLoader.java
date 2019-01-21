package com.healthymedium.arc.utilities;

import android.os.AsyncTask;
import com.healthymedium.arc.core.LoadingDialog;

public class AsyncLoader extends AsyncTask<AsyncLoader.Listener, Void, Void> {

    LoadingDialog dialog;

    @Override
    protected Void doInBackground(Listener... listeners) {
        for(int i=0;i<listeners.length;i++){
            listeners[i].onExecute();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new LoadingDialog();
        dialog.show(NavigationManager.getInstance().getFragmentManager(),"LoadingDialog");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
    }

    public interface Listener{
        void onExecute();
    }
}

