package lookid_front.lookid.Control

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import lookid_front.lookid.Activity.ResInfo_Activity
import lookid_front.lookid.Entity.Reservation_Entity
import lookid_front.lookid.R
import lookid_front.lookid.R.id.res_startdate_TextView
import java.text.SimpleDateFormat
import java.util.*

class Res_adapter(val context: Context, val resList : ArrayList<Reservation_Entity>) : RecyclerView.Adapter<Res_adapter.holder>() {
    val dateFormat = SimpleDateFormat(Date_Control().dateFormat, Locale.KOREA)
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_res,p0,false)
        return holder(view)
    }
    override fun getItemCount(): Int { return resList.size }
    override fun onBindViewHolder(p0: holder, p1: Int) { p0.bind(resList[p1],p1) }

    inner class holder(view : View) : RecyclerView.ViewHolder(view){
        val res_View = view.findViewById<LinearLayout>(R.id.res_view)
        val resname_TextView = view.findViewById<TextView>(R.id.res_resname)
        val resdate_TextView = view.findViewById<TextView>(R.id.res_date)
        val resstate_TextView = view.findViewById<TextView>(R.id.res_state)
        fun bind(res : Reservation_Entity, index : Int){
            resname_TextView.text = res.r_name
            resdate_TextView.text = "${dateFormat.format(Date(res.s_date))} ~ ${dateFormat.format(Date(res.e_date))}"
            val state_list = context.resources.getStringArray(R.array.state_list)
            resstate_TextView.text = state_list[res.state]
            res_View.setOnClickListener {
                val intent = Intent(context, ResInfo_Activity::class.java)
                res.state = 1
                intent.putExtra("res",res)
                context.startActivity(intent)
            }
        }
    }
}