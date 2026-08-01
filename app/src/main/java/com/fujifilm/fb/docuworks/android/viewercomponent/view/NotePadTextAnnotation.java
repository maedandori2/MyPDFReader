package com.fujifilm.fb.docuworks.android.viewercomponent.view;

/* JADX INFO: loaded from: classes.dex */
class NotePadTextAnnotation extends Annotation {
    private static final long serialVersionUID = 1;
    private boolean mEditAble = true;
    private NotePadAnnotation mNotePadAnnotation = new NotePadAnnotation();
    private TextAnnotation mTextAnnotation = new TextAnnotation();

    public NotePadAnnotation getNotePadAnnotation() {
        return this.mNotePadAnnotation;
    }

    public TextAnnotation getTextAnnotation() {
        return this.mTextAnnotation;
    }

    public boolean isEditAble() {
        return this.mEditAble;
    }

    public void setEditAble(boolean z) {
        this.mEditAble = z;
    }

    public void setNotePadAnnotation(NotePadAnnotation notePadAnnotation) {
        super.setIsFixed(notePadAnnotation.isFixed());
        this.mNotePadAnnotation = notePadAnnotation;
    }

    public void setTextAnnotation(TextAnnotation textAnnotation) {
        this.mTextAnnotation = textAnnotation;
    }
}
