package com.healthymedium.arc.ui.base;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

public class ChipDrawable extends SimpleDrawable {

    public ChipDrawable(){
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

        int radius = height;

        path.moveTo(rect.left + radius, rect.top);

        // top line
        path.lineTo(rect.right - radius, rect.top);

        // right arc
        RectF rightRect = new RectF(rect.right - radius, rect.top, rect.right, rect.bottom);
        path.arcTo(rightRect, 270F, 180F, false);

        // bottom line
        path.lineTo(rect.left + radius, rect.bottom);

        // left arc
        RectF leftRect = new RectF(rect.left, rect.top, rect.left + radius, rect.bottom);
        path.arcTo(leftRect, 90F, 180F, false);

        path.close();

        return path;
    }


}
