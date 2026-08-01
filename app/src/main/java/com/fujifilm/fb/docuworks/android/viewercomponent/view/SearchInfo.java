package com.fujifilm.fb.docuworks.android.viewercomponent.view;

import android.graphics.Point;
import android.graphics.Rect;
import java.io.Serializable;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class SearchInfo implements Serializable {
    private static final long serialVersionUID = 1;
    private int mAnnoIndex;
    private int mDocIndex;
    private int mEndDocIndex;
    private int mEndPageIndex;
    private int mPageIndex;
    private int mStartDocIndex;
    private int mStartPageIndex;
    private String mSearchText = "";
    private TextInfo mTextInfo = new TextInfo();
    private transient Rect mAnnoRect = new Rect();

    public int getAnnoIndex() {
        return this.mAnnoIndex;
    }

    public Rect getAnnoRect() {
        return this.mAnnoRect;
    }

    public int getDocIndex() {
        return this.mDocIndex;
    }

    public int getEndDocIndex() {
        return this.mEndDocIndex;
    }

    public int getEndPageIndex() {
        return this.mEndPageIndex;
    }

    public int getPageIndex() {
        return this.mPageIndex;
    }

    public String getSearchText() {
        return this.mSearchText;
    }

    public int getStartDocIndex() {
        return this.mStartDocIndex;
    }

    public int getStartPageIndex() {
        return this.mStartPageIndex;
    }

    public TextInfo getTextInfo() {
        return this.mTextInfo;
    }

    public void reset() {
        this.mSearchText = "";
        this.mTextInfo.reset();
        this.mAnnoRect.set(0, 0, 0, 0);
        this.mDocIndex = 0;
        this.mPageIndex = 0;
        this.mStartDocIndex = 0;
        this.mStartPageIndex = 0;
        this.mAnnoIndex = -1;
    }

    public void setAnnoIndex(int i) {
        this.mAnnoIndex = i;
    }

    public void setAnnoRect(Rect rect) {
        this.mAnnoRect = rect;
    }

    public void setDocIndex(int i) {
        this.mDocIndex = i;
    }

    public void setEndDocIndex(int i) {
        this.mEndDocIndex = i;
    }

    public void setEndPageIndex(int i) {
        this.mEndPageIndex = i;
    }

    public void setPageIndex(int i) {
        this.mPageIndex = i;
    }

    public void setSearchResult(SearchInfo searchInfo) {
        this.mAnnoIndex = searchInfo.mAnnoIndex;
        this.mDocIndex = searchInfo.mDocIndex;
        this.mEndDocIndex = searchInfo.mEndDocIndex;
        this.mEndPageIndex = searchInfo.mEndPageIndex;
        this.mPageIndex = searchInfo.mPageIndex;
        this.mSearchText = searchInfo.mSearchText;
        this.mStartDocIndex = searchInfo.mStartDocIndex;
        this.mStartPageIndex = searchInfo.mStartPageIndex;
        this.mTextInfo.setEndIndex(searchInfo.mTextInfo.getEndIndex());
        this.mTextInfo.setSelectedTextType(searchInfo.mTextInfo.getSelectedTextType());
        this.mTextInfo.setStartIndex(searchInfo.mTextInfo.getStartIndex());
        this.mTextInfo.setTextLength(searchInfo.mTextInfo.getTextLength());
        this.mTextInfo.setTextLengthWithLF(searchInfo.mTextInfo.getTextLengthWithLF());
        this.mTextInfo.setTextRectCount(searchInfo.mTextInfo.getTextRectCount());
        List<TextRect> textRects = this.mTextInfo.getTextRects();
        textRects.clear();
        List<TextRect> textRects2 = searchInfo.getTextInfo().getTextRects();
        for (int i = 0; i < textRects2.size(); i++) {
            TextRect textRect = new TextRect();
            TextRect textRect2 = textRects2.get(i);
            int size = textRect2.getPoints().size();
            for (int i2 = 0; i2 < size; i2++) {
                Point point = textRect2.getPoints().get(i2);
                textRect.setPoint(new Point(point.x, point.y));
            }
            textRects.add(textRect);
        }
        this.mAnnoRect = searchInfo.getAnnoRect();
    }

    public void setSearchText(String str) {
        this.mSearchText = str;
    }

    public void setStartDocIndex(int i) {
        this.mStartDocIndex = i;
    }

    public void setStartPageIndex(int i) {
        this.mStartPageIndex = i;
    }

    public void setTextInfo(TextInfo textInfo) {
        this.mTextInfo = textInfo;
    }
}
