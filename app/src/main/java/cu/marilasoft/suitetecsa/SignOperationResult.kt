package cu.marilasoft.suitetecsa

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.LoginException
import cu.marilasoft.selibrary.utils.OperationException
import cu.marilasoft.suitetecsa.utils.Communicator
import kotlinx.android.synthetic.main.fragment_sign_operation_result.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

class SignOperationResult : Fragment() {

    lateinit var phoneNumber: String
    lateinit var password: String
    lateinit var mContext: Context
    private var logged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_operation_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logged = SignOperationResultArgs.fromBundle(arguments!!).isLogged
        mContext = context as Context

        phoneNumber = SignOperationResultArgs.fromBundle(arguments!!).phoneNumber.toString()
        password = SignOperationResultArgs.fromBundle(arguments!!).password.toString()

        RunTask(mContext).execute()
    }

    inner class RunTask(
        override var mContext: Context
    ) : AsyncTask<Void?, Void?, Void?>(), Communicator {
        private var runError = false
        private var errorMessage = ""

        override fun onPreExecute() {
            super.onPreExecute()
            result_info.text = "Esto no tardara mucho..."
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                if (!logged) login(phoneNumber, password)
                loadMyAccount()
                loadProducts()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e2: NoSuchAlgorithmException) {
                e2.printStackTrace()
            } catch (e: LoginException) {
                runError = true
                errorMessage = e.message.toString()
                e.printStackTrace()
            } catch (e: OperationException) {
                runError = true
                errorMessage = e.message.toString()
                e.printStackTrace()
            } catch (e: CommunicationException) {
                runError = true
                errorMessage = e.message.toString()
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (runError) showAlertDialog(errorMessage)
            else {
                val intent = Intent(mContext, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }
}