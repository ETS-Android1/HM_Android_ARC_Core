package com.healthymedium.arc.paths.setup_v2;

import android.annotation.SuppressLint;

import com.healthymedium.arc.api.RestClient;
import com.healthymedium.arc.api.RestResponse;
import com.healthymedium.arc.api.models.AuthDetails;
import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.core.Config;
import com.healthymedium.arc.core.LoadingDialog;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.navigation.NavigationManager;
import com.healthymedium.arc.path_data.SetupPathData;
import com.healthymedium.arc.paths.templates.SetupTemplate;
import com.healthymedium.arc.study.PathSegment;
import com.healthymedium.arc.study.Study;
import com.healthymedium.arc.utilities.ViewUtil;

@SuppressLint("ValidFragment")
public class Setup2ParticipantConfirm extends Setup2Template {

    public static boolean DEBUG_USE_SETUP_RATER = false;

    CharSequence previousCharacterSequence = "";

    public Setup2ParticipantConfirm(int firstDigitCount, int secondDigitCount) {
        super(firstDigitCount, secondDigitCount, ViewUtil.getString(R.string.login_confirm_ARCID));
    }

    @Override
    protected boolean isFormValid(CharSequence sequence) {
        if (sequence.toString().equals(previousCharacterSequence.toString())) {
            return true;
        } else {
            showError(Application.getInstance().getResources().getString(R.string.login_error1));
            return false;
        }
    }

    @Override
    protected boolean shouldAutoProceed() {
        return true;
    }

    @Override
    public void onResume() {
        String id = ((SetupPathData)Study.getCurrentSegmentData()).id;
        previousCharacterSequence = id;
        super.onResume();
    }

    @Override
    protected void onNextRequested() {
        super.onNextRequested();

        if(Config.REST_BLACKHOLE) {
            PathSegment path = Study.getCurrentSegment();

            if(DEBUG_USE_SETUP_RATER){
                if(!fragmentExists(path,Setup2AuthRater.class)){
                    path.fragments.add(new Setup2AuthRater(6));
                }
            }else{
                if(!fragmentExists(path,Setup2AuthConfirm.class)){
                    path.fragments.add(new Setup2AuthConfirm(6));
                }
            }


            Study.getInstance().openNextFragment();
            return;
        }

        SetupPathData setupPathData = ((SetupPathData)Study.getCurrentSegmentData());
        loadingDialog = new LoadingDialog();
        loadingDialog.show(getFragmentManager(),"LoadingDialog");
        Study.getRestClient().requestAuthDetails(setupPathData.id, authDetailsListener);
    }

    private boolean fragmentExists(PathSegment path, Class tClass) {
        int last = path.fragments.size()-1;
        String oldName = path.fragments.get(last).getSimpleTag();
        String newName = tClass.getSimpleName();
        return oldName.equals(newName);
    }

    RestClient.Listener authDetailsListener = new RestClient.Listener() {
        @Override
        public void onSuccess(RestResponse response) {
            String errorString = parseForError(response,false);
            if(errorString!=null) {
                showError(errorString);
                loadingDialog.dismiss();
                return;
            }

            SetupPathData setupPathData = ((SetupPathData)Study.getCurrentSegmentData());
            AuthDetails authDetails = response.getOptionalAs(AuthDetails.class);
            PathSegment path = Study.getCurrentSegment();

            String authType = authDetails.getType();
            int authLength = authDetails.getCodeLength();

            if(authType.equals(AuthDetails.TYPE_RATER)){
                if(!fragmentExists(path,Setup2AuthRater.class)){
                    path.fragments.add(new Setup2AuthRater(authLength));
                }
                loadingDialog.dismiss();
                Study.openNextFragment();

            } else if(authType.equals(AuthDetails.TYPE_CONFIRM_CODE)) {
                if(!fragmentExists(path,Setup2AuthConfirm.class)){
                    path.fragments.add(new Setup2AuthConfirm(authLength));
                }
                Study.getRestClient().requestVerificationCode(setupPathData.id, verificationCodeListener);

            } else if(authType.equals(AuthDetails.TYPE_MANUAL)) {
                if(!fragmentExists(path,Setup2AuthManual.class)){
                    path.fragments.add(new Setup2AuthManual(authLength));
                }
                loadingDialog.dismiss();
                Study.openNextFragment();

            }

        }

        @Override
        public void onFailure(RestResponse response) {
            String errorString = parseForError(response,true);
            showError(errorString);
            loadingDialog.dismiss();
        }
    };

    RestClient.Listener verificationCodeListener = new RestClient.Listener() {
        @Override
        public void onSuccess(RestResponse response) {
            String errorString = parseForError(response,false);
            loadingDialog.dismiss();
            if(errorString==null) {
                Study.openNextFragment();
            } else {
                showError(errorString);
            }
        }

        @Override
        public void onFailure(RestResponse response) {
            String errorString = parseForError(response,true);
            showError(errorString);
            loadingDialog.dismiss();
        }
    };

}
