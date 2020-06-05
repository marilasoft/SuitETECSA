package cu.marilasoft.suitetecsa.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import cu.marilasoft.suitetecsa.CustomProgressBar
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

interface Communicator {
    var mContext: Context
    val customProgressBar: CustomProgressBar
        get() = CustomProgressBar()

    fun showAlertDialog(msg: String) {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage(msg)
        builder.setPositiveButton("OK", null)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class)
    fun enableSSLSocket() {
        HttpsURLConnection.setDefaultHostnameVerifier { hostname, session -> true }
        val context = SSLContext.getInstance("TLS")
        context.init(null, arrayOf<X509TrustManager>(object : X509TrustManager {

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }
        }), SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)
    }
}