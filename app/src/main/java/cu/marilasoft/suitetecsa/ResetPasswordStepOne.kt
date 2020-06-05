package cu.marilasoft.suitetecsa


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
import cu.marilasoft.suitetecsa.utils.Communicator
import kotlinx.android.synthetic.main.fragment_reset_password_step_one.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

/**
 * A simple [Fragment] subclass.
 */
class ResetPasswordStepOne : Fragment() {
    lateinit var mContext: Context
    lateinit var phoneNumberInput: String

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
                        !et_phone_number.text.toString().startsWith("6"))
            ) {
                et_phone_number.error = "Introduce un numero valido"
            } else {
                phoneNumberInput = et_phone_number.text.toString()
                RunTask(mContext).execute()
            }
        }
    }

    inner class RunTask(override var mContext: Context) : AsyncTask<Void?, Void?, Void?>(),
        Communicator, MCPortal {
        private val progressDialog = customProgressBar
        lateinit var sessionId: String
        lateinit var errorMessage: String
        private var runError = false

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.show(mContext, "Por favor, espere...")
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                resetPassword(phoneNumberInput.toString())
                sessionId = cookies["JSESSIONID"].toString()
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
            if (progressDialog.dialog.isShowing) progressDialog.dialog.dismiss()
            if (runError) showAlertDialog(errorMessage)
            else {
                Log.e("Cookies", sessionId)
                val action = ResetPasswordStepOneDirections.toResetPasswordStepTwo(
                    sessionId,
                    phoneNumberInput.toString()
                )
                findNavController().navigate(action)
            }
        }
    }
}
