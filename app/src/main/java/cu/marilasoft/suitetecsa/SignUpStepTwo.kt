package cu.marilasoft.suitetecsa


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.OperationException
import kotlinx.android.synthetic.main.fragment_sign_up_step_two.*
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
class SignUpStepTwo : Fragment() {
    private val cookies = HashMap<String, String>()
    lateinit var code: String
    lateinit var mContext: Context
    private val progressDialog = CustomProgressBar()
    lateinit var sessionId: String
    private lateinit var phoneNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_step_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionId = SignUpStepTwoArgs.fromBundle(arguments!!).sessionId
        phoneNumber = SignUpStepTwoArgs.fromBundle(arguments!!).phoneNumber
        Log.e("JSSESIONID", sessionId)
        cookies["JSESSIONID"] = sessionId
        Toast.makeText(context, sessionId, Toast.LENGTH_SHORT).show()


        mContext = context as Context

        btn_continue.setOnClickListener {
            var error = false
            if (et_code.text.toString().length < 4 || et_code.text.toString() == "") {
                et_code.error = "Introduzca un codigo de 4 digitos"
                error = true
            }
            if (!error) {
                code = et_code.text.toString()
                RunTask().execute()
            }
        }
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
                mcPortal.verifyCode(code, cookies)
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
                val action = SignUpStepTwoDirections.toSignUpStepThree(sessionId, phoneNumber)
                findNavController().navigate(action)
            }
        }
    }
}
