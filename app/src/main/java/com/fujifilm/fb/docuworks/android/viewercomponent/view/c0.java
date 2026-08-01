package com.fujifilm.fb.docuworks.android.viewercomponent.view;

import android.content.Context;
import android.graphics.Canvas;
import com.fujifilm.fb.docuworks.android.viewercomponent.exception.JNIException;

/* JADX INFO: compiled from: DrawingHandler.java */
/* JADX INFO: loaded from: classes.dex */
public interface c0 {
    void a(Context context, int i, int i2) throws JNIException;

    boolean createCanvasAndBitmap(int i, int i2) throws JNIException;

    boolean drawDoublePage(Canvas canvas, int i, int i2, int i3, int i4, float f, int i5, float f2, float f3, float f4, float f5, float f6, float f7);

    boolean drawDoublePageEx(Canvas canvas, int i, int i2, int i3, int i4, float f, int i5, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, float f16) throws JNIException;

    boolean drawPage(Canvas canvas, int i, int i2, float f, int i3, float f2, float f3, float f4, float f5, float f6, float f7) throws JNIException;

    boolean drawPageEx(Canvas canvas, int i, int i2, float f, int i3, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, float f16) throws JNIException;

    boolean isAllTiledsOver();

    boolean isCurrentTiledsOver();

    boolean preDraw(int i, int i2, float f, float f2, float f3, float f4, float f5) throws JNIException;
}
