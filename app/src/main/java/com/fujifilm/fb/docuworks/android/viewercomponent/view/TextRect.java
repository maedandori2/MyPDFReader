package com.fujifilm.fb.docuworks.android.viewercomponent.view;

import android.graphics.Point;
import android.graphics.Rect;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class TextRect implements Serializable {
    private static final long serialVersionUID = 1;
    private int mOrientation;
    private transient Point mOrigin;
    private transient List<Point> mPoints = new ArrayList();
    private transient Rect mRect;

    public int getOrientation() {
        return this.mOrientation;
    }

    public Point getOrigin() {
        return this.mOrigin;
    }

    public Point getPoint(int i) {
        return this.mPoints.get(i);
    }

    public List<Point> getPoints() {
        return this.mPoints;
    }

    public Rect getRect() {
        return this.mRect;
    }

    public void setOrientation(int i) {
        this.mOrientation = i;
    }

    public void setOrigin(Point point) {
        this.mOrigin = point;
    }

    public void setPoint(Point point) {
        this.mPoints.add(point);
    }

    public void setRect(Rect rect) {
        this.mRect = rect;
    }
}
