package com.fujifilm.fb.docuworks.android.viewercomponent.view;

/* JADX INFO: loaded from: classes.dex */
class TextAnnotation extends Annotation {
    private static final long serialVersionUID = 1;
    private long mFontColor;
    private String mFontName;
    private float mFontSize;
    private String mText;
    private int mLeft = 0;
    private int mTop = 0;
    private int mRight = 0;
    private int mBottom = 0;
    private int mFontCharSet = 0;
    private int mWrapStyle = 0;
    private int mFontStyle = 0;
    private float mFontAngle = 0.0f;
    private boolean mIsFontVertical = false;
    private boolean mIsFillHas = false;
    private long mFillColor = 0;

    TextAnnotation() {
    }

    public int getBottom() {
        return this.mBottom;
    }

    public long getFillColor() {
        return this.mFillColor;
    }

    public float getFontAngle() {
        return this.mFontAngle;
    }

    public int getFontCharSet() {
        return this.mFontCharSet;
    }

    public long getFontColor() {
        return this.mFontColor;
    }

    public String getFontName() {
        return this.mFontName;
    }

    public float getFontSize() {
        return this.mFontSize;
    }

    public int getFontStyle() {
        return this.mFontStyle;
    }

    public int getLeft() {
        return this.mLeft;
    }

    public int getRight() {
        return this.mRight;
    }

    public String getText() {
        return this.mText;
    }

    public int getTop() {
        return this.mTop;
    }

    public int getWrapStyle() {
        return this.mWrapStyle;
    }

    public boolean isFillHas() {
        return this.mIsFillHas;
    }

    public boolean isFontVertical() {
        return this.mIsFontVertical;
    }

    public void setBottom(int i) {
        this.mBottom = i;
    }

    public void setFillColor(long j) {
        this.mFillColor = j;
    }

    public void setFillHas(boolean z) {
        this.mIsFillHas = z;
    }

    public void setFontAngle(float f) {
        this.mFontAngle = f;
    }

    public void setFontCharSet(int i) {
        this.mFontCharSet = i;
    }

    public void setFontColor(long j) {
        this.mFontColor = j;
    }

    public void setFontName(String str) {
        this.mFontName = str;
    }

    public void setFontSize(float f) {
        this.mFontSize = f;
    }

    public void setFontStyle(int i) {
        this.mFontStyle = i;
    }

    public void setFontVertical(boolean z) {
        this.mIsFontVertical = z;
    }

    public void setLeft(int i) {
        this.mLeft = i;
    }

    public void setRight(int i) {
        this.mRight = i;
    }

    public void setText(String str) {
        this.mText = str;
    }

    public void setTop(int i) {
        this.mTop = i;
    }

    public void setWrapStyle(int i) {
        this.mWrapStyle = i;
    }
}
