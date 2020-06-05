package cu.marilasoft.suitetecsa

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class UserPortalInfo : AppCompatActivity() {

    private lateinit var userName: String
    private lateinit var credit: String
    private lateinit var time: String
    private lateinit var expire: String
    private lateinit var deleteDate: String

    private lateinit var tvUser: TextView
    private lateinit var tvCredit: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvExpire: TextView
    private lateinit var tvDeleteDate: TextView
    private lateinit var ibExit: ImageButton

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_portal_info)

        tvUser = findViewById(R.id.tv_nauta_user)
        tvCredit = findViewById(R.id.tv_credit)
        tvTime = findViewById(R.id.tv_time)
        tvExpire = findViewById(R.id.tv_expire)
        tvDeleteDate = findViewById(R.id.tv_delete)
        ibExit = findViewById(R.id.ib_exit)

        val data: Bundle = intent!!.extras!!

        userName = data.getString("userName").toString()
        credit = data.getString("credit").toString()
        time = data.getString("time").toString()
        expire = data.getString("blockDate").toString()
        deleteDate = data.getString("delDate").toString()

        tvUser.text = userName
        tvCredit.text = credit
        tvTime.text = time
        tvExpire.text = "Expira el: $expire"
        tvDeleteDate.text = "Vence el: $deleteDate"
    }
}
