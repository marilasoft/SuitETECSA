package cu.marilasoft.suitetecsa


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.OperationException
import kotlinx.android.synthetic.main.fragment_sign_up_step_three.*
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
class SignUpStepThree : Fragment() {
    lateinit var mContext: Context
    lateinit var phoneNumber: String
    lateinit var newPassword: String
    lateinit var sessionId: String
    private val progressDialog = CustomProgressBar()
    private val cookies = HashMap<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_step_three, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionId = SignUpStepThreeArgs.fromBundle(arguments!!).JSESSIONID
        cookies["JSESSIONID"] = sessionId
        phoneNumber = SignUpStepThreeArgs.fromBundle(arguments!!).phoneNumber
        mContext = context as Context
        btn_to_finish.setOnClickListener {
            if (isAllCorrect()) {
                newPassword = et_new_password.text.toString()
                RunTask().execute()
            }
        }
    }

    private fun isAllCorrect(): Boolean{
        var bool = true
        if (et_new_password.text.toString().isEmpty() ||
            et_new_password.text.toString().length < 6) {
            et_new_password.error = "Introduzca una contrasennademas de 6 caracteres"
            bool = false
        }
        if (et_repeat_new_password.text.toString() != et_new_password.text.toString()) {
            et_repeat_new_password.error = "La contrasenna no coincide"
            bool = false
        }
        return bool
    }

    @SuppressLint("StaticFieldLeak")
    inner class RunTask : AsyncTask<Void?, Void?, Void?>() {
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
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e2: NoSuchAlgorithmException) {
                e2.printStackTrace()
            }

            try {
                val mcPortal = MCPortal()
                mcPortal.completeSignUp(newPassword, cookies)
            } catch (e: CommunicationException) {
                e.printStackTrace()
                runError = true
                errorMessage = e.message.toString()
            } catch (e: OperationException) {
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
                val action = SignUpStepThreeDirections.signUpToResult(false, phoneNumber, newPassword, null)
                findNavController().navigate(action)
            }
        }
    }
}
