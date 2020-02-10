package cu.marilasoft.suitetecsa


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import kotlinx.android.synthetic.main.fragment_reset_password_step_one.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

/**
 * A simple [Fragment] subclass.
 */
class ResetPasswordStepOne : Fragment() {
    lateinit var mContext: Context
    val progressDialog = CustomProgressBar()
    lateinit var phoneNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password_step_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = context as Context
        btn_continue.setOnClickListener {
            if (et_phone_number.text.toString().length < 8 ||
                (!et_phone_number.text.toString().startsWith("5") &&
                        !et_phone_number.text.toString().startsWith("6"))) {
                et_phone_number.error = "Introduce un numero valido"
            } else {
                phoneNumber = et_phone_number.text.toString()
                RunTask().execute()
            }
        }
    }

    inner class RunTask: AsyncTask<Void?, Void?, Void?>() {
        lateinit var sessionId: String
        lateinit var errorMessage: String
        private var runError = false

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

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.show(mContext, "Por favor, espere...")
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                val mcPortal = MCPortal()
                mcPortal.resetPassword(phoneNumber)
                sessionId = mcPortal.cookies["JSESSIONID"].toString()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e2: NoSuchAlgorithmException) {
                e2.printStackTrace()
            } catch (e: CommunicationException) {
                e.printStackTrace()
                runError = true
                errorMessage = e.message.toString()
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (progressDialog.dialog.isShowing) {
                progressDialog.dialog.dismiss()
            }
            if (runError) {
                val builder = AlertDialog.Builder(mContext)
                builder.setMessage(errorMessage)
                builder.setPositiveButton("OK", null)
                val alertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            } else {
                Log.e("Cookies", sessionId)
                val action = ResetPasswordStepOneDirections.toResetPasswordStepTwo(sessionId, phoneNumber)
                findNavController().navigate(action)
            }
        }
    }
}
