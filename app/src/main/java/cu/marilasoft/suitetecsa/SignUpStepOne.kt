package cu.marilasoft.suitetecsa


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.suitetecsa.utils.Communicator
import kotlinx.android.synthetic.main.fragment_sign_up_step_one.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

/**
 * A simple [Fragment] subclass.
 */
class SignUpStepOne : Fragment() {

    lateinit var phoneNumberInput: String
    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var emailAddress: String
    lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_step_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(context, "$context", Toast.LENGTH_SHORT).show()
        btn_continue.setOnClickListener {
            var error = false
            if (et_phone_number.text.toString().length < 8) {
                et_phone_number.error = "Este campo debe tener 8 digitos"
                error = true
            }else if (!et_phone_number.text.toString().startsWith("5") &&
                    !et_phone_number.text.toString().startsWith("6")) {
                et_phone_number.error = "El numero debe comenzar con 5 o 6"
                error = true
            }
            if (et_first_name.text.toString() == "") {
                et_first_name.error = "Este campo es requerido"
                error = true
            }
            if (et_last_name.text.toString() == "") {
                et_last_name.error = "Este campo es requerido"
                error = true
            }
            if (!error) {
                phoneNumberInput = et_phone_number.text.toString()
                firstName = et_first_name.text.toString()
                lastName = et_last_name.text.toString()
                emailAddress = et_email_address.text.toString()
                mContext = context as Context
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
                signUp(phoneNumberInput, firstName, lastName, emailAddress)
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
                val action = SignUpStepOneDirections.toSignUpStepTwo(
                    cookies["JSESSIONID"].toString(), phoneNumberInput)
                findNavController().navigate(action)
            }
        }
    }
}
