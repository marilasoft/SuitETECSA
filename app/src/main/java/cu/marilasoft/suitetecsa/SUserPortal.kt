package cu.marilasoft.suitetecsa


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cu.marilasoft.selibrary.UserPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.LoginException
import cu.marilasoft.suitetecsa.utils.Communicator
import kotlinx.android.synthetic.main.fragment_user_portal.*
import java.io.ByteArrayInputStream
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

/**
 * A simple [Fragment] subclass.
 */
open class SUserPortal : Fragment() {

    lateinit var mContext: Context
    lateinit var operation: String
    lateinit var userName: String
    lateinit var password: String
    lateinit var captchaCode: String
    lateinit var csrf: String
    var mCookies: MutableMap<String, String> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_portal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = context as Context

        btn_sign_in.setOnClickListener {
            userName = et_user_name.text.toString()
            password = et_password.text.toString()
            captchaCode = et_captcha_code.text.toString()
            var error = false

            if (!userName.contains("@nauta.com.cu") && !userName.contains("@nauta.co.cu")) {
                et_user_name.error = "Introduzca un nombre de usuario valido"
                error = true
            }

            if (password.length < 6) {
                et_password.error = "La contrasena debe ser de al menos 6 caracteres"
                error = true
            }

            if (captchaCode.isEmpty()) {
                et_captcha_code.error = "Introduzca el codigo"
                error = true
            }

            operation = "login"
            if (!error) Task(mContext).execute()
        }

        ib_reload_captcha.setOnClickListener {
            operation = "loadCaptcha"
            Task(mContext).execute()
        }

        operation = "loadCaptcha"
        Task(mContext).execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class Task(override var mContext: Context) : AsyncTask<Void?, Void?, Void?>(),
        Communicator, UserPortal {
        lateinit var errorMessage: String
        private var runError = false

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                when (operation) {
                    "loadCaptcha" -> {
                        if (mCookies.isEmpty()) {
                            preLogin()
                            csrf = csrfCode
                            mCookies = cookies
                        }
                        loadCAPTCHA(cookies)
                    }
                    "login" -> {
                        csrfCode = csrf
                        cookies = mCookies
                        login(userName, password, captchaCode, cookies)
                    }
                }
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
            if (runError) showAlertDialog(errorMessage)
            else {
                when (operation) {
                    "loadCaptcha" -> {
                        val bitMap = BitmapFactory.decodeStream(ByteArrayInputStream(captchaImage))
                        iv_captcha.setImageBitmap(bitMap)
                    }
                    "login" -> {
                        val intent = Intent(mContext, UserPortalInfo::class.java)
                        intent.putExtra("userName", userName)
                        intent.putExtra("password", password)
                        intent.putExtra("credit", credit)
                        intent.putExtra("time", time)
                        intent.putExtra("blockDate", blockDate)
                        intent.putExtra("delDate", delDate)
                        intent.putExtra("session", cookies["session"])
                        intent.putExtra("nauta_lang", cookies["nauta_lang"])
                        mContext.startActivity(intent)
                    }
                }
            }
        }
    }
}
