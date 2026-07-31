package jp.co.fujixerox.docuworks.android.viewercomponent.view;
import android.graphics.Color;
import java.io.Serializable;
public class Annotation implements Serializable {
    public static final int ANNO_COLOR_ALPHA = 255;
    public static final int ANNO_COLOR_BLUE = 16711680;
    public static final int ANNO_COLOR_GREEN = 65280;
    public static final int ANNO_COLOR_RED = 255;
    private static final long serialVersionUID = 1;
    protected int mIndex;
    protected int mPageCheck;
    protected int mType;
    protected int mWidth = 0;
    protected int mHeight = 0;
    protected int mPosX = 0;
    protected int mPosY = 0;
    protected String mText = "";
    protected int mAnnotationColor = 0;
    protected boolean mIsSignature = false;
    protected boolean mIsFixed = false;
    public void setIsSignature(boolean z) { this.mIsSignature = z; }
    public boolean isSignature() { return this.mIsSignature; }
    public int getWidth() { return this.mWidth; }
    public void setWidth(int i) { this.mWidth = i; }
    public int getHeight() { return this.mHeight; }
    public void setHeight(int i) { this.mHeight = i; }
    public int getX() { return this.mPosX; }
    public void setAnnoX(int i) { this.mPosX = i; }
    public int getY() { return this.mPosY; }
    public void setAnnoY(int i) { this.mPosY = i; }
    public String getAnnotText() { return this.mText; }
    public void setAnnotText(String str) { this.mText = str; }
    public int getAnnotationColor() { return this.mAnnotationColor; }
    public void setAnnotationColor(int i) { this.mAnnotationColor = i; }
    public void setIndex(int i) { this.mIndex = i; }
    public int getIndex() { return this.mIndex; }
    public void setPageCheck(int i) { this.mPageCheck = i; }
    public int getPageCheck() { return this.mPageCheck; }
    public int getType() { return this.mType; }
    public void setType(int i) { this.mType = i; }
    public boolean isFixed() { return this.mIsFixed; }
    public void setIsFixed(boolean z) { this.mIsFixed = z; }
}
