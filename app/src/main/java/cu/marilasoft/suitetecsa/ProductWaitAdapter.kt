package cu.marilasoft.suitetecsa

import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView

class ProductWaitAdapter(private var list: ArrayList<ProductWait>): RecyclerView.Adapter<ProductWaitAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        lateinit var v1: View
        lateinit var v2: View
        lateinit var v3: View
        lateinit var v4: View
        lateinit var v5: View
        lateinit var animation: Animation

        fun bindItem(data: ProductWait) {
            v1 = itemView.findViewById(R.id.v1)
            v2 = itemView.findViewById(R.id.v2)
            v3 = itemView.findViewById(R.id.v3)
            v4 = itemView.findViewById(R.id.v4)
            v5 = itemView.findViewById(R.id.v5)

            animation = AnimationUtils.loadAnimation(itemView.context, R.anim.to_rigth)
            animation.repeatCount = 9

            v1.startAnimation(animation)
            v2.startAnimation(animation)
            v3.startAnimation(animation)
            v4.startAnimation(animation)
            v5.startAnimation(animation)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.product_wait_item, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list[position])
    }
}