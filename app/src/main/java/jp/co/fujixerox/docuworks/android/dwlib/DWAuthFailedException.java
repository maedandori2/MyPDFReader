package jp.co.fujixerox.docuworks.android.dwlib;

public class DWAuthFailedException extends Exception {
    public DWAuthFailedException() {}
    public DWAuthFailedException(String s) { super(s); }
    public DWAuthFailedException(int code) {}
}
