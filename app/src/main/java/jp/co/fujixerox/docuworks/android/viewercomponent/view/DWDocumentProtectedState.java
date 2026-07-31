package jp.co.fujixerox.docuworks.android.viewercomponent.view;
import java.io.Serializable;
public class DWDocumentProtectedState implements Serializable {
    private static final long serialVersionUID = 1;
    public DWDocumentProtectedState(String str) { }
    public boolean isProtected() { return false; }
    public DWSecurityKind getProtectedType() { return DWSecurityKind.DW_NO_PROTECT; }
    public boolean isProtectedByPassword() { return false; }
    public boolean isPasswordEmpty() { return true; }
    public String getPasswordComment() { return ""; }
}
