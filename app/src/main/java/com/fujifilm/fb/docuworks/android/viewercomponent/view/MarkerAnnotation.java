package com.fujifilm.fb.docuworks.android.viewercomponent.view;

import android.graphics.Point;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class MarkerAnnotation extends Annotation {
    private static final long serialVersionUID = 1;
    private long mColor;
    private boolean mIsTransparent;
    private List<Point> mPoints = new ArrayList();
    private int mSize;

    MarkerAnnotation() {
    }

    public long getColor() {
        return this.mColor;
    }

    public Point getPoint(int i) {
        return this.mPoints.get(i);
    }

    public int getPointNum() {
        return this.mPoints.size();
    }

    public List<Point> getPoints() {
        return this.mPoints;
    }

    public int getSize() {
        return this.mSize;
    }

    public boolean isTransparent() {
        return this.mIsTransparent;
    }

    public void setColor(long j) {
        this.mColor = j;
    }

    public void setIsTransparent(boolean z) {
        this.mIsTransparent = z;
    }

    public void setPoint(Point point) {
        this.mPoints.add(point);
    }

    public void setPoints(List<Point> list) {
        this.mPoints = list;
    }

    public void setSize(int i) {
        this.mSize = i;
    }
}
