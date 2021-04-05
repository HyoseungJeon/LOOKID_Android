package lookid_front.lookid.Control

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import lookid_front.lookid.Entity.Child
import lookid_front.lookid.R

class Child_adapter(val context: Context) : RecyclerView.Adapter<Child_adapter.holder>() {
    var res_state : Boolean = false
    var childlist = ArrayList<Child>()
    val textWatcher_ary = arrayListOf<EditListener>()
    constructor(context : Context, res_state : Boolean) : this(context){
        this.res_state = res_state
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_res_child,p0,false)
        for(i in 0 until 100)
            textWatcher_ary.add(EditListener(i))
        return holder(view)
    }
    override fun getItemCount(): Int { return childlist.size }
    override fun onBindViewHolder(p0: holder, p1: Int) { p0.bind(context,childlist[p1].c_name,p1)}

    inner class holder(view: View) : RecyclerView.ViewHolder(view) {
        val childEditText = view.findViewById<EditText>(R.id.res_child_name_EditText)
        val indexTextView = view.findViewById<TextView>(R.id.res_child_index_TextView)

        fun bind(context: Context, child : String, index: Int){
            indexTextView.text = (index + 1).toString()
            if(res_state)
                childEditText.isEnabled = false
            if(index < itemCount) {
                childEditText.setText(childlist[index].c_name)
            }
            for(i in 0 until itemCount)
                childEditText.removeTextChangedListener(textWatcher_ary[i])
            childEditText.addTextChangedListener(textWatcher_ary[index])
        }
    }
    fun checklist():Boolean{
        var str : String = ""
        var count = 0
        for(i in 0 until childlist.size){
            if(childlist[i].c_name.isEmpty()){
                str = str + (i + 1).toString() + ", "
                count++
            }
            if(count >= 10){
                Toast.makeText(context,"10개 이상의 칸이 비어있습니다",Toast.LENGTH_SHORT).show()
                return false
            }
        }
        if(str != ""){
            str = str.substring(0, str.lastIndex - 1)
            Toast.makeText(context,"$str 번째 이름을 입력해주세요",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    fun list_init(size : Int){
        if(size > 100){
            Toast.makeText(context,"최대 피보호자 수는 100명 입니다",Toast.LENGTH_SHORT).show()
            return
        }
        if(childlist.size < size){
            for(i in childlist.size until size){
                childlist.add(Child())
                notifyItemChanged(i)
            }
        }
        else if(size < childlist.size){
            while (size != childlist.size){
                notifyItemRemoved(childlist.lastIndex)
                childlist.removeAt(childlist.lastIndex)
            }
        }
        Log.d("Res_Group", childlist.toString())
    }

    fun setlist(list : ArrayList<Child>){
        childlist = list
        notifyDataSetChanged()
    }
    fun getlist():ArrayList<Child>{return childlist}

    inner class EditListener(var index: Int) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if(!s.toString().isEmpty()) {
                if(index < itemCount)
                    childlist[index].c_name = s.toString()
            }
            else {
                childlist[index].c_name = ""
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
}