package com.healthymedium.arc.core.TestEnumerations;

public enum PriceTestStyle
{
    ORIGINAL("original"),
    REVISED("revised");

    private String style;

    PriceTestStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }
}