package lookid_front.lookid.Control

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import lookid_front.lookid.Entity.Group
import lookid_front.lookid.R

class Group_adapter_ResLast(val context: Context, val grouplist : ArrayList<Group>) : RecyclerView.Adapter<Group_adapter_ResLast.holder>() {
    var child_adapter : Child_adapter = Child_adapter(context)
    var dialog : AlertDialog? = null
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_reslast_group,p0,false)
        return holder(view)
    }
    override fun getItemCount(): Int { return grouplist.size }
    override fun onBindViewHolder(p0: holder, p1: Int) { p0.bind(grouplist[p1],context,p1) }

    inner class holder(view : View) : RecyclerView.ViewHolder(view){
        val name_TextView = view.findViewById<TextView>(R.id.reslast_group_name_TextView)
        val child_Button = view.findViewById<Button>(R.id.reslast_group_child_Button)
        val adminlist_RecView = view.findViewById<RecyclerView>(R.id.reslast_group_admin_RecView)

        fun bind(group : Group, context : Context, id : Int) {
            val index = id
            val admin_adapter = Admin_adapter(context, grouplist[index].admin_list)
            name_TextView.setText(group.name)
            adminlist_RecView.adapter = admin_adapter
            child_Button.setOnClickListener(Click_listener(index))
        }
    }
    //피보호자 리스트 다이얼로그를 띄워주는 함수
    fun Dialog_child(index : Int){
        val builder = AlertDialog.Builder(this.context,R.style.DialogStyle_child)
        builder.setTitle("피보호자 목록")
        var inflater  = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_res_childlist,null))
        builder.setPositiveButton("확인",null)
        //builder.setNegativeButton("취소",null)
        dialog = builder.create() as AlertDialog
        dialog!!.setOnShowListener(Dialog_Listener(index))
        dialog!!.show()
    }
    //어뎁터 클릭 리스너
    inner class Click_listener(val index : Int) : View.OnClickListener{
        override fun onClick(v: View) {
            when(v.id){
                R.id.reslast_group_child_Button->{ Dialog_child(index) }
            }
        }
    }
    //다이얼로그 리스너
    inner class Dialog_Listener(var index : Int) : DialogInterface.OnShowListener{
        override fun onShow(dialog: DialogInterface?) {
            val alert = dialog as AlertDialog
            val positiveButton : Button = alert.getButton(AlertDialog.BUTTON_POSITIVE)
            val child_View = alert.findViewById<LinearLayout>(R.id.dialog_res_child_View)
            val child_Rec = alert.findViewById<RecyclerView>(R.id.dialog_res_child_list_RecView)
            child_View.visibility = View.GONE
            child_adapter = Child_adapter(context,true)
            child_adapter.setlist(grouplist[index].child_list)
            child_Rec.adapter = child_adapter
            child_Rec.layoutManager = LinearLayoutManager(context)
            child_Rec.setItemViewCacheSize(100)

            positiveButton.setOnClickListener {
                if(child_adapter.checklist()){
                    alert.dismiss()
                }
            }
        }
    }
}