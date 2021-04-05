package lookid_front.lookid.Control

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import lookid_front.lookid.Entity.Child
import lookid_front.lookid.R
import java.util.*


class Missing_adapter(val context: Context, val resList : ArrayList<Child>?, val itemClick: (Child) -> Unit)  : RecyclerView.Adapter<Missing_adapter.holder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): holder {

        val view = LayoutInflater.from(context).inflate(R.layout.row_child2, p0, false)
        return holder(view,itemClick)

    }

    override fun getItemCount(): Int {
        if (resList?.size == null) {
            return 0
        }
        return resList!!.size
    }

    override fun onBindViewHolder(p0: holder, p1: Int) {
        if(resList!![p1].isMissing)
            p0.bind(resList!![p1], p1)
    }

    inner class holder(itemView: View, itemClick: (Child) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val resname_TextView = itemView.findViewById<TextView>(R.id.res_map_child_name2)
        fun bind(child: Child, index: Int) {
            resname_TextView.text = child.c_name
            itemView.setOnClickListener { itemClick(child) }
        }
    }
}