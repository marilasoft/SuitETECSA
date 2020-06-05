package cu.marilasoft.suitetecsa

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.LoginException
import cu.marilasoft.selibrary.utils.OperationException
import cu.marilasoft.suitetecsa.utils.BuysDB
import cu.marilasoft.suitetecsa.utils.Communicator
import kotlinx.android.synthetic.main.fragment_sign_operation_result.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

class SignOperationResult : Fragment() {

    lateinit var phoneNumberInput: String
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

        phoneNumberInput = SignOperationResultArgs.fromBundle(arguments!!).phoneNumber.toString()
        password = SignOperationResultArgs.fromBundle(arguments!!).password.toString()

        RunTask(mContext).execute()
    }

    inner class RunTask(
        override var mContext: Context
    ) : AsyncTask<Void?, Void?, Void?>(), Communicator, MCPortal {
        private var runError = false
        private var errorMessage = ""

        override fun onPreExecute() {
            super.onPreExecute()
            result_info.text = "Esto no tardara mucho..."
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                cookies["portaluser"] = SharedApp.prefs.portalUser
                if (!logged) {
                    login(phoneNumberInput, password)
                    SharedApp.prefs.portalUser = cookies["portaluser"].toString()
                }

                loadMyAccount(null, cookies, loadHomePage = true)
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