package com.healthymedium.arc.study;

import android.support.annotation.Nullable;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.api.models.EarningDetails;
import com.healthymedium.arc.api.models.EarningOverview;

import org.joda.time.DateTime;

public class Earnings {

    private EarningOverview overview;
    private int overviewRefresh;
    private DateTime overviewUpdateTime;

    private String prevWeeklyTotal = new String();
    private String prevStudyTotal = new String();

    private EarningDetails details;
    private int detailsRefresh;
    private DateTime detailsUpdateTime;

    public Earnings(){
        overviewRefresh = 0;
        detailsRefresh = 0;
    }

    public void refreshOverview(final Listener listener){

        overviewRefresh = -1;

        final RestClient client = Study.getRestClient();

        if(client.isUploading()) {
            client.addUploadListener(new RestClient.UploadListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onStop() {
                    client.removeUploadListener(this);
                    if(client.isUploadQueueEmpty()) {
                        internalRefreshOverview(listener);
                    } else if(listener!=null){
                        overviewRefresh = 0;
                        listener.onFailure();
                    }
                }
            });
        } else if(client.isUploadQueueEmpty()) {
            internalRefreshOverview(listener);
        } else {
            overviewRefresh = 0;
            if(listener!=null){
                listener.onFailure();
            }
        }

    }

    private void internalRefreshOverview(final Listener listener){

        final RestClient client = Study.getRestClient();

        client.getEarningOverview(new RestClient.Listener() {
            @Override
            public void onSuccess(RestResponse response) {
                overviewRefresh = 1;
                overview = response.getOptionalAs(EarningOverview.class);
                overviewUpdateTime = DateTime.now();
                if(listener!=null){
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(RestResponse response) {
                overviewRefresh = 0;
                if(listener!=null){
                    listener.onFailure();
                }
            }
        });
    }

    public boolean isRefreshingOverview(){
        return overviewRefresh==-1;
    }

    public boolean hasCurrentOverview(){
        return overviewRefresh==1;
    }

    public EarningOverview getOverview(){
        return overview;
    }

    @Nullable
    public DateTime getOverviewRefreshTime(){
        return overviewUpdateTime;
    }

    public String getPrevWeeklyTotal() {
        return prevWeeklyTotal;
    }

    public String getPrevStudyTotal() {
        return prevStudyTotal;
    }

    public void refreshDetails(final Listener listener){

        detailsRefresh = -1;

        final RestClient client = Study.getRestClient();

        if(client.isUploading()) {
            client.addUploadListener(new RestClient.UploadListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onStop() {
                    client.removeUploadListener(this);
                    if(client.isUploadQueueEmpty()) {
                        internalRefreshDetails(listener);
                    } else if(listener!=null){
                        detailsRefresh = 0;
                        listener.onFailure();
                    }
                }
            });
        } else if(client.isUploadQueueEmpty()) {
            internalRefreshDetails(listener);
        } else {
            detailsRefresh = 0;
            if(listener!=null){
                listener.onFailure();
            }
        }

    }

    private void internalRefreshDetails(final Listener listener){

        final RestClient client = Study.getRestClient();

        client.getEarningOverview(new RestClient.Listener() {
            @Override
            public void onSuccess(RestResponse response) {
                detailsRefresh = 1;
                details = response.getOptionalAs(EarningDetails.class);
                detailsUpdateTime = DateTime.now();
                if(listener!=null){
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(RestResponse response) {
                detailsRefresh = 0;
                if(listener!=null){
                    listener.onFailure();
                }
            }
        });
    }

    public boolean isRefreshingDetails(){
        return detailsRefresh==-1;
    }

    public boolean hasCurrentDetails(){
        return detailsRefresh==1;
    }

    public EarningDetails getDetails(){
        return details;
    }

    @Nullable
    public DateTime getDetailsRefreshTime(){
        return detailsUpdateTime;
    }

    public void invalidate(){
        if(overview!=null){
            prevStudyTotal = overview.total_earnings;
            prevWeeklyTotal = overview.cycle_earnings;
        }
        overviewRefresh = 0;
        detailsRefresh = 0;
    }

    public interface Listener {
        void onSuccess();
        void onFailure();
    }

}
