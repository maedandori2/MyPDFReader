package com.fujifilm.fb.docuworks.android.viewercomponent.view;

/* JADX INFO: loaded from: classes.dex */
class NotePadAnnotation extends Annotation {
    private static final long serialVersionUID = 1;
    private boolean mAutoResize;
    private int mChildCount;
    private long mNotePadColor;
    private int mLeft = 0;
    private int mTop = 0;
    private int mRight = 0;
    private int mBottom = 0;

    NotePadAnnotation() {
    }

    public int getBottom() {
        return this.mBottom;
    }

    public int getChildCount() {
        return this.mChildCount;
    }

    public int getLeft() {
        return this.mLeft;
    }

    public long getNotePadColor() {
        return this.mNotePadColor;
    }

    public int getRight() {
        return this.mRight;
    }

    public int getTop() {
        return this.mTop;
    }

    public boolean isAutoResize() {
        return this.mAutoResize;
    }

    public void setAutoResize(boolean z) {
        this.mAutoResize = z;
    }

    public void setBottom(int i) {
        this.mBottom = i;
    }

    public void setChildCount(int i) {
        this.mChildCount = i;
    }

    public void setLeft(int i) {
        this.mLeft = i;
    }

    public void setNotePadColor(long j) {
        this.mNotePadColor = j;
    }

    public void setRight(int i) {
        this.mRight = i;
    }

    public void setTop(int i) {
        this.mTop = i;
    }
}
