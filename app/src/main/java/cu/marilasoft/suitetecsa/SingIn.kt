package cu.marilasoft.suitetecsa


import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.LoginException
import cu.marilasoft.suitetecsa.utils.Communicator
import kotlinx.android.synthetic.main.fragment_sing_in.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

/**
 * A simple [Fragment] subclass.
 */
class SingIn : Fragment() {
    lateinit var mContext: Context
    lateinit var phoneNumberInput: String
    lateinit var password: String
    lateinit var anim: AnimationDrawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sing_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = context as Context
        tv_sign_up.setOnClickListener {
            findNavController().navigate(R.id.to_signUp)
        }
        tv_forgot_password.setOnClickListener {
            findNavController().navigate(R.id.to_resetPassword)
        }
        anim = sign_in_container.background as AnimationDrawable
        anim.setEnterFadeDuration(6000)
        anim.setExitFadeDuration(2000)
        btn_sign_in.setOnClickListener {
            var error = false
            if (et_phone_number.text.toString().length < 8 ||
                (!et_phone_number.text.toString().startsWith("5") &&
                        !et_phone_number.text.toString().startsWith("6"))
            ) {
                et_phone_number.error = "Introduce un numerode elefono valido"
                error = true
            }
            if (et_password.text.toString().isEmpty()) {
                et_password.error = "Introduzca una contrasenna valida"
                error = true
            }
            if (!error) {
                phoneNumberInput = et_phone_number.text.toString()
                password = et_password.text.toString()
                RunTask(mContext).execute()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (anim != null && !anim.isRunning)
            anim.start()
    }

    override fun onPause() {
        super.onPause()
        if (anim != null && anim.isRunning)
            anim.stop()
    }

    inner class RunTask(override var mContext: Context) : AsyncTask<Void?, Void?, Void?>(),
        Communicator, MCPortal {
        lateinit var sessionId: String
        lateinit var errorMessage: String
        private val progressDialog = customProgressBar
        private var runError = false

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.show(mContext, "Por favor, espere...")
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                login(phoneNumberInput, password)
                SharedApp.prefs.portalUser = cookies["portaluser"].toString()
                SharedApp.prefs.urlMyAccount = urls["myAccount"].toString()
                SharedApp.prefs.urlProducts = urls["products"].toString()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e2: NoSuchAlgorithmException) {
                e2.printStackTrace()
            } catch (e: CommunicationException) {
                e.printStackTrace()
                runError = true
                errorMessage = e.message.toString()
            } catch (e: LoginException) {
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
                val action = SingInDirections.actionSingInToResult(true, null, null, null)
                findNavController().navigate(action)
            }
        }
    }
}
