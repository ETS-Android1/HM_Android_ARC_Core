package com.healthymedium.arc.api;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class CallbackChain {

    private static final String tag = "CallbackChain";

    List<Link> links = new ArrayList<>();
    RestClient.Listener clientListener;


    boolean addLink(Call call){
        return addLink(call,null);
    }

    boolean addLink(Call call, Listener listener){
        if(call==null){
            return false;
        }
        Link link = new Link();
        link.call = call;
        link.listener = listener;
        links.add(link);
        return true;
    }

    void execute(RestClient.Listener clientListener){
        this.clientListener = clientListener;
        if(links.size()==0){
            Log.e(tag,"no calls to execute, aborting");
        }
        if(links.get(0).call==null){
            Log.e(tag,"call is null, aborting");
        }
        links.get(0).call.enqueue(callback);
    }

    private Callback callback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> retrofitResponse) {
            if(links.size()==0){
                Log.wtf(tag,"link doesn't exist, investigate this");
                return;
            }

            RestResponse response = RestResponse.fromRetrofitResponse(retrofitResponse);
            Link link = links.remove(0);

            boolean proceed = true;
            if(link.listener!=null) {
                proceed = link.listener.onResponse(response);
            }

            if(!proceed){
                Log.i(tag,"stop requested");
                return;
            }

            if(links.size()==0){
                Log.i(tag,"stopping at the end of the chain");
                return;
            }

            if(links.get(0).call==null){
                Log.e(tag,"call is null, aborting");
                return;
            }

            links.get(0).call.enqueue(this);
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
            if(links.size()==0){
                Log.wtf(tag,"link doesn't exist, investigate this");
                return;
            }

            RestResponse response = RestResponse.fromRetrofitFailure(throwable);
            Link link = links.remove(0);

            boolean proceed = true;
            if(link.listener!=null) {
                proceed = link.listener.onFailure(response);
            }

            if(!proceed){
                Log.i(tag,"stop requested");
                return;
            }

            if(links.size()==0){
                Log.i(tag,"stopping at the end of the chain");
                return;
            }

            if(links.get(0).call==null){
                Log.e(tag,"call is null, aborting");
                return;
            }

            links.get(0).call.enqueue(this);
        }
    };

    private class Link {
        Call call;
        Listener listener;
    }

    public interface Listener{
        boolean onResponse(RestResponse response);
        boolean onFailure(RestResponse response);
    }
}
