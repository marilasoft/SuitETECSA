package cu.marilasoft.suitetecsa

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsMessage
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_reset_password_step_two.*
import kotlinx.android.synthetic.main.fragment_sign_up_step_two.*
import java.lang.NullPointerException
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val permissionCheck = ContextCompat.checkSelfPermission(
//            this, Manifest.permission.RECEIVE_SMS
//        )
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            Log.i("Mensaje", "No se tiene permiso para enviar SMS.")
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.RECEIVE_SMS),
//                225
//            )
//        } else {
//            val mr = MessageReceiver()
//            val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
//            registerReceiver(mr, filter)
//        }
    }

//    inner class MessageReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val bundle = intent.extras
//            if (bundle != null) {
//                val permissionCheck = ContextCompat.checkSelfPermission(
//                    this@MainActivity, Manifest.permission.READ_SMS
//                )
//                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                    Log.i("Mensaje", "No se tiene permiso para enviar SMS.")
//                    ActivityCompat.requestPermissions(
//                        this@MainActivity,
//                        arrayOf(Manifest.permission.READ_SMS),
//                        224
//                    )
//                } else {
//                    val sms = bundle["pdus"] as Array<Any>?
//                    for (i in sms!!.indices) {
//                        val message: SmsMessage =
//                            SmsMessage.createFromPdu(sms[i] as ByteArray)
//                        val numero: String = message.displayOriginatingAddress
//                        Log.e("Numero: ", numero)
//                        val messageText: String = message.messageBody.toString()
//                        Log.e("Message: ", messageText)
//                        if (numero == "Cubacel") {
//                            val fCode = messageText.split("CODIGO MiCubacel: ")[1]
//                            Log.e("Code: ", fCode)
//                            try {
//                                et_code.setText(fCode)
//                            } catch (e: NullPointerException) {
//                                Log.e("Error", e.message.toString())
//                            }
//                            try {
//                                et_code_r.setText(fCode)
//                            } catch (e: NullPointerException) {
//                                Log.e("Error", e.message.toString())
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}
