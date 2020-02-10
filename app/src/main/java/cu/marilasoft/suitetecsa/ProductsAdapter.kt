package cu.marilasoft.suitetecsa

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cu.marilasoft.selibrary.models.Product
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.OperationException
import cu.marilasoft.suitetecsa.utils.Communicator
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

class ProductsAdapter(private var list: ArrayList<Product>) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindItem(data: Product) {
            val title: TextView = itemView.findViewById(R.id.tv_title)
            val description: TextView = itemView.findViewById(R.id.tv_description)
            val price: TextView = itemView.findViewById(R.id.tv_price)
            val buy: Button = itemView.findViewById(R.id.btn_buy)
            val cookies: MutableMap<String, String> = HashMap()
            cookies["portaluser"] = SharedApp.prefs.portalUser
            cookies["DRUTT_DSERVER_SESSIONID"] = SharedApp.prefs.sessionId

            title.text = data.title
            description.text = data.description
            var priceFormatted = data.price.toString()
            if (data.price.toString().contains(".")) {
                val size = data.price.toString().toCharArray().size
                var position = 0
                for (point in data.price.toString().toCharArray()) {
                    if (point == '.') break
                    else position++
                }
                if (position + 2 == size) priceFormatted = "${data.price}0 CUC"
            } else priceFormatted = "${data.price}.00 CUC"
            price.text = priceFormatted
            buy.setOnClickListener {
                RunTask(itemView.context, data).execute()
            }
        }

        inner class RunTask(override var mContext: Context, var product: Product) :
            AsyncTask<Void?, Void?, Void?>(), Communicator {
            private var runError = false
            private var errorMessage = ""
            private val progressDialog = customProgressBar

            override fun onPreExecute() {
                super.onPreExecute()
                progressDialog.show(mContext, "Por favor, espere...")
            }

            override fun doInBackground(vararg params: Void?): Void? {
                try {
                    enableSSLSocket()
                    val cookies = HashMap<String, String>()
                    cookies["portaluser"] = SharedApp.prefs.portalUser
                    cookies["DRUTT_DSERVER_SESSIONID"] = SharedApp.prefs.sessionId
                    product.buy(product.urlBuyAction, cookies)
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
                if (progressDialog.dialog.isShowing) progressDialog.dialog.dismiss()
                if (runError) showAlertDialog(errorMessage)
                else showAlertDialog("Compra satisfactoria!")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list[position])
    }
}