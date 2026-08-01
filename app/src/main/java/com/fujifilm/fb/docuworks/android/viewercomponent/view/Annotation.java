package com.fujifilm.fb.docuworks.android.viewercomponent.view;

import android.graphics.Color;
import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
class Annotation implements Serializable {
    public static final int ANNO_COLOR_ALPHA = 255;
    public static final int ANNO_COLOR_BLUE = 16711680;
    public static final int ANNO_COLOR_GREEN = 65280;
    public static final int ANNO_COLOR_RED = 255;
    public static final int BITMAP_TYPE = 12;
    public static final int ELLIPSE_TYPE = 6;
    public static final int ERROR_TYPE = -1;
    public static final int FREEHAND_TYPE = 2;
    public static final int HEX = 16;
    public static final int LINE_TYPE = 4;
    public static final int NOTEPAD_TYPE = 3;
    public static final int OCTAL = 8;
    public static final int POLYGON_TYPE = 7;
    public static final int RECTANGLE_TYPE = 5;
    public static final int TEXT_TYPE = 1;
    public static final int UNKNOWN_TYPE = 0;
    private static final long serialVersionUID = 1;
    protected int mIndex;
    protected int mPageCheck;
    protected int mType;
    protected int mWidth = 0;
    protected int mHeight = 0;
    protected int mPosX = 0;
    protected int mPosY = 0;
    protected String mText = "";
    protected int mAnnotationColor = 0;
    protected boolean mIsSignature = false;
    protected boolean mIsFixed = false;

    public String getAnnotText() {
        return this.mText;
    }

    public int getAnnotationColor() {
        int i = this.mAnnotationColor;
        return Color.argb(255, i & 255, (65280 & i) >> 8, (i & ANNO_COLOR_BLUE) >> 16);
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getIndex() {
        return this.mIndex;
    }

    public int getPageCheck() {
        return this.mPageCheck;
    }

    public int getType() {
        return this.mType;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getX() {
        return this.mPosX;
    }

    public int getY() {
        return this.mPosY;
    }

    public boolean isFixed() {
        return this.mIsFixed;
    }

    public boolean isSignature() {
        return this.mIsSignature;
    }

    public void setAnnoX(int i) {
        this.mPosX = i;
    }

    public void setAnnoY(int i) {
        this.mPosY = i;
    }

    public void setAnnotText(String str) {
        this.mText = str;
    }

    public void setAnnotationColor(int i) {
        this.mAnnotationColor = i;
    }

    public void setHeight(int i) {
        this.mHeight = i;
    }

    public void setIndex(int i) {
        this.mIndex = i;
    }

    public void setIsFixed(boolean z) {
        this.mIsFixed = z;
    }

    public void setIsSignature(boolean z) {
        this.mIsSignature = z;
    }

    public void setPageCheck(int i) {
        this.mPageCheck = i;
    }

    public void setType(int i) {
        this.mType = i;
    }

    public void setWidth(int i) {
        this.mWidth = i;
    }
}
