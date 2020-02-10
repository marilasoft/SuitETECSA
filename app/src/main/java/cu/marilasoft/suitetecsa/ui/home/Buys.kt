package cu.marilasoft.suitetecsa.ui.home


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cu.marilasoft.suitetecsa.Buy
import cu.marilasoft.suitetecsa.BuysAdapter
import cu.marilasoft.suitetecsa.R
import cu.marilasoft.suitetecsa.utils.BuysDB

/**
 * A simple [Fragment] subclass.
 */
class Buys : Fragment() {
    lateinit var mContext: Context
    lateinit var recyclerView: RecyclerView
    private var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_buys, container, false)

        mContext = context as Context
        recyclerView = root.findViewById(R.id.rv_buys)
        recyclerView.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)

        updateInterface()

        RunWait().execute()

        return root
    }

    fun updateInterface() {
        val buysDB = BuysDB(mContext, "buys", null, 1)
        val db = buysDB.writableDatabase
        val buys = ArrayList<Buy>()
        val query = db.rawQuery(
            "select title, description, dataInfo, restData, percent, expireInDate, expireInHours, expireDate from buys",
            null
        )
        while (query.moveToNext()) {
            buys.add(
                Buy(
                    query.getString(0),
                    query.getString(1),
                    query.getString(2),
                    query.getString(3).toFloat(),
                    query.getString(4).toInt(),
                    query.getString(5),
                    query.getString(6),
                    query.getString(7)
                )
            )
        }
        db.close()
        val adapter = BuysAdapter(buys)
        recyclerView.adapter = adapter
    }

    inner class RunWait : AsyncTask<Void?, Void?, Void?>() {

        override fun doInBackground(vararg params: Void?): Void? {
            val time = if (count > 0) 20000L
            else 10000L
            Thread.sleep(time)
            count++
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            updateInterface()
            if (count < 5) RunWait().execute()
        }
    }
}
