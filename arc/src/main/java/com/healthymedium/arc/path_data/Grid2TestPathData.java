package com.healthymedium.arc.path_data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import android.util.Log;
import com.healthymedium.arc.api.DoubleTypeAdapter;
import com.healthymedium.arc.api.ItemTypeAdapterFactory;
import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.api.tests.data.GridTest;
import com.healthymedium.arc.api.tests.data.GridTestImage;
import com.healthymedium.arc.api.tests.data.GridTestSection;
import com.healthymedium.arc.api.tests.data.GridTestTap;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.study.PathSegmentData;
import com.healthymedium.arc.time.TimeUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class Grid2TestPathData extends PathSegmentData {

    DateTime start;
    List<Grid2TestPathData.Section> sections = new ArrayList<>();

    public Grid2TestPathData(){
        super();
    }

    public void markStarted() {
        start = DateTime.now();
    }

    public boolean hasStarted(){
        return start!=null;
    }

    public void startNewSection(){
        sections.add(new Grid2TestPathData.Section());
    }

    public Grid2TestPathData.Section getCurrentSection(){
        return sections.get(sections.size()-1);
    }

    public void updateCurrentSection(Grid2TestPathData.Section section){
        sections.set(sections.size()-1,section);
    }

    public List<Grid2TestPathData.Section> getSections() {
        return sections;
    }

    public void setSections(List<Grid2TestPathData.Section> sections) {
        this.sections = sections;
    }

    @Override
    protected BaseData onProcess() {
        GridTest test = new GridTest();

        test.sections = new ArrayList<>();

        long startTime = 0;

        if(start != null)
        {
            test.date = TimeUtil.toUtcDouble(start);
            startTime = start.getMillis();
        }

        for(Grid2TestPathData.Section section : sections){
            GridTestSection testSection = new GridTestSection();
            testSection.eCount = section.eCount;
            testSection.fCount = section.fCount;

            testSection.images = new ArrayList<>();
            for(Grid2TestPathData.Image image : section.images){
                GridTestImage testImage = new GridTestImage();
                testImage.x = image.x;
                testImage.y = image.y;
                testImage.image = image.image;
                testSection.images.add(testImage);
            }

            testSection.choices = new ArrayList<>();
            for(Grid2TestPathData.Tap tap : section.choices){
                GridTestTap testTap = new GridTestTap();
                testTap.x = tap.x;
                testTap.y = tap.y;
                if(start != null)
                {
                    testTap.imageSelectionTime = Double.valueOf((tap.imageSelectionTime - startTime) / (double) 1000);
                    testTap.gridSelectionTime = Double.valueOf((tap.gridSelectionTime - startTime) / (double) 1000);
                    testTap.image = tap.image;
                }
                testSection.choices.add(testTap);
            }

            if(start != null)
            {
                testSection.displayDistraction = Double.valueOf((section.displayTimeDistraction - startTime) / (double)1000);
                testSection.displayTestGrid = Double.valueOf((section.displayTimeTestGrid - startTime) / (double)1000);
                testSection.displaySymbols = Double.valueOf((section.displayTimeSymbols - startTime) / (double)1000);
            }
            test.sections.add(testSection);
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .registerTypeAdapter(Double.class,new DoubleTypeAdapter())
                .setPrettyPrinting()
                .setLenient()
                .create();
        String string = gson.toJson(test);
        Log.i("test",string);
        return test;

    }

    public class Section {

        private long displayTimeSymbols;
        private long displayTimeDistraction;
        private long displayTimeTestGrid;
        private int eCount;
        private int fCount;
        private List<Grid2TestPathData.Image> images = new ArrayList<>();
        private List<Grid2TestPathData.Tap> choices = new ArrayList<>();

        public Section(){

        }

        public void markSymbolsDisplayed() {
            displayTimeSymbols = System.currentTimeMillis();
        }

        public void markDistractionDisplayed() {
            displayTimeDistraction = System.currentTimeMillis();
        }

        public void markTestGridDisplayed() {
            displayTimeTestGrid = System.currentTimeMillis();
        }

        public void setECount(int eCount) {
            this.eCount = eCount;
        }

        public void setFCount(int fCount) {
            this.fCount = fCount;
        }

        public List<Grid2TestPathData.Image> getImages() {
            return images;
        }

        public void setImages(List<Grid2TestPathData.Image> images) {
            this.images = images;
        }

        public List<Grid2TestPathData.Tap> getChoices() {
            return choices;
        }

        public void setChoices(List<Grid2TestPathData.Tap> choices) {
            this.choices = choices;
        }

    }

    public static class Tap {

        private long imageSelectionTime;
        private long gridSelectionTime;
        private String image;
        private int x;
        private int y;

        public Tap(int x, int y, int image, long gridSelectionTime, long imageSelectionTime){
            this.imageSelectionTime = imageSelectionTime;
            this.gridSelectionTime = gridSelectionTime;
            this.image = Image.fromId(image);
            this.x = x;
            this.y = y;
        }

        public Tap(){

        }

    }

    public static class Image {

        public static transient final String PHONE = "phone";
        public static transient final String PEN = "pen";
        public static transient final String KEY = "key";

        private String image;
        private int x;
        private int y;

        public Image(int row,int col,String name) {
            image = name;
            x = row;
            y = col;
        }

        public String name() {
            return image;
        }

        public int row() {
            return x;
        }

        public int column() {
            return y;
        }

        public int id() {
            if(image.equals(PHONE)) {
                return R.drawable.phone;
            }
            if(image.equals(PEN)) {
                return R.drawable.pen;
            }
            if(image.equals(KEY)) {
                return R.drawable.key;
            }
            return 0;
        }

        static public String fromId(int id) {
            if(id==R.drawable.phone){
                return Image.PHONE;
            } else if(id==R.drawable.pen){
                return Image.PEN;
            } else if(id==R.drawable.key){
                return Image.KEY;
            }
            return "?";
        }

    }

}
