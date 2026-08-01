package com.fujifilm.fb.docuworks.android.viewercomponent.exception;

/* JADX INFO: loaded from: classes.dex */
public class JNIException extends Exception {
    private static final long serialVersionUID = 1;
    private int mErrorCode;

    public JNIException() {
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public JNIException(String str) {
        super(str);
    }

    public JNIException(Throwable th) {
        super(th);
    }

    public JNIException(String str, Throwable th) {
        super(str, th);
    }
}
