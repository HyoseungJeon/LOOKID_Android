package lookid_front.lookid.Control

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import lookid_front.lookid.Activity.Map_Activity
import java.util.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import lookid_front.lookid.Entity.Group
import lookid_front.lookid.R

class Group_map_adapter(val context: Context, val resList: ArrayList<Group>?, val itemClick: (Group) -> Unit) : RecyclerView.Adapter<Group_map_adapter.holder>() {
    var num : Int = 0
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_child3, p0, false)

        var a : Int ?= resList?.size
        val dm = context.resources.displayMetrics
        val width = dm.widthPixels

        Log.i("안녕", "가로길이" + width)

        val pp = LinearLayout.LayoutParams(width, MATCH_PARENT)
        val pp2 = LinearLayout.LayoutParams(width/2, MATCH_PARENT)
        val pp3 = LinearLayout.LayoutParams(width/3, MATCH_PARENT)
        val pp4 = LinearLayout.LayoutParams(width/4, MATCH_PARENT)
        val pp5 = LinearLayout.LayoutParams(width/5, MATCH_PARENT)
        val pp6 = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)

        when (a){
            1 -> view.layoutParams = pp
            2 -> view.layoutParams = pp2
            3 -> view.layoutParams = pp3
            4 -> view.layoutParams = pp4
            5 -> view.layoutParams = pp5
            else -> {
                view.layoutParams = pp6
            }
        }
        return holder(view, itemClick)
    }


    override fun getItemCount(): Int {
        if (resList?.size == null) {
            return 0
        }
        return resList!!.size
    }

    override fun onBindViewHolder(p0: holder, p1: Int) {
        p0.bind(resList!![p1], p1)

    }

    inner class holder(view: View,itemClick: (Group) -> Unit) : RecyclerView.ViewHolder(view) {

        val resname_TextView = view.findViewById<TextView>(R.id.res_map_child_name3)
        var mapActivity:Map_Activity = Map_Activity()

        fun bind(group: Group, index: Int) {
            resname_TextView.text = group.name
            mapActivity.g_pid = group.g_pid
            itemView.setOnClickListener { itemClick(group)  }
        }
        // itemView.setBackgroundColor(context.resources.getColor(R.color.LOOKID_foreground))
        //; if(num == group.g_pid){itemView.setBackgroundColor(context.resources.getColor(R.color.LOOKID_foreground))} else{itemView.setBackgroundColor(context.resources.getColor(R.color.LOOKID_gray))} ; num = group.g_pid
    }

}