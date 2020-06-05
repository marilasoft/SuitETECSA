package cu.marilasoft.suitetecsa


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.OperationException
import cu.marilasoft.suitetecsa.utils.Communicator
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
    lateinit var mActivity: Activity
    lateinit var sessionId: String
    private lateinit var phoneNumberInput: String

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
        phoneNumberInput = SignUpStepTwoArgs.fromBundle(arguments!!).phoneNumber
        Log.e("JSSESIONID", sessionId)
        cookies["JSESSIONID"] = sessionId

        mContext = context as Context
        mActivity = activity as Activity

        val permissionCheck = ContextCompat.checkSelfPermission(
            mContext, Manifest.permission.RECEIVE_SMS
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para enviar SMS.")
            ActivityCompat.requestPermissions(
                mActivity,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                225
            )
        } else {
            val mr = MessageReceiver()
            val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
            mActivity.registerReceiver(mr, filter)
        }

        btn_continue.setOnClickListener {
            var error = false
            if (et_code.text.toString().length < 4 || et_code.text.toString() == "") {
                et_code.error = "Introduzca un codigo de 4 digitos"
                error = true
            }
            if (!error) {
                code = et_code.text.toString()
                RunTask(mContext).execute()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class RunTask(override var mContext: Context) : AsyncTask<Void?, Void?, Void?>(),
    Communicator, MCPortal {
        private val progressDialog = customProgressBar
        lateinit var errorMessage: String
        private var runError = false

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.show(mContext, "Por favor, espere...")
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                verifyCode(code, cookies)
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
            if (progressDialog.dialog.isShowing) progressDialog.dialog.dismiss()
            if (runError) showAlertDialog(errorMessage)
            else {
                val action = SignUpStepTwoDirections.toSignUpStepThree(sessionId, phoneNumberInput)
                findNavController().navigate(action)
            }
        }
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            if (bundle != null) {
                val permissionCheck = ContextCompat.checkSelfPermission(
                    mContext, Manifest.permission.READ_SMS
                )
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    Log.i("Mensaje", "No se tiene permiso para enviar SMS.")
                    ActivityCompat.requestPermissions(
                        mActivity,
                        arrayOf(Manifest.permission.READ_SMS),
                        224
                    )
                } else {
                    val sms = bundle["pdus"] as Array<Any>?
                    for (i in sms!!.indices) {
                        val message: SmsMessage =
                            SmsMessage.createFromPdu(sms[i] as ByteArray)
                        val numero: String = message.displayOriginatingAddress
                        Log.e("Numero: ", numero)
                        val messageText: String = message.messageBody.toString()
                        Log.e("Message: ", messageText)
                        if (numero == "Cubacel") {
                            code = messageText.split("CODIGO MiCubacel: ")[1]
                            Log.e("Code: ", code)
                            try {
                                et_code.setText(code)
                                et_code.isEnabled = false
                                RunTask(mContext).execute()
                            } catch (e: NullPointerException) {
                                Log.e("Error", e.message.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}
