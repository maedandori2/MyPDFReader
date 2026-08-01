package com.fujifilm.fb.docuworks.android.viewercomponent.view;

import android.graphics.Point;
import android.graphics.Rect;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class TextInfo implements Serializable {
    private static final long serialVersionUID = 1;
    private int mEndIndex;
    private SelectedTextType mSelectedTextType;
    private int mStartIndex;
    private int mTextLength;
    private int mTextLengthWithLF;
    private int mTextRectCount;
    private List<TextRect> mTextRects = new ArrayList();

    public enum SelectedTextType {
        TEXT_SELECTED_TYPE_NIL,
        TEXT_SELECTED_TYPE_EMF,
        TEXT_SELECTED_TYPE_WMF,
        TEXT_SELECTED_TYPE_OCR,
        TEXT_SELECTED_TYPE_TEXT_ANN,
        TEXT_SELECTED_TYPE_STAMP_ANN,
        TEXT_SELECTED_TYPE_HEADER_FOOTER
    }

    public TextInfo() {
    }

    public int getEndIndex() {
        return this.mEndIndex;
    }

    public SelectedTextType getSelectedTextType() {
        return this.mSelectedTextType;
    }

    public int getStartIndex() {
        return this.mStartIndex;
    }

    public int getTextLength() {
        return this.mTextLength;
    }

    public int getTextLengthWithLF() {
        return this.mTextLengthWithLF;
    }

    public TextRect getTextRect(int i) {
        return this.mTextRects.get(i);
    }

    public int getTextRectCount() {
        return this.mTextRectCount;
    }

    public List<TextRect> getTextRects() {
        return this.mTextRects;
    }

    public void reset() {
        this.mStartIndex = 0;
        this.mEndIndex = 0;
        this.mTextLength = 0;
        this.mTextLengthWithLF = 0;
        this.mSelectedTextType = SelectedTextType.TEXT_SELECTED_TYPE_NIL;
        this.mTextRectCount = 0;
        this.mTextRects.clear();
    }

    public void setEndIndex(int i) {
        this.mEndIndex = i;
    }

    public void setSelectedTextType(SelectedTextType selectedTextType) {
        this.mSelectedTextType = selectedTextType;
    }

    public void setStartIndex(int i) {
        this.mStartIndex = i;
    }

    public void setTextLength(int i) {
        this.mTextLength = i;
    }

    public void setTextLengthWithLF(int i) {
        this.mTextLengthWithLF = i;
    }

    public void setTextRect(TextRect textRect) {
        this.mTextRects.add(textRect);
    }

    public void setTextRectCount(int i) {
        this.mTextRectCount = i;
    }

    public TextInfo(TextInfo textInfo) {
        this.mStartIndex = textInfo.getStartIndex();
        this.mEndIndex = textInfo.getEndIndex();
        this.mTextLength = textInfo.getTextLength();
        this.mTextLengthWithLF = textInfo.getTextLengthWithLF();
        this.mSelectedTextType = textInfo.getSelectedTextType();
        this.mTextRectCount = textInfo.getTextRectCount();
        this.mTextRects.clear();
        List<TextRect> textRects = textInfo.getTextRects();
        for (int i = 0; i < textRects.size(); i++) {
            TextRect textRect = new TextRect();
            TextRect textRect2 = textRects.get(i);
            textRect.setOrientation(textRect2.getOrientation());
            textRect.setOrigin(new Point(textRect2.getOrigin()));
            textRect.setRect(new Rect(textRect2.getRect()));
            int size = textRect2.getPoints().size();
            for (int i2 = 0; i2 < size; i2++) {
                Point point = textRect2.getPoints().get(i2);
                textRect.setPoint(new Point(point.x, point.y));
            }
            this.mTextRects.add(textRect);
        }
    }
}
