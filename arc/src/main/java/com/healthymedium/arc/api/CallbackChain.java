package com.healthymedium.arc.api;

import com.healthymedium.arc.utilities.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class CallbackChain {

    private static final String tag = "CallbackChain";

    List<Link> links = new ArrayList<>();
    RestClient.Listener clientListener;
    Object persistentObject;
    Gson gson;

    public CallbackChain(Gson gson){
        this.gson = gson;
    }

    public Object getPersistentObject(){
        return persistentObject;
    }

    public void setPersistentObject(Object object){
        persistentObject = object;
    }

    public boolean addLink(Call call){
        return addLink(call,null);
    }

    public boolean addLink(Call call, Listener listener){
        if(call==null){
            return false;
        }
        Link link = new Link();
        link.call = call;
        link.listener = listener;
        links.add(link);
        return true;
    }

    public void execute(RestClient.Listener clientListener){
        this.clientListener = clientListener;
        if(links.size()==0){
            Log.e(tag,"no calls to execute, aborting");
        }
        if(links.get(0).call==null){
            Log.e(tag,"call is null, aborting");
        }
        Log.i(tag,"starting first link");
        links.get(0).call.enqueue(callback);
    }

    private Callback callback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> retrofitResponse) {
            Log.i(tag,"parsing response");
            RestResponse response = RestResponse.fromRetrofitResponse(retrofitResponse);
            Log.i(tag,gson.toJson(response));

            if(links.size()==0){
                Log.wtf(tag,"link doesn't exist, investigate this");
                if(clientListener!=null) {
                    clientListener.onFailure(response);
                }
                return;
            }

            Link link = links.remove(0);

            boolean proceed;
            if(link.listener!=null) {
                proceed = link.listener.onResponse(CallbackChain.this,response);
            } else {
                proceed = response.successful;
            }

            if(!proceed){
                Log.i(tag,"stop requested");
                if(clientListener!=null) {
                    clientListener.onFailure(response);
                }
                return;
            }

            if(links.size()==0){
                Log.i(tag,"stopping at the end of the chain");
                if(clientListener!=null) {
                    clientListener.onSuccess(response);
                }
                return;
            }

            if(links.get(0).call==null){
                Log.e(tag,"call is null, aborting");
                return;
            }

            Log.i(tag,"starting new link");
            links.get(0).call.enqueue(this);
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
            Log.i(tag,"parsing failure");
            RestResponse response = RestResponse.fromRetrofitFailure(throwable);
            Log.i(tag,gson.toJson(response));

            if(links.size()==0){
                Log.wtf(tag,"link doesn't exist, investigate this");
                if(clientListener!=null) {
                    clientListener.onFailure(response);
                }
                return;
            }

            Link link = links.remove(0);

            boolean proceed;
            if(link.listener!=null) {
                proceed = link.listener.onFailure(CallbackChain.this,response);
            } else {
                proceed = response.successful;
            }

            if(!proceed){
                Log.i(tag,"stop requested");
                if(clientListener!=null) {
                    clientListener.onFailure(response);
                }
                return;
            }

            if(links.size()==0){
                Log.i(tag,"stopping at the end of the chain");
                if(clientListener!=null) {
                    clientListener.onSuccess(response);
                }
                return;
            }

            if(links.get(0).call==null){
                Log.e(tag,"call is null, aborting");
                if(clientListener!=null) {
                    clientListener.onFailure(response);
                }
                return;
            }

            Log.i(tag,"starting new link");
            links.get(0).call.enqueue(this);
        }
    };

    private class Link {
        Call call;
        Listener listener;
    }

    public interface Listener{
        boolean onResponse(CallbackChain chain, RestResponse response);
        boolean onFailure(CallbackChain chain, RestResponse response);
    }
}
