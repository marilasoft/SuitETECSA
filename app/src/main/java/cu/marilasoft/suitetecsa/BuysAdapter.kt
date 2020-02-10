package cu.marilasoft.suitetecsa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class BuysAdapter(var list: ArrayList<Buy>): RecyclerView.Adapter<BuysAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bindItem(data: Buy) {
            val title: TextView = itemView.findViewById(R.id.tv_buy_title)
            val description: TextView = itemView.findViewById(R.id.tv_buy_description)
            val percent: CircularProgressBar = itemView.findViewById(R.id.cpb_consumption_progress)
            val rest: TextView = itemView.findViewById(R.id.tv_buy_rest)
            val unit: TextView = itemView.findViewById(R.id.tv_buy_unit)
            val expire: TextView = itemView.findViewById(R.id.tv_buy_expire_date)
            val descriptionAction: TextView = itemView.findViewById(R.id.tv_buy_description_action)

            title.text = data.title
            description.text = data.description
            percent.setProgressWithAnimation(data.percent.toFloat(), 1000)
            rest.text = data.restData.toString()
            unit.text = data.dataInfo
            expire.text = "Expira el: ${data.expireDate}"
            descriptionAction.setOnClickListener {
                if (description.isVisible) description.visibility = View.GONE
                else description.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.package_buy_item, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list[position])
    }
}