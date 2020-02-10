package cu.marilasoft.suitetecsa.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.models.Product
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.LoginException
import cu.marilasoft.suitetecsa.CustomProgressBar
import cu.marilasoft.suitetecsa.SharedApp
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

interface Communicator {
    var mContext: Context
    val customProgressBar: CustomProgressBar
        get() = CustomProgressBar()

    @Throws(LoginException::class, CommunicationException::class)
    fun login(phoneNumber: String, password: String) {
        val mcPortal = MCPortal()
        mcPortal.login(phoneNumber, password)
        val cookies = mcPortal.cookies
        SharedApp.prefs.portalUser = cookies["portaluser"].toString()
        SharedApp.prefs.urlMyAccount = mcPortal.urlsMCP["myAccount"].toString()
        SharedApp.prefs.urlProducts = mcPortal.urlsMCP["products"].toString()
    }

    fun loadMyAccount(loadHomePage: Boolean = false) {
        val mcPortal = MCPortal()
        val cookies: MutableMap<String, String> = HashMap()
        cookies["portaluser"] = SharedApp.prefs.portalUser
        val url = if (loadHomePage) null else {
            SharedApp.prefs.urlMyAccount
        }
        mcPortal.loadMyAccount(url, cookies, loadHomePage = loadHomePage)
        val buys = mcPortal.getBuys()
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
        SharedApp.prefs.phoneNumber = mcPortal.phoneNumber!!
        SharedApp.prefs.credit = mcPortal.credit!!.replace(" CUC", "").toFloat()
        SharedApp.prefs.expire = mcPortal.expire.toString()
        if (mcPortal.creditBonus != null) SharedApp.prefs.creditBonus =
            mcPortal.creditBonus!!.replace(" CUC", "").toFloat()
        SharedApp.prefs.expireBonus = mcPortal.expireBonus.toString()
        SharedApp.prefs.date = mcPortal.date.toString()
        SharedApp.prefs.payableBalance = mcPortal.payableBalance.toString()
        SharedApp.prefs.bonusServices = mcPortal.isActiveBonusServices
        SharedApp.prefs.urlChangeBonusServices = mcPortal.urlsMCP["changeBonusServices"].toString()
        if (loadHomePage) SharedApp.prefs.urlProducts = mcPortal.urlsMCP["products"].toString()
        SharedApp.prefs.isSubscribeFNF = mcPortal.familyAndFriends.isSubscribe
        SharedApp.prefs.phoneNumberOne = mcPortal.familyAndFriends.phoneNumbers[0].phoneNumber
        SharedApp.prefs.phoneNumberTwo = mcPortal.familyAndFriends.phoneNumbers[1].phoneNumber
        SharedApp.prefs.phoneNumberThree = mcPortal.familyAndFriends.phoneNumbers[2].phoneNumber
    }

    fun loadProducts(): List<Product> {
        val mcPortal = MCPortal()
        val cookies: MutableMap<String, String> = HashMap()
        cookies["portaluser"] = SharedApp.prefs.portalUser
        if (SharedApp.prefs.sessionId != "") cookies["DRUTT_DSERVER_SESSIONID"] =
            SharedApp.prefs.sessionId
        mcPortal.loadProducts(SharedApp.prefs.urlProducts, cookies)
        return mcPortal.products
    }

    fun changeBonusServicesState() {
        val mcPortal = MCPortal()
        val cookies: MutableMap<String, String> = HashMap()
        cookies["portaluser"] = SharedApp.prefs.portalUser
        if (SharedApp.prefs.sessionId != "") cookies["DRUTT_DSERVER_SESSIONID"] =
            SharedApp.prefs.sessionId
        mcPortal.changeBonusServices(SharedApp.prefs.bonusServices,
            SharedApp.prefs.urlChangeBonusServices, cookies)
        SharedApp.prefs.bonusServices = !SharedApp.prefs.bonusServices
    }

    fun unsubscribeFNF() {
        val mcPortal = MCPortal()
        val cookies: MutableMap<String, String> = HashMap()
        cookies["portaluser"] = SharedApp.prefs.portalUser
        val url = SharedApp.prefs.urlMyAccount
        mcPortal.loadMyAccount(url, cookies)
        mcPortal.familyAndFriends.unsubscribe(cookies)
        loadMyAccount()
    }

    fun addPhoneNumberToFNF(phoneNumber: String) {
        val mcPortal = MCPortal()
        val cookies: MutableMap<String, String> = HashMap()
        cookies["portaluser"] = SharedApp.prefs.portalUser
        val url = SharedApp.prefs.urlMyAccount
        mcPortal.loadMyAccount(url, cookies)
        for (number in mcPortal.familyAndFriends.phoneNumbers) {
            if (number.phoneNumber == "") number.add(phoneNumber, cookies)
        }
        loadMyAccount()
    }

    fun changePhoneNumberFromFNF(oldPhoneNumber: String ,phoneNumber: String) {
        val mcPortal = MCPortal()
        val cookies: MutableMap<String, String> = HashMap()
        cookies["portaluser"] = SharedApp.prefs.portalUser
        val url = SharedApp.prefs.urlMyAccount
        mcPortal.loadMyAccount(url, cookies)
        for (number in mcPortal.familyAndFriends.phoneNumbers) {
            if (number.phoneNumber == oldPhoneNumber) number.change(phoneNumber, cookies)
        }
        loadMyAccount()
    }

    fun deletePhoneNumberFromFNF(phoneNumber: String) {
        val mcPortal = MCPortal()
        val cookies: MutableMap<String, String> = HashMap()
        cookies["portaluser"] = SharedApp.prefs.portalUser
        val url = SharedApp.prefs.urlMyAccount
        mcPortal.loadMyAccount(url, cookies)
        for (number in mcPortal.familyAndFriends.phoneNumbers) {
            if (number.phoneNumber == "") number.delete(phoneNumber, cookies)
        }
        loadMyAccount()
    }

    fun loanMe(mount: String) {
        val mcPortal = MCPortal()
        val cookies: MutableMap<String, String> = HashMap()
        cookies["portaluser"] = SharedApp.prefs.portalUser
        val url = SharedApp.prefs.urlMyAccount
        mcPortal.loanMe(mount, SharedApp.prefs.phoneNumber, cookies)
    }

    fun showAlertDialog(msg: String) {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage(msg)
        builder.setPositiveButton("OK", null)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    @Throws(KeyManagementException::class, NoSuchAlgorithmException::class)
    fun enableSSLSocket() {
        HttpsURLConnection.setDefaultHostnameVerifier { hostname, session -> true }
        val context = SSLContext.getInstance("TLS")
        context.init(null, arrayOf<X509TrustManager>(object : X509TrustManager {

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }
        }), SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)
    }
}