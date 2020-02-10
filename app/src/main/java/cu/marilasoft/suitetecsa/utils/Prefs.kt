package cu.marilasoft.suitetecsa.utils

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
    var portalUser: String
        get() = prefs.getString(PORTAL_USER, "").toString()
        set(value) = prefs.edit().putString(PORTAL_USER, value).apply()
    var sessionId: String
        get() = prefs.getString(DRUTT_DSERVER_SESSIONID, "").toString()
        set(value) = prefs.edit().putString(DRUTT_DSERVER_SESSIONID, value).apply()
    var urlMyAccount: String
        get() = prefs.getString(MY_ACCOUNT_URL, "").toString()
        set(value) = prefs.edit().putString(MY_ACCOUNT_URL, value).apply()
    var urlChangeBonusServices: String
        get() = prefs.getString(CHANGE_SERVICES_URL, "").toString()
        set(value) = prefs.edit().putString(CHANGE_SERVICES_URL, value).apply()
    var urlProducts: String
        get() = prefs.getString(PRODUCTS_URL, "").toString()
        set(value) = prefs.edit().putString(PRODUCTS_URL, value).apply()
    var phoneNumber: String
        get() = prefs.getString(PHONE_NUMBER, "").toString()
        set(value) = prefs.edit().putString(PHONE_NUMBER, value).apply()
    var userName: String
        get() = prefs.getString(USER_NAME, "").toString()
        set(value) = prefs.edit().putString(USER_NAME, value).apply()
    var credit: Float
        get() = prefs.getFloat(CREDIT, 0.0f)
        set(value) = prefs.edit().putFloat(CREDIT, value).apply()
    var expire: String
        get() = prefs.getString(EXPIRE, "").toString()
        set(value) = prefs.edit().putString(EXPIRE, value).apply()
    var creditBonus: Float
        get() = prefs.getFloat(CREDIT_BONUS, 0.0f)
        set(value) = prefs.edit().putFloat(CREDIT_BONUS, value).apply()
    var expireBonus: String
        get() = prefs.getString(EXPIRE_BONUS, "").toString()
        set(value) = prefs.edit().putString(EXPIRE_BONUS, value).apply()
    var date: String
        get() = prefs.getString(DATE, "").toString()
        set(value) = prefs.edit().putString(DATE, value).apply()
    var payableBalance: String
        get() = prefs.getString(PAYABLE_BALANCE, "").toString()
        set(value) = prefs.edit().putString(PAYABLE_BALANCE, value).apply()
    var bonusServices: Boolean
        get() = prefs.getBoolean(BONUS_SERVICES, false)
        set(value) = prefs.edit().putBoolean(BONUS_SERVICES, value).apply()
    var isSubscribeFNF: Boolean
        get() = prefs.getBoolean(IS_SUBSCRIBE_FNF, false)
        set(value) = prefs.edit().putBoolean(IS_SUBSCRIBE_FNF, value).apply()
    var phoneNumberOne: String
        get() = prefs.getString(PHONE_NUMBER_ONE, "").toString()
        set(value) = prefs.edit().putString(PHONE_NUMBER_ONE, value).apply()
    var phoneNumberTwo: String
        get() = prefs.getString(PHONE_NUMBER_TWO, "").toString()
        set(value) = prefs.edit().putString(PHONE_NUMBER_TWO, value).apply()
    var phoneNumberThree: String
        get() = prefs.getString(PHONE_NUMBER_THREE, "").toString()
        set(value) = prefs.edit().putString(PHONE_NUMBER_THREE, value).apply()

    companion object {
        const val PREFS_NAME = "cu.marilasoft.suitetecsa"
        const val PORTAL_USER = "PORTAL_USER"
        const val DRUTT_DSERVER_SESSIONID = "DRUTT_DSERVER_SESSIONID"
        const val MY_ACCOUNT_URL = "MY_ACCOUNT_URL"
        const val CHANGE_SERVICES_URL = "CHANGE_SERVICES_URL"
        const val PRODUCTS_URL = "PRODUCTS_URL"
        const val PHONE_NUMBER = "PHONE_NUMBER"
        const val USER_NAME = "USER_NAME"
        const val CREDIT = "CREDIT"
        const val EXPIRE = "EXPIRE"
        const val CREDIT_BONUS = "CREDIT_BONUS"
        const val EXPIRE_BONUS = "EXPIRE_BONUS"
        const val DATE = "DATE"
        const val PAYABLE_BALANCE = "PAYABLE_BALANCE"
        const val BONUS_SERVICES = "BONUS_SERVICES"
        const val IS_SUBSCRIBE_FNF = "IS_SUBSCRIBE_FNF"
        const val PHONE_NUMBER_ONE = "PHONE_NUMBER_ONE"
        const val PHONE_NUMBER_TWO = "PHONE_NUMBER_TWO"
        const val PHONE_NUMBER_THREE = "PHONE_NUMBER_THREE"
    }
}