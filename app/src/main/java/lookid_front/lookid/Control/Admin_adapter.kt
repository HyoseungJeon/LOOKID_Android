package lookid_front.lookid.Control

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import lookid_front.lookid.Entity.Admin
import lookid_front.lookid.R

class Admin_adapter(val context: Context, val adminlist: ArrayList<Admin>) : RecyclerView.Adapter<Admin_adapter.holder>() {
    var delete : Boolean = false
    constructor(context: Context, adminlist: ArrayList<Admin>, delete : Boolean) : this(context,adminlist){this.delete = delete}
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_res_admin,p0,false)
        return holder(view)
    }
    override fun getItemCount(): Int { return adminlist.size }
    override fun onBindViewHolder(p0: holder, p1: Int) { p0.bind(context,adminlist[p1],p1) }

    inner class holder(view : View) : RecyclerView.ViewHolder(view) {
        val admin_TextView = view.findViewById<TextView>(R.id.res_group_admin_TextView)
        val admin_delete_Button = view.findViewById<Button>(R.id.res_admin_delete_Button)
        fun bind(context: Context, admin : Admin, index : Int) {
            admin_TextView.text = admin.name
            if(delete){
                admin_delete_Button.setOnClickListener {
                    adminlist.removeAt(index)
                    notifyItemRemoved(index)
                    notifyItemRangeChanged(index,itemCount)
                }
            }
            else
                admin_delete_Button.visibility = View.GONE
        }
    }

    fun add(admin : Admin){
        if(adminlist.size >= 20){
            Toast.makeText(context,"최대 관리자는 20명 입니다",Toast.LENGTH_SHORT).show()
            return
        }
        if(adminlist.contains(admin)){
            Toast.makeText(context, "이미 존재하는 관리자 입니다",Toast.LENGTH_SHORT).show()
            return
        }
        adminlist.add(admin)
        notifyDataSetChanged()
    }
}