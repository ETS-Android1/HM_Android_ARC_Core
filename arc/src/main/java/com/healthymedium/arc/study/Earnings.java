package com.healthymedium.arc.study;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.api.models.EarningDetails;
import com.healthymedium.arc.api.models.EarningOverview;

public class Earnings {

    private EarningOverview overview;
    private int overviewRefresh;

    private EarningDetails details;
    private int detailsRefresh;

    public Earnings(){
        overviewRefresh = 0;
        detailsRefresh = 0;
    }

    public void refreshOverview(final Listener listener){

        RestClient client = Study.getRestClient();

        if(!client.isUploadQueueEmpty()){
            if(listener!=null){
                listener.onFailure();
            }
            return;
        }

        // check the upload queue first. if it has anything, mark failed
        overview = null;
        overviewRefresh = -1;

        client.getEarningOverview(new RestClient.Listener() {
            @Override
            public void onSuccess(RestResponse response) {
                overviewRefresh = 1;
                overview = response.getOptionalAs(EarningOverview.class);
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

    public EarningOverview getOverview(){
        return overview;
    }

    public void refreshDetails(final Listener listener){

        RestClient client = Study.getRestClient();

        // check the upload queue first. if it has anything, mark failed
        if(!client.isUploadQueueEmpty()){
            if(listener!=null){
                listener.onFailure();
            }
            return;
        }

        details = null;
        detailsRefresh = -1;

        client.getEarningDetails(new RestClient.Listener() {
            @Override
            public void onSuccess(RestResponse response) {
                detailsRefresh = 1;
                details = response.getOptionalAs(EarningDetails.class);
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

    public EarningDetails getDetails(){
        return details;
    }

    public void invalidate(){
        overview = null;
        overviewRefresh = 0;

        details = null;
        detailsRefresh = 0;
    }

    public interface Listener {
        void onSuccess();
        void onFailure();
    }

}
