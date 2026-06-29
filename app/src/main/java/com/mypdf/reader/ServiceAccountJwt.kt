package com.mypdf.reader

import android.util.Base64
import android.util.Log
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec

object ServiceAccountJwt {

    private const val TAG = "ServiceAccountJwt"
    private const val SCOPE = "https://www.googleapis.com/auth/drive"
    private const val TOKEN_URL = "https://oauth2.googleapis.com/token"

    fun create(clientEmail: String, privateKeyPem: String): String {
        // Header - phải dùng compact JSON không có space
        val headerJson = """{"alg":"RS256","typ":"JWT"}"""
        val header = base64url(headerJson.toByteArray(Charsets.UTF_8))

        // Payload - compact JSON không có space
        val now = System.currentTimeMillis() / 1000
        val payloadJson = "{\"iss\":\"$clientEmail\",\"scope\":\"$SCOPE\",\"aud\":\"$TOKEN_URL\",\"exp\":${now + 3600},\"iat\":$now}"
        val payload = base64url(payloadJson.toByteArray(Charsets.UTF_8))

        val signingInput = "$header.$payload"
        Log.d(TAG, "Signing input length: ${signingInput.length}")

        // Load private key và ký
        val privateKey = loadPrivateKey(privateKeyPem)
        val sig = Signature.getInstance("SHA256withRSA")
        sig.initSign(privateKey)
        sig.update(signingInput.toByteArray(Charsets.UTF_8))
        val signature = base64url(sig.sign())

        return "$signingInput.$signature"
    }

    private fun loadPrivateKey(pem: String): java.security.PrivateKey {
        Log.d(TAG, "Raw PEM first 50 chars: ${pem.take(50)}")
        Log.d(TAG, "Raw PEM length: ${pem.length}")

        // Android JSONObject.getString() KHÔNG tự unescape \n
        // Phải replace thủ công trước
        val withRealNewlines = pem
            .replace("\\n", "\n")  // \n literal → newline thật
            .replace("\\r", "")    // xóa \r nếu có

        Log.d(TAG, "Contains real newlines: ${withRealNewlines.contains('\n')}")

        // Xóa header/footer và tất cả whitespace
        val base64Only = withRealNewlines
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replace("\n", "")
            .replace("\r", "")
            .replace(" ", "")
            .trim()

        Log.d(TAG, "Base64 key length: ${base64Only.length}")
        Log.d(TAG, "Base64 key first 20: ${base64Only.take(20)}")

        if (base64Only.length < 100) {
            throw IllegalArgumentException("Private key quá ngắn (${base64Only.length} chars) - file JSON bị lỗi")
        }

        val keyBytes = Base64.decode(base64Only, Base64.DEFAULT)
        Log.d(TAG, "Key bytes length: ${keyBytes.size}")

        val spec = PKCS8EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(spec)
    }

    private fun base64url(data: ByteArray): String {
        return Base64.encodeToString(
            data,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
    }
}
