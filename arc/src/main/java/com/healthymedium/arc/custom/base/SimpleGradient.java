package com.healthymedium.arc.custom.base;

import android.graphics.LinearGradient;
import android.graphics.Shader;

public class SimpleGradient {

    public static final int LINEAR_HORIZONTAL = 0;
    public static final int LINEAR_VERTICAL = 1;

    int id;
    int color0;
    int color1;
    Shader.TileMode tileMode;

    public SimpleGradient(int enumeratedValue){
        id = enumeratedValue;
    }

    public int getId() {
        return id;
    }

    Shader getShader(int width, int height){
        switch (id){
            case LINEAR_VERTICAL:
                return new LinearGradient(0, 0, 0, height, color0, color1, tileMode);
            case LINEAR_HORIZONTAL:
                return new LinearGradient(0,0,width,0,color0,color1,tileMode);
        }
        return null;
    }

    void setColor0(int color){
        this.color0 = color;
    }

    void setColor1(int color){
        this.color1 = color;
    }

    void setTileMode(Shader.TileMode tileMode){
        this.tileMode = tileMode;
    }

}
