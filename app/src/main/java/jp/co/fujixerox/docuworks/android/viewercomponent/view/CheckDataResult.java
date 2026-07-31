package jp.co.fujixerox.docuworks.android.viewercomponent.view;
public class CheckDataResult {
    private long mErrorCode;
    private long mNthOrg;
    private long mNthPage;
    private long mOthers;
    public long getErrorCode() { return this.mErrorCode; }
    public void setErrorCode(long j) { this.mErrorCode = j; }
    public long getNthPage() { return this.mNthPage; }
    public void setNthPage(long j) { this.mNthPage = j; }
    public long getNthOrg() { return this.mNthOrg; }
    public void setNthOrg(long j) { this.mNthOrg = j; }
    public long getOthers() { return this.mOthers; }
    public void setOthers(long j) { this.mOthers = j; }
}
