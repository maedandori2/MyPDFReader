package com.fujifilm.fb.docuworks.android.viewercomponent.view;

/* JADX INFO: loaded from: classes.dex */
class LineAnnotation extends Annotation {
    private static final long serialVersionUID = 1;
    private int mArrowStyle;
    private int mArrowType;
    private long mBorderColor;
    private int mBorderStyle;
    private boolean mBorderTransparent;
    private int mBorderWidth;
    private int mEndPointX;
    private int mEndPointY;
    private int mStartPointX;
    private int mStartPointY;

    LineAnnotation() {
    }

    public int getArrowStyle() {
        return this.mArrowStyle;
    }

    public int getArrowType() {
        return this.mArrowType;
    }

    public long getBorderColor() {
        return this.mBorderColor;
    }

    public int getBorderStyle() {
        return this.mBorderStyle;
    }

    public boolean getBorderTransparent() {
        return this.mBorderTransparent;
    }

    public int getBorderWidth() {
        return this.mBorderWidth;
    }

    public int getEndPointX() {
        return this.mEndPointX;
    }

    public int getEndPointY() {
        return this.mEndPointY;
    }

    public int getStartPointX() {
        return this.mStartPointX;
    }

    public int getStartPointY() {
        return this.mStartPointY;
    }

    public void setArrowStyle(int i) {
        this.mArrowStyle = i;
    }

    public void setArrowType(int i) {
        this.mArrowType = i;
    }

    public void setBorderColor(long j) {
        this.mBorderColor = j;
    }

    public void setBorderStyle(int i) {
        this.mBorderStyle = i;
    }

    public void setBorderTransparent(boolean z) {
        this.mBorderTransparent = z;
    }

    public void setBorderWidth(int i) {
        this.mBorderWidth = i;
    }

    public void setEndPointX(int i) {
        this.mEndPointX = i;
    }

    public void setEndPointY(int i) {
        this.mEndPointY = i;
    }

    public void setStartPointX(int i) {
        this.mStartPointX = i;
    }

    public void setStartPointY(int i) {
        this.mStartPointY = i;
    }
}
