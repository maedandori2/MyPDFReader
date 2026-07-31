package jp.co.fujixerox.docuworks.android.dwlib;

public class DWWriteFailedException extends Exception {
    public DWWriteFailedException() {}
    public DWWriteFailedException(String s) { super(s); }
    public DWWriteFailedException(int code) {}
}
