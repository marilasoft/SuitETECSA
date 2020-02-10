package cu.marilasoft.suitetecsa.ui.home


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cu.marilasoft.selibrary.models.Product
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.OperationException
import cu.marilasoft.suitetecsa.*
import cu.marilasoft.suitetecsa.utils.Communicator
import cu.marilasoft.suitetecsa.utils.ProductsDB
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

/**
 * A simple [Fragment] subclass.
 */
class Shopping : Fragment() {

    lateinit var mContext: Context
    lateinit var recyclerView: RecyclerView
    private var products = ArrayList<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_shopping, container, false)
        mContext = context as Context
        recyclerView = root.findViewById(R.id.rv_products)
        recyclerView.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        val productsWait = ArrayList<ProductWait>()
        productsWait.add(ProductWait())
        val adapter = ProductWaitAdapter(productsWait)
        recyclerView.adapter = adapter
        RunTask(mContext).execute()

        return root
    }

    fun updateInterface() {

        val adapter = ProductsAdapter(products)
        recyclerView.adapter = adapter
    }

    inner class RunTask(override var mContext: Context) : AsyncTask<Void?, Void?, Void?>(), Communicator {
        private var runError = false
        private var errorMessage = ""

        override fun onPreExecute() {
            super.onPreExecute()
        }
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                products = loadProducts() as ArrayList<Product>
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
            if (runError) showAlertDialog(errorMessage)
            else updateInterface()
        }
    }
}
