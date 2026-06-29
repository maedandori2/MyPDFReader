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
        // Header
        val header = base64url("""{"alg":"RS256","typ":"JWT"}""".toByteArray())

        // Payload
        val now = System.currentTimeMillis() / 1000
        val payloadJson = "{" +
            "\"iss\":\"$clientEmail\"," +
            "\"scope\":\"$SCOPE\"," +
            "\"aud\":\"$TOKEN_URL\"," +
            "\"exp\":${now + 3600}," +
            "\"iat\":$now" +
            "}"
        val payload = base64url(payloadJson.toByteArray(Charsets.UTF_8))

        val signingInput = "$header.$payload"

        // Ký bằng RS256
        val privateKey = loadPrivateKey(privateKeyPem)
        val sig = Signature.getInstance("SHA256withRSA")
        sig.initSign(privateKey)
        sig.update(signingInput.toByteArray(Charsets.UTF_8))
        val signature = base64url(sig.sign())

        return "$signingInput.$signature"
    }

    private fun loadPrivateKey(pem: String): java.security.PrivateKey {
        // Bước 1: convert \n literal (từ JSON string) thành newline thật
        val normalized = pem.replace("\\n", "\n")

        // Bước 2: xóa header/footer PEM
        val cleaned = normalized
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replace("\n", "")
            .replace("\r", "")
            .trim()

        Log.d(TAG, "Private key length after clean: ${cleaned.length}")

        val keyBytes = Base64.decode(cleaned, Base64.DEFAULT)
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
