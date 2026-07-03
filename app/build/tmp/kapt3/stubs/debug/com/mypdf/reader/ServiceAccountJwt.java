package com.mypdf.reader;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0002J\u0016\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u0004J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0004H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/mypdf/reader/ServiceAccountJwt;", "", "()V", "SCOPE", "", "TAG", "TOKEN_URL", "base64url", "data", "", "create", "clientEmail", "privateKeyPem", "loadPrivateKey", "Ljava/security/PrivateKey;", "pem", "app_debug"})
public final class ServiceAccountJwt {
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String TAG = "ServiceAccountJwt";
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String SCOPE = "https://www.googleapis.com/auth/drive";
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String TOKEN_URL = "https://oauth2.googleapis.com/token";
    @org.jetbrains.annotations.NotNull
    public static final com.mypdf.reader.ServiceAccountJwt INSTANCE = null;
    
    private ServiceAccountJwt() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String create(@org.jetbrains.annotations.NotNull
    java.lang.String clientEmail, @org.jetbrains.annotations.NotNull
    java.lang.String privateKeyPem) {
        return null;
    }
    
    private final java.security.PrivateKey loadPrivateKey(java.lang.String pem) {
        return null;
    }
    
    private final java.lang.String base64url(byte[] data) {
        return null;
    }
}