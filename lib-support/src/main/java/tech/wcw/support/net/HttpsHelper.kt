package tech.wcw.support.net

import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * author: wcw
 * date: 2020/7/14
 * des: https
 */
class HttpsHelper {
    companion object {
        @JvmStatic
        fun sslSocketFactory(): SSLSocketFactory {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(
                null,
                trustManager(), SecureRandom()
            )
            return sslContext.socketFactory
        }

        @JvmStatic
        fun trustManager(): Array<X509TrustManager> {
            return Array(1) {
                object : X509TrustManager {
                    override fun checkClientTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                    ) {
                    }

                    override fun checkServerTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return emptyArray()
                    }
                }
            }

        }

        @JvmStatic
        fun hostnameVerifier(): HostnameVerifier {
            return HostnameVerifier { _, _ -> true }
        }


        @JvmStatic
        fun sslSocketFactory(certificates: InputStream): SSLSocketFactory {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val ca = certificateFactory.generateCertificate(certificates)
            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            keyStore.setCertificateEntry("ca", ca)
            val defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val trustManagerFactory = TrustManagerFactory.getInstance(defaultAlgorithm)
            trustManagerFactory.init(keyStore)
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustManagerFactory.trustManagers, SecureRandom())
            return sslContext.socketFactory
        }

    }
}