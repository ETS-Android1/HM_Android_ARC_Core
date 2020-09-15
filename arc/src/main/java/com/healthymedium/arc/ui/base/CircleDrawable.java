package com.healthymedium.arc.ui.base;


import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

public class CircleDrawable extends SimpleDrawable {

    public CircleDrawable(){
        super();
    }

    @Override
    protected void updateOffsets() {
        // create a rect that's small enough that the stroke isn't cut off
        int offset = (int) (strokeWidth/2);
        rect.set(offset,offset,width-offset,height-offset);
    }

    protected Path getPath(Rect rect) {

        Path path = new Path();
        float radius = Math.min(rect.width() / 2, rect.height() / 2);
        path.addCircle(rect.centerX(),rect.centerY(),radius, Path.Direction.CCW);
        path.close();

        return path;
    }


}
