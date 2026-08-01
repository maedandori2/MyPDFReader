package com.fujifilm.fb.docuworks.android.viewercomponent.view;

/* JADX INFO: loaded from: classes.dex */
class LinkAnnotation extends Annotation {
    private static final long serialVersionUID = 1;
    private String mFileNameForLinkAnnoInside;
    private String mLinkJumpTitleString;
    private int mLinkTitleCharSet;
    private int mLinkToAnnoAttribute;
    private int mLinkToDWType;
    private int mLinkToPageNum;
    private int mLinkToType;
    private String mLinkXDWPath;
    private String mMailaddressString;
    private String mOtherFilePathString;
    private String mSheafnameString;
    private String mUrlString;

    public int getAnnotationKind() {
        return this.mLinkToDWType;
    }

    public String getFileNameForLinkAnnoInside() {
        return this.mFileNameForLinkAnnoInside;
    }

    public String getLinkJumpTitleString() {
        return this.mLinkJumpTitleString;
    }

    public int getLinkTitleCharSet() {
        return this.mLinkTitleCharSet;
    }

    public int getLinkTo() {
        return this.mLinkToType;
    }

    public int getLinkToAnnoAttribute() {
        return this.mLinkToAnnoAttribute;
    }

    public int getLinkToPageNum() {
        return this.mLinkToPageNum;
    }

    public String getLinkXDWPath() {
        return this.mLinkXDWPath;
    }

    public String getMailaddressString() {
        return this.mMailaddressString;
    }

    public String getOtherFilePathString() {
        return this.mOtherFilePathString;
    }

    @Override // com.fujifilm.fb.docuworks.android.viewercomponent.view.Annotation
    public int getPageCheck() {
        return this.mPageCheck;
    }

    public String getSheafnameString() {
        return this.mSheafnameString;
    }

    public String getUrlString() {
        return this.mUrlString;
    }

    public void setAnnotationKind(int i) {
        this.mLinkToDWType = i;
    }

    public void setFileNameForLinkAnnoInside(String str) {
        this.mFileNameForLinkAnnoInside = str;
    }

    public void setLinkJumpTitleString(String str) {
        this.mLinkJumpTitleString = str;
    }

    public void setLinkTitleCharSet(int i) {
        this.mLinkTitleCharSet = i;
    }

    public void setLinkTo(int i) {
        this.mLinkToType = i;
    }

    public void setLinkToAnnoAttribute(int i) {
        this.mLinkToAnnoAttribute = i;
    }

    public void setLinkToPageNum(int i) {
        this.mLinkToPageNum = i;
    }

    public void setLinkXDWPath(String str) {
        this.mLinkXDWPath = str;
    }

    public void setMailaddressString(String str) {
        this.mMailaddressString = str;
    }

    public void setOtherFilePathString(String str) {
        this.mOtherFilePathString = str;
    }

    @Override // com.fujifilm.fb.docuworks.android.viewercomponent.view.Annotation
    public void setPageCheck(int i) {
        this.mPageCheck = i;
    }

    public void setSheafnameString(String str) {
        this.mSheafnameString = str;
    }

    public void setUrlString(String str) {
        this.mUrlString = str;
    }
}
