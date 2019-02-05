package com.healthymedium.arc.path_data;

import android.location.Location;

import com.healthymedium.arc.api.tests.data.LocationSurvey;
import com.healthymedium.arc.api.tests.data.LocationSurveySection;
import com.healthymedium.arc.study.PathSegmentData;

import java.util.ArrayList;
import java.util.Map;

public class LocationPathData extends PathSegmentData {

    public LocationPathData(){
        super();
    }

    @Override
    protected Object onProcess() {

        LocationSurvey survey = new LocationSurvey();
        survey.questions = new ArrayList<>();

        int size = objects.size();
        for (int i=0;i<size;i++) {
            Map<String, Object> response = (Map<String, Object>) objects.get(i);
            LocationSurveySection surveySection = processHashMap(response, LocationSurveySection.class);

            if(i==0){
                survey.start_date = surveySection.display_time;
            } else if(survey.start_date > surveySection.display_time){
                survey.start_date = surveySection.display_time;
            }

            surveySection.question_id = "location " + Integer.toString(i+1);
            survey.questions.add(surveySection);
        }

        return survey;

    }
}
