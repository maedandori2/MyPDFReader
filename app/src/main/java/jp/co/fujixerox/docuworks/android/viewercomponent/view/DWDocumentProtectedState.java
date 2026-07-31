package jp.co.fujixerox.docuworks.android.viewercomponent.view;
import java.io.Serializable;
public class DWDocumentProtectedState implements Serializable {
    private static final long serialVersionUID = 1;
    
    private String mFilePath = null;
    private boolean mIsProtectedByPassword = false;
    private boolean mIsProtected = false;
    private DWSecurityKind mProtectedType = null;
    private boolean mIsPasswordEmpty = false;
    private String mPasswordComment = null;

    public DWDocumentProtectedState(String str) { 
        this.mFilePath = str;
    }
    public boolean isProtected() { return this.mIsProtected; }
    public DWSecurityKind getProtectedType() { return this.mProtectedType; }
    public boolean isProtectedByPassword() { return this.mIsProtectedByPassword; }
    public boolean isPasswordEmpty() { return this.mIsPasswordEmpty; }
    public String getPasswordComment() { return this.mPasswordComment; }
}
