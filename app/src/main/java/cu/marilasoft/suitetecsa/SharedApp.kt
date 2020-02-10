package cu.marilasoft.suitetecsa

import android.app.Application
import cu.marilasoft.suitetecsa.utils.Prefs

class SharedApp: Application() {

    companion object {
        lateinit var prefs: Prefs
    }

    override fun onCreate() {
        super.onCreate()

        prefs = Prefs(applicationContext)
    }
}