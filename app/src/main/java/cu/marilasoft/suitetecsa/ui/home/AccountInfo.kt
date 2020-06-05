package cu.marilasoft.suitetecsa.ui.home


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
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
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.OperationException
import cu.marilasoft.suitetecsa.R
import cu.marilasoft.suitetecsa.SharedApp
import cu.marilasoft.suitetecsa.utils.BuysDB
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
        sp_select_mount.adapter =
            ArrayAdapter(mContext, android.R.layout.simple_spinner_item, creditToRequest)
        sp_select_mount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        RunTask(mContext, "loadInfo").execute()
    }

    @SuppressLint("SetTextI18n")
    private fun updateInterface() {
        try {
            tv_phone_number.text = "+53 ${SharedApp.prefs.phoneNumber}"
            tv_credit.text = toFormat(SharedApp.prefs.credit.toString())
            tv_expire.text = "Expira el: ${SharedApp.prefs.expire}"
            if (SharedApp.prefs.creditBonus != 0.0f) {
                tv_bonus.text = toFormat(SharedApp.prefs.creditBonus.toString())
                tv_expire_bonus.text = "Expira el: ${SharedApp.prefs.expireBonus}"
            }
            if (SharedApp.prefs.creditBonus != 0.0f) ll_bonus.visibility = View.VISIBLE
            else ll_bonus.visibility = View.GONE
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
            }
            if (phoneNumberTwo != "") et_phone_number_two.setText(phoneNumberTwo)
            else {
                et_phone_number_two.isEnabled = true
            }
            if (phoneNumberThree != "") et_phone_number_three.setText(phoneNumberThree)
            else {
                et_phone_number_three.isEnabled = true
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

    @SuppressLint("StaticFieldLeak")
    inner class RunTask(
        override var mContext: Context,
        private val operation: String,
        private val phoneNumberInput: String? = null,
        private val oldPhoneNumber: String? = null
    ) : AsyncTask<Void?, Void?, Void?>(), Communicator, MCPortal {
        private var runError = false
        private var errorMessage = ""
        private val progressDialog = customProgressBar

        private fun loadInfo() {
            cookies["portaluser"] = SharedApp.prefs.portalUser
            loadMyAccount(null, cookies, loadHomePage = true)
            SharedApp.prefs.sessionId = cookies["DRUTT_DSERVER_SESSIONID"].toString()
            SharedApp.prefs.phoneNumber = phoneNumber!!
            SharedApp.prefs.credit = credit!!.replace(" CUC", "").toFloat()
            SharedApp.prefs.expire = expire.toString()
            if (creditBonus != null) SharedApp.prefs.creditBonus =
                creditBonus!!.replace(" CUC", "").toFloat()
            SharedApp.prefs.expireBonus = expireBonus.toString()
            SharedApp.prefs.date = date.toString()
            SharedApp.prefs.payableBalance = payableBalance.toString()
            SharedApp.prefs.bonusServices = isActiveBonusServices
            SharedApp.prefs.urlChangeBonusServices = urls["changeBonusServices"].toString()
            SharedApp.prefs.urlProducts = urls["products"].toString()
            SharedApp.prefs.isSubscribeFNF = familyAndFriends.isSubscribe
            SharedApp.prefs.phoneNumberOne = familyAndFriends.phoneNumbers[0].phoneNumber
            SharedApp.prefs.phoneNumberTwo = familyAndFriends.phoneNumbers[1].phoneNumber
            SharedApp.prefs.phoneNumberThree = familyAndFriends.phoneNumbers[2].phoneNumber
            loadProducts(SharedApp.prefs.urlProducts, cookies)
            val buys = getBuys()
            val buysDB = BuysDB(mContext, "buys", null, 1)
            val dbBuys = buysDB.writableDatabase
            dbBuys.delete("buys", null, null)
            for (buy in buys) {
                val register = ContentValues()
                register.put("packageId", buy.packageId)
                register.put("title", buy.title)
                register.put("description", buy.description)
                register.put("dataInfo", buy.dataInfo)
                register.put("restData", buy.restData)
                register.put("percent", buy.percent)
                register.put("expireInDate", buy.expireInDate)
                register.put("expireInHours", buy.expireInHours)
                register.put("expireDate", buy.expireDate)
                dbBuys.insert("buys", null, register)
            }
            dbBuys.close()
        }

        override fun onPreExecute() {
            super.onPreExecute()
            if (operation != "loadInfo") progressDialog.show(mContext)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                cookies["DRUTT_DSERVER_SESSIONID"] = SharedApp.prefs.sessionId
                when (operation) {
                    "loadInfo" -> loadInfo()
                    "loanMe" -> loanMe(creditMount, SharedApp.prefs.phoneNumber, cookies)
                    "changeBonusServices" -> changeBonusServices(
                        SharedApp.prefs.bonusServices,
                        SharedApp.prefs.urlChangeBonusServices, cookies
                    )
                    "unsubscribeFNF" -> {
                        loadInfo()
                        familyAndFriends.unsubscribe(cookies)
                    }
                    "addPhoneNumberToFNF" -> {
                        loadInfo()
                        for (number in familyAndFriends.phoneNumbers) {
                            if (number.phoneNumber == "") {
                                number.add(phoneNumberInput.toString(), cookies)
                                break
                            }
                        }
                    }
                    "changePhoneNumberFromFNF" -> {
                        loadInfo()
                        for (number in familyAndFriends.phoneNumbers) {
                            if (number.phoneNumber == oldPhoneNumber.toString()) number.change(
                                phoneNumberInput.toString(),
                                cookies
                            )
                        }
                    }
                    "dalatePhoneNumberFromFNF" -> {
                        for (number in familyAndFriends.phoneNumbers) {
                            if (number.phoneNumber == phoneNumberInput.toString()) number.delete(
                                phoneNumberInput.toString(),
                                cookies
                            )
                        }
                    }
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
            if (operation != "loadInfo") {
                if (progressDialog.dialog.isShowing) progressDialog.dialog.dismiss()
            }
            if (runError) showAlertDialog(errorMessage)
            else {
                when (operation) {
                    "changeBonusServices" -> SharedApp.prefs.bonusServices =
                        !SharedApp.prefs.bonusServices
                }
                updateInterface()
            }
        }
    }
}
