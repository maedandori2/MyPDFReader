package jp.co.fujixerox.docuworks.android.viewercomponent.view;
import android.content.Context;
import java.io.Serializable;

public class DWDocumentProtectedState implements Serializable {
    public static final String ADDITIONAL_PROHIBIT = "ADDITIONAL_PROHIBIT";
    public static final String IS_FORBIDDEN_COPY = "isForbiddenCopy";
    public static final String IS_FORBIDDEN_LOCALSAVE = "isForbiddenLocalSave";
    private static final int MP_PERM_ANNO_EDIT = 4;
    private static final int MP_PERM_COPY = 16;
    private static final int MP_PERM_DOC_EDIT = 2;
    private static final int MP_PERM_PRINT = 8;
    public static final int PROHIBIT_NULL = 0;
    public static final int PROHIBIT_SAVE_COPY = 1;
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
    
    public boolean isAnnotationEditForbidden() { return false; }
    public boolean isCopyForbidden() { return false; }
    public boolean isDocumentEditForbidden() { return false; }
    public boolean isPrintForbidden() { return false; }
    public boolean isLimitCopyOperation(Context context) { return false; }
    public boolean isLimitAnnotationEditOperation(Context context) { return false; }
    public boolean isLimitShareOperation(Context context) { return false; }
    public boolean hasAdditionalProhibit(Context context) { return false; }
}
