package com.mypdf.reader

import android.util.Base64
import android.util.Log
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec

/**
 * Tạo JWT để xác thực Google Service Account.
 * Không cần thư viện ngoài — chỉ dùng java.security có sẵn trong Android.
 */
object ServiceAccountJwt {

    private const val TAG = "ServiceAccountJwt"
    private const val SCOPE = "https://www.googleapis.com/auth/drive"
    private const val TOKEN_URL = "https://oauth2.googleapis.com/token"

    fun create(clientEmail: String, privateKeyPem: String): String {
        // Header
        val header = base64url("""{"alg":"RS256","typ":"JWT"}""".toByteArray())

        // Payload
        val now = System.currentTimeMillis() / 1000
        val payload = base64url(
            """
            {
                "iss": "$clientEmail",
                "scope": "$SCOPE",
                "aud": "$TOKEN_URL",
                "exp": ${now + 3600},
                "iat": $now
            }
            """.trimIndent().toByteArray()
        )

        val signingInput = "$header.$payload"

        // Ký bằng RS256
        val privateKey = loadPrivateKey(privateKeyPem)
        val sig = Signature.getInstance("SHA256withRSA")
        sig.initSign(privateKey)
        sig.update(signingInput.toByteArray())
        val signature = base64url(sig.sign())

        return "$signingInput.$signature"
    }

    private fun loadPrivateKey(pem: String): java.security.PrivateKey {
        // Xóa header/footer PEM và khoảng trắng
        val cleaned = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace("\n", "")
            .replace("\r", "")
            .trim()

        val keyBytes = Base64.decode(cleaned, Base64.DEFAULT)
        val spec = PKCS8EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(spec)
    }

    private fun base64url(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}
