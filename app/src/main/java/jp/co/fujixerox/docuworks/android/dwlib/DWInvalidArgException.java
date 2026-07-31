package jp.co.fujixerox.docuworks.android.dwlib;

public class DWInvalidArgException extends Exception {
    public DWInvalidArgException() {}
    public DWInvalidArgException(String s) { super(s); }
    public DWInvalidArgException(int code) {}
}
