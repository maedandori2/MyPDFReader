package com.fujifilm.fb.docuworks.android.viewercomponent.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import com.fujifilm.fb.docuworks.android.viewercomponent.exception.JNIException;
import java.nio.Buffer;

/* JADX INFO: loaded from: classes.dex */
public class BaseBridge implements c0 {
    public static Bitmap cache = null;
    private static BaseBridge mBaseBridge = null;
    private static int mDevFullHeight = 0;
    private static int mDevFullWidth = 0;
    public static boolean mUseSkiaPortWithoutOSSkiaSymbols = false;
    private DWAuthData mAuthData;
    private String mFilePath;
    private long mDocumentHandle = 0;
    private final int LAGEPAGESIZE = 1024;

    private enum CPU_FEATURES {
        NO_ARM(0),
        ARMv5TE(1),
        ARMEABIV7A(2),
        ARMEABIV7A_NEON(3),
        X86(4),
        ARM64(5);

        private final int value;

        CPU_FEATURES(int i) {
            this.value = i;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static boolean mIsLibraryLoaded = false;

    static {
        try {
            System.loadLibrary("c++_shared");
            try { System.loadLibrary("opencv_java4"); } catch(Throwable t){}
            try { System.loadLibrary("supkBase64"); } catch(Throwable t){}
            System.loadLibrary("cpufd");
            System.loadLibrary("icudata");
            
            String[] abis = android.os.Build.SUPPORTED_ABIS;
            boolean isArm64 = false;
            for (String abi : abis) {
                if (abi.contains("arm64") || abi.contains("x86_64")) {
                    isArm64 = true;
                    break;
                }
            }
            
            if (isArm64) {
                System.loadLibrary("DWLibraryForAndroid_SP_VFP_NEON");
            } else {
                try {
                    System.loadLibrary("DWLibraryForAndroid_SP_VFP");
                } catch (Throwable t) {
                    System.loadLibrary("DWLibraryForAndroid_SP_VFP_NEON");
                }
            }
            
            if (!isUseOSSkiaSymbols()) {
                mUseSkiaPortWithoutOSSkiaSymbols = true;
            }
            mIsLibraryLoaded = true;
        } catch (Throwable th) {
            android.util.Log.e("BaseBridge", "Failed to load new native libraries", th);
            mIsLibraryLoaded = false;
        }
        mDevFullWidth = 0;
        mDevFullHeight = 0;
        cache = null;
    }

    public static boolean isLibraryLoaded() {
        return mIsLibraryLoaded;
    }

    BaseBridge() {
    }

    public static BaseBridge b() {
        if (mBaseBridge == null) {
            mBaseBridge = new BaseBridge();
        }
        return mBaseBridge;
    }

    private static native int getCPUFeatures();

    private native int getPageImage(int i, float f, Object obj, int i2, int i3);

    private static native boolean isUseOSSkiaSymbols();

    static void pasteBitmap(Buffer buffer, int i, int i2) {
        if (cache == null || i != mDevFullWidth || i2 != mDevFullHeight) {
            mDevFullWidth = i;
            mDevFullHeight = i2;
            Bitmap bitmap = cache;
            if (bitmap != null) {
                bitmap.recycle();
            }
            cache = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        }
        cache.copyPixelsFromBuffer(buffer);
    }

    public long a() {
        return this.mDocumentHandle;
    }

    @Override // com.fujifilm.fb.docuworks.android.viewercomponent.view.c0
    public void a(Context context, int i, int i2) {
    }

    public native int addAnnotationFromAnnFile2File(String str, String str2) throws JNIException;

    public native int addLoginAnnotationToLocal(int i, int i2, int i3, String str) throws JNIException;

    public native int addLoginAnnotationToPage(int i, int i2, int i3, int i4, int i5, String str) throws JNIException;

    public native boolean canChangeAnnotationDisplayMode();

    native boolean canRotatePage(int i);

    public native void cancelSearch();

    public native CheckDataResult checkData();

    public native int checkDocumentWithAuthData(String str, DWAuthData dWAuthData);

    public native void closeDocument();

    native int confirmPassword(String str, String str2);

    public native boolean copyAnnotation(int i, int i2, int i3);

    native int copyDocument(String str, int i, String str2);

    native int copyPage(String str, int i, String str2);

    public native boolean createAnnotation(int i, int i2, Annotation annotation);

    @Override // com.fujifilm.fb.docuworks.android.viewercomponent.view.c0
    public native boolean createCanvasAndBitmap(int i, int i2);

    native int createDocumentFromJpegFile(String str, String str2, float f, float f2, float f3, float f4, float f5, float f6);

    native int createEmptyBinder(String str);

    native int createEmptyDocument(String str);

    public native boolean createTextAnnotation(int i, int i2, Annotation annotation);

    public native boolean deleteAnnotation(int i, int i2, int i3);

    native int deleteDocuments(int i, int i2);

    public native int deleteLoginAnnotation(int i, String str) throws JNIException;

    native int deletePages(int i, int i2);

    public native void disableAnnotationDisplay();

    @Override // com.fujifilm.fb.docuworks.android.viewercomponent.view.c0
    public native boolean drawDoublePage(Canvas canvas, int i, int i2, int i3, int i4, float f, int i5, float f2, float f3, float f4, float f5, float f6, float f7);

    @Override // com.fujifilm.fb.docuworks.android.viewercomponent.view.c0
    public native boolean drawDoublePageEx(Canvas canvas, int i, int i2, int i3, int i4, float f, int i5, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, float f16);

    @Override // com.fujifilm.fb.docuworks.android.viewercomponent.view.c0
    public native boolean drawPage(Canvas canvas, int i, int i2, float f, int i3, float f2, float f3, float f4, float f5, float f6, float f7);

    @Override // com.fujifilm.fb.docuworks.android.viewercomponent.view.c0
    public native boolean drawPageEx(Canvas canvas, int i, int i2, float f, int i3, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, float f16);

    public native boolean editAnnotation(int i, int i2, int i3, Annotation annotation, int i4, boolean z);

    public native void enableAnnotationDisplay();

    public native void enterBackGround();

    public native Object[] getAllLoginAnnotation(String str) throws JNIException;

    public native int getAnnoCountForPage(int i, int i2);

    public native int getAnnoKind(int i, int i2, int i3);

    public native boolean getAnnotationDisplayMode();

    public native Annotation getAnnotationInfo(int i, int i2, int i3, int i4);

    public native Annotation getAnnotationParameter(int i, int i2, int i3, int i4);

    public native int getBoundBottomForPage(int i, int i2);

    public native int getBoundLeftForPage(int i, int i2);

    public native int getBoundRightForPage(int i, int i2);

    public native int getBoundTopForPage(int i, int i2);

    public native long getColorForPostitAnno(int i, int i2, int i3);

    native int getContents(int i, String str);

    native String getContentsName(int i);

    native long getContentsSize(int i);

    native int getDocSummaryInformation(DWDocSummaryInfo dWDocSummaryInfo);

    native String getDocumentName(int i);

    public native int getDocumentVersion();

    public native long getFilePermission(String str) throws JNIException;

    public native int getHeightForAnno(int i, int i2, int i3);

    public native int getHeightWithAnnoForPage(int i, int i2);

    public native String getLinkJumpTitle(int i, int i2, int i3);

    public native String getLinkTitle(int i, int i2, int i3);

    public native int getLinkTitleCharSet(int i, int i2, int i3);

    public native int getLinkTo(int i, int i2, int i3);

    public native int getLinkToAnnoAttribute(int i, int i2, int i3);

    public native int getLinkToPageNum(int i, int i2, int i3);

    public native String getLinkXDWPathString(int i, int i2, int i3);

    public native int getLoginAnnotationImage(int i, int i2, int i3, Object obj, String str) throws JNIException;

    public native String getMailAddress(int i, int i2, int i3);

    native int getNumberOfContents();

    native int getNumberOfDocuments();

    public native int getNumberOfFontEmbedded();

    public native int getNumberOfPages();

    native int getNumberOfPagesOfDocument(int i);

    public native String getOtherFilePath(int i, int i2, int i3);

    public native int getPageCheck(int i, int i2, int i3);

    native int getPageRect(int i, RectF rectF);

    public native int getPaperHeightForPage(int i, int i2);

    native int getPaperRect(int i, RectF rectF);

    public native int getPaperWidthForPage(int i, int i2);

    native String getPasswordComment(String str);

    public native int getPosXForAnno(int i, int i2, int i3);

    public native int getPosYForAnno(int i, int i2, int i3);

    public native int getReadAnnotationCreation(int i, String str) throws JNIException;

    public native int getReadAnnotationNumber(String str) throws JNIException;

    public native Annotation getReadAnnotationParameter(int i, String str, boolean z) throws JNIException;

    public native int getSecurityInfo(DWDocumentProtectedState dWDocumentProtectedState);

    public native String getSheafName(int i, int i2, int i3);

    public native String getTextForAnno(int i, int i2, int i3);

    public native String getTextInAnnotation(int i, int i2, int i3);

    public native String getTextInPage(int i, int i2, long j, Object obj);

    public native boolean getTextInfo(int i, int i2, long j, long j2, long j3, long j4, Object obj);

    public native int getTextType(int i, int i2, int i3, int i4, Annotation annotation);

    native boolean getThumbnailImg(int i, float f, int i2, float f2, float f3, int i3, int i4, Object obj, Object obj2);

    public native String getUrl(int i, int i2, int i3);

    public native int getWidthForAnno(int i, int i2, int i3);

    public native int getWidthWithAnnoForPage(int i, int i2);

    public native int getXbdDocumentCount();

    public native int getXbdPageCount();

    public native String getXdwDocumentName(int i);

    public native int getXdwPageCount(int i);

    native boolean hasSignatures();

    public native int howManyAnnotations(int i, int i2, int i3);

    public native int initAnnEdit(int i, int i2, DrawerStatusObservable drawerStatusObservable);

    public native int initDocEdit();

    public native int initTiledLayer(DrawerStatusObservable drawerStatusObservable) throws JNIException;

    native int insertDocument(String str, String str2, int i);

    native int insertDocumentToBinder(String str, String str2, int i);

    @Override // com.fujifilm.fb.docuworks.android.viewercomponent.view.c0
    public native boolean isAllTiledsOver();

    public native boolean isBinder();

    public native boolean isContentDoc();

    @Override // com.fujifilm.fb.docuworks.android.viewercomponent.view.c0
    public native boolean isCurrentTiledsOver();

    public native boolean isFontEmbedded() throws JNIException;

    public native boolean isLinkAnno(int i, int i2, int i3);

    public native boolean isNotePadAnno(int i, int i2, int i3);

    public native int isReadAnnotation(String str);

    native void lockDrawingForThumbnail();

    public native boolean moveAnnotation(int i, int i2, int i3, int i4, int i5);

    native int moveDocument(int i, int i2);

    native int movePage(int i, int i2);

    native int movePageInBinder(int i, int i2, int i3, int i4);

    public native int openCopiesOfDocument(String str);

    public native int openCopiesOfDocumentWithAuthData(String str, DWAuthData dWAuthData) throws JNIException;

    public native int openDocument(String str, int i) throws JNIException;

    public native int openDocumentWithAuthData(String str, DWAuthData dWAuthData, int i) throws JNIException;

    public native boolean pasteAnnotation(int i, int i2, int i3, int i4);

    @Override // com.fujifilm.fb.docuworks.android.viewercomponent.view.c0
    public native boolean preDraw(int i, int i2, float f, float f2, float f3, float f4, float f5);

    public native int readAnnotationToLoginAnnotation(String str, String str2) throws JNIException;

    native void releaseDrawingForThumbnail();

    native int rotatePage(int i, int i2);

    public native int saveAnnotationEdit();

    public native int saveAs(String str);

    public native int saveEditInfoToFile();

    public native int searchBackward(Object obj);

    public native int searchForward(Object obj);

    public native boolean setDrawingEnv(int i, int i2, int i3);

    public native boolean setTempEnv(String str);

    public native int startSearch(Object obj);

    public int a(int i, float f, Canvas canvas, int i2, int i3) {
        return getPageImage(i, f, canvas, i2, i3);
    }

    public int a(int i, float f, Bitmap bitmap, int i2, int i3) {
        return getPageImage(i, f, bitmap, i2, i3);
    }
}
