package jp.co.fujixerox.supk;

public final class supkInterface {
    public static native int nkz_i(int i, byte[] bArr, byte[] bArr2);
    public static native byte[] nkz_s(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int[] iArr);

    static {
        System.loadLibrary("supkBase64");
    }
}
