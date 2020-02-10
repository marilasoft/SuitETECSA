package cu.marilasoft.suitetecsa.ui.home


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.OperationException
import cu.marilasoft.suitetecsa.R
import cu.marilasoft.suitetecsa.SharedApp
import cu.marilasoft.suitetecsa.utils.Communicator
import kotlinx.android.synthetic.main.fragment_account_info.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

/**
 * A simple [Fragment] subclass.
 */
class AccountInfo : Fragment() {
    lateinit var mContext: Context
    lateinit var creditMount: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = context as Context

        updateInterface()

        val creditToRequest = arrayOf("1", "2")
        sp_select_mount.adapter = ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, creditToRequest)
        sp_select_mount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                creditMount = creditToRequest[position]
            }
        }

        btn_to_request.setOnClickListener {
            RunTask(mContext, "loanMe").execute()
        }

        ib_recharge.setOnClickListener {
            if (et_recharge.text.toString().length == 12 || et_recharge.text.toString().length == 16) {
                val permissionCheck = ContextCompat.checkSelfPermission(
                    mContext, Manifest.permission.CALL_PHONE
                )
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    Log.i("Mensaje", "No se tiene permiso para enviar SMS.")
                    ActivityCompat.requestPermissions(
                        activity as Activity,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        225
                    )
                } else {
                    val i = Intent(Intent.ACTION_CALL)
                    i.data = Uri.parse("tel:*662*${et_recharge.text}${Uri.encode("#")}")
                    startActivity(i)
                }
            }
        }

        sw_bonus_service_state.setOnClickListener {
            RunTask(mContext, "changeBonusServices").execute()
        }

        btn_deactivate.setOnClickListener {
            RunTask(mContext, "unsubscribeFNF").execute()
        }

        var isChangeOneRunning = false
        btn_change_one.setOnClickListener {
            val phoneNumber = et_phone_number_one.text.toString()
            val oldPhoneNumber = SharedApp.prefs.phoneNumberOne
            if (!isChangeOneRunning) {
                et_phone_number_one.isEnabled = true
                isChangeOneRunning = true
            } else if (phoneNumber == oldPhoneNumber) et_phone_number_one.error =
                "Introdujo el mismo numero"
            else {
                RunTask(
                    mContext, "changePhoneNumberFromFNF",
                    phoneNumber,
                    oldPhoneNumber
                ).execute()
            }
        }

        var isChangeTwoRunning = false
        btn_change_two.setOnClickListener {
            val phoneNumber = et_phone_number_two.text.toString()
            val oldPhoneNumber = SharedApp.prefs.phoneNumberTwo
            if (!isChangeTwoRunning) {
                et_phone_number_two.isEnabled = true
                isChangeTwoRunning = true
            } else if (phoneNumber == oldPhoneNumber) et_phone_number_two.error =
                "Introdujo el mismo numero"
            else {
                RunTask(
                    mContext, "changePhoneNumberFromFNF",
                    phoneNumber,
                    oldPhoneNumber
                ).execute()
            }
        }

        var isChangeThreeRunning = false
        btn_change_three.setOnClickListener {
            val phoneNumber = et_phone_number_three.text.toString()
            val oldPhoneNumber = SharedApp.prefs.phoneNumberThree
            if (!isChangeThreeRunning) {
                et_phone_number_three.isEnabled = true
                isChangeThreeRunning = true
            } else if (phoneNumber == oldPhoneNumber) et_phone_number_three.error =
                "Introdujo el mismo numero"
            else {
                RunTask(
                    mContext, "changePhoneNumberFromFNF",
                    phoneNumber,
                    oldPhoneNumber
                ).execute()
            }
        }

        btn_delete_one.setOnClickListener {
            RunTask(
                mContext,
                "deletePhoneNumberFromFNF",
                SharedApp.prefs.phoneNumberOne
                ).execute()
        }

        btn_delete_two.setOnClickListener {
            RunTask(
                mContext,
                "deletePhoneNumberFromFNF",
                SharedApp.prefs.phoneNumberTwo
            ).execute()
        }

        btn_delete_three.setOnClickListener {
            RunTask(
                mContext,
                "deletePhoneNumberFromFNF",
                SharedApp.prefs.phoneNumberThree
            ).execute()
        }

        RunTask(mContext, "loadMyAccount").execute()
    }

    private fun updateInterface() {
        try {
            tv_credit.text = toFormat(SharedApp.prefs.credit.toString())
            tv_expire.text = "Expira: ${SharedApp.prefs.expire}"
            if (SharedApp.prefs.creditBonus != 0.0f) tv_bonus.text =
                "Bono: ${toFormat(SharedApp.prefs.creditBonus.toString())} - Expira: ${SharedApp.prefs.expireBonus}"
            if (SharedApp.prefs.creditBonus != 0.0f) tv_bonus.visibility = View.VISIBLE
            else tv_bonus.visibility = View.GONE
            sw_bonus_service_state.isChecked = SharedApp.prefs.bonusServices
            if (SharedApp.prefs.date != "null") {
                tv_loan_date.visibility = View.VISIBLE
                tv_loan_date.text = "Fecha del Adelanto: ${SharedApp.prefs.date}"
                btn_to_request.isEnabled = false
            }
            tv_bedt_mount.text = "Saldo pendiente por pagar: ${SharedApp.prefs.payableBalance}"
            val phoneNumberOne = SharedApp.prefs.phoneNumberOne
            val phoneNumberTwo = SharedApp.prefs.phoneNumberTwo
            val phoneNumberThree = SharedApp.prefs.phoneNumberThree
            if (phoneNumberOne != "") et_phone_number_one.setText(phoneNumberOne)
            else {
                et_phone_number_one.isEnabled = true
                ll_change_and_delete_one.visibility = View.INVISIBLE
                btn_add_one.visibility = View.VISIBLE
            }
            if (phoneNumberTwo != "") et_phone_number_two.setText(phoneNumberTwo)
            else {
                et_phone_number_two.isEnabled = true
                ll_change_and_delete_two.visibility = View.INVISIBLE
                btn_add_two.visibility = View.VISIBLE
            }
            if (phoneNumberThree != "") et_phone_number_three.setText(phoneNumberThree)
            else {
                et_phone_number_three.isEnabled = true
                ll_change_and_delete_three.visibility = View.INVISIBLE
                btn_add_three.visibility = View.VISIBLE
            }
            if (SharedApp.prefs.isSubscribeFNF) cv_family_and_friends.visibility = View.VISIBLE
            else cv_family_and_friends.visibility = View.GONE
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun toFormat(credit: String): String {
        if (credit.contains(".")) {
            val size = credit.toCharArray().size
            var position = 0
            for (char in credit.toCharArray()) {
                if (char == '.') break
                position++
            }
            if (position + 2 == size) return "${credit}0 CUC"
            else if (position + 3 != size) return "${credit}.00 CUC"
        }
        return "$credit CUC"
    }

    inner class RunTask(
        override var mContext: Context,
        private val operation: String,
        private val phoneNumber: String? = null,
        private val oldPhoneNumber: String? = null
    ) : AsyncTask<Void?, Void?, Void?>(), Communicator {
        private var runError = false
        private var errorMessage = ""
        private val progressDialog = customProgressBar

        override fun onPreExecute() {
            super.onPreExecute()
            if (operation != "loadMyAccount") progressDialog.show(mContext)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                when (operation) {
                    "loadMyAccount" -> {
                        loadMyAccount(true)
                        loadProducts()
                    }
                    "loanMe" -> loanMe(creditMount)
                    "changeBonusServices" -> changeBonusServicesState()
                    "unsubscribeFNF" -> unsubscribeFNF()
                    "addPhoneNumberToFNF" -> addPhoneNumberToFNF(phoneNumber!!)
                    "changePhoneNumberFromFNF" -> changePhoneNumberFromFNF(
                        oldPhoneNumber!!,
                        phoneNumber!!
                    )
                    "dalatePhoneNumberFromFNF" -> deletePhoneNumberFromFNF(phoneNumber!!)
                }
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e2: NoSuchAlgorithmException) {
                e2.printStackTrace()
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
            if (operation != "loadMyAccount"){
                if (progressDialog.dialog.isShowing) progressDialog.dialog.dismiss()
            }
            if (runError) showAlertDialog(errorMessage)
            else updateInterface()
        }
    }
}
