package com.healthymedium.arc.path_data;

import com.healthymedium.arc.api.tests.data.SymbolTest;
import com.healthymedium.arc.api.tests.data.SymbolTestSection;
import com.healthymedium.arc.study.PathSegmentData;
import com.healthymedium.arc.time.JodaUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class SymbolsTestPathData extends PathSegmentData {

    private DateTime start;
    private List<Section> sections = new ArrayList<>();

    public SymbolsTestPathData(){
        super();

        for(int i=0;i<12;i++){
            sections.add(new Section());
        }
    }

    public void markStarted(){
        start = DateTime.now();
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    protected Object onProcess() {
        SymbolTest test = new SymbolTest();
        test.date = JodaUtil.toUtcDouble(start);
        test.sections = new ArrayList<>();

        long startTime = start.getMillis();

        for(Section section : sections){
            SymbolTestSection testSection = new SymbolTestSection();
            testSection.appearanceTime = Double.valueOf((section.appearanceTime - startTime) / (double)1000);
            testSection.selectionTime = Double.valueOf((section.selectionTime - startTime) / (double)1000);
            testSection.selected = section.selected;
            testSection.correct = section.correct;
            testSection.choices = section.choices;
            testSection.options = section.options;
            test.sections.add(testSection);
        }

        return test;
    }

    public class Section {

        private long appearanceTime;
        private long selectionTime;
        private int selected;
        private int correct;
        private List<List<String>> options = new ArrayList<>();
        private List<List<String>> choices = new ArrayList<>();

        public void markAppearanceTime() {
            this.appearanceTime = System.currentTimeMillis();
        }

        public void setSelected(int selected, long selectionTime) {
            this.selected = selected;
            this.selectionTime = selectionTime;
        }

        public void setCorrect(Integer correct) {
            this.correct = correct;
        }

        public void setOptions(List<List<String>> options) {
            this.options = options;
        }

        public void setChoices(List<List<String>> choices) {
            this.choices = choices;
        }

    }
}
