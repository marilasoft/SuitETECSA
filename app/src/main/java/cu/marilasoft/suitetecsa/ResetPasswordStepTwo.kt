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
import cu.marilasoft.selibrary.utils.OperationException
import kotlinx.android.synthetic.main.fragment_reset_password_step_two.*
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
class ResetPasswordStepTwo : Fragment() {
    lateinit var phoneNumber: String
    lateinit var code: String
    lateinit var newPassword: String
    lateinit var sessionId: String
    lateinit var mContext: Context
    val progressDialog = CustomProgressBar()
    val cookies = HashMap<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password_step_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        phoneNumber = ResetPasswordStepTwoArgs.fromBundle(arguments!!).phoneNumber
        sessionId = ResetPasswordStepTwoArgs.fromBundle(arguments!!).sessionId
        Log.e("sessionId", sessionId)
        cookies["JSESSIONID"] = sessionId
        mContext = context as Context
        btn_to_finish.setOnClickListener {
            var error = false
            if (et_code_r.text.toString().length < 4 ||
                    et_code_r.text.toString().isEmpty()) {
                et_code_r.error = "Introdusca el codigo que le ha enviado Cubacel"
                error = true
            }
            if (et_new_password.text.toString().length < 6 ||
                    et_new_password.text.toString().isEmpty()) {
                et_new_password.error = "Introduzca una contrasenna de 6 o mas caracteres"
                error = true
            }
            if (et_repeat_new_password.text.toString() != et_new_password.text.toString()) {
                et_repeat_new_password.error = "Las contrasennas deben coincidir"
                error = true
            }
            if (!error) {
                code = et_code_r.text.toString()
                newPassword = et_new_password.text.toString()
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
                mcPortal.completeResetPassword(code, newPassword, cookies)
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e2: NoSuchAlgorithmException) {
                e2.printStackTrace()
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
                val action = ResetPasswordStepTwoDirections.resetPasswordToResult(false, phoneNumber, newPassword, null)
                findNavController().navigate(action)
            }
        }
    }
}
