package lookid_front.lookid.Dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import lookid_front.lookid.Activity.SignIn_Activity
import lookid_front.lookid.R

class Basic_Dialog(val context: Context, val title : String, val message : String, val postiveListener : DialogInterface.OnClickListener){
    var dialog : AlertDialog
    val builder = AlertDialog.Builder(context, R.style.DialogStyle)
    constructor(context: Context, title: String, message: String, postiveListener: DialogInterface.OnClickListener, negtiveButton : Boolean)
    :this(context,title,message,postiveListener){
        builder.setNegativeButton("취소",null)
        dialog = builder.create() as AlertDialog}
    init {
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("확인",postiveListener)
                .setIcon(R.drawable.icon_info)!!
        dialog = builder.create() as AlertDialog
    }
    fun show(){
        dialog.show()
    }
}