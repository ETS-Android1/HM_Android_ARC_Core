package com.healthymedium.arc.api.tests;

import com.healthymedium.arc.api.tests.data.ChronotypeSurvey;
import com.healthymedium.arc.api.tests.data.ContextSurvey;
import com.healthymedium.arc.api.tests.data.GridTest;
import com.healthymedium.arc.api.tests.data.PriceTest;
import com.healthymedium.arc.api.tests.data.SymbolTest;
import com.healthymedium.arc.api.tests.data.WakeSurvey;

public class CognitiveTest extends BaseTest {

    public ChronotypeSurvey chronotype_survey;
    public ContextSurvey context_survey;
    public WakeSurvey wake_survey;
    public SymbolTest symbol_test;
    public PriceTest price_test;
    public GridTest grid_test;

    public CognitiveTest(){
        type = "cognitive";
    }

}
