package com.fujifilm.fb.docuworks.android.viewercomponent.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.fujifilm.fb.docuworks.android.viewercomponent.exception.JNIException;
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
    private String mFilePath;
    private boolean mIsProtectedByPassword;
    private boolean mIsProtected = false;
    private DWSecurityKind mProtectedType = null;
    private boolean mIsPasswordEmpty = false;
    private String mPasswordComment = null;

    public DWDocumentProtectedState(String str) {
        this.mFilePath = null;
        this.mIsProtectedByPassword = false;
        this.mFilePath = str;
        this.mIsProtectedByPassword = isProtectedByPassword();
    }

    String a(DWSecurityKind dWSecurityKind, Context context) {
        return "";
    }

    public String getPasswordComment() {
        return this.mPasswordComment;
    }

    public DWSecurityKind getProtectedType() {
        return this.mProtectedType;
    }

    public boolean hasAdditionalProhibit(Context context) {
        if (context == null) {
            return true;
        }
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean("isForbiddenLocalSave", false) || defaultSharedPreferences.getBoolean("isForbiddenCopy", false);
    }

    public boolean isAnnotationEditForbidden() {
        return false;
    }

    public boolean isCopyForbidden() {
        return false;
    }

    public boolean isDocumentEditForbidden() {
        return false;
    }

    public boolean isLimitAnnotationEditOperation(Context context) {
        return context == null || PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isForbiddenLocalSave", false) || isAnnotationEditForbidden();
    }

    public boolean isLimitCopyOperation(Context context) {
        return context == null || PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isForbiddenCopy", false) || isCopyForbidden();
    }

    public boolean isLimitShareOperation(Context context) {
        if (context == null) {
            return true;
        }
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isForbiddenLocalSave", false);
    }

    public boolean isPasswordEmpty() {
        return this.mIsPasswordEmpty;
    }

    public boolean isPrintForbidden() {
        return false;
    }

    public boolean isProtected() {
        return this.mIsProtected;
    }

    public boolean isProtectedByPassword() {
        DWSecurityKind dWSecurityKind = this.mProtectedType;
        return dWSecurityKind == DWSecurityKind.DW_PROTECTED_BY_PSWD || dWSecurityKind == DWSecurityKind.DW_PROTECTED_BY_PSWD128 || dWSecurityKind == DWSecurityKind.DW_PROTECTED_BY_PSWD256;
    }
}
