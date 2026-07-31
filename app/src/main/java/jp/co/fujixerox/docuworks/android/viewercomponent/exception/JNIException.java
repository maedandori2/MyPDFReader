package jp.co.fujixerox.docuworks.android.viewercomponent.exception;

public class JNIException extends Exception {
    private static final long serialVersionUID = 1;
    private int mErrorCode;

    public JNIException() {
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

    public int getErrorCode() {
        return this.mErrorCode;
    }
}
