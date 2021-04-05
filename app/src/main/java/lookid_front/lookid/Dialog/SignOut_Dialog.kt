package lookid_front.lookid.Dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import lookid_front.lookid.Activity.SignIn_Activity
import lookid_front.lookid.Control.User_Control
import lookid_front.lookid.R

class SignOut_Dialog(val context: Context, val title : String, val message : String){
    val act = context as Activity
    val builder = AlertDialog.Builder(context, R.style.DialogStyle)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인", DialogInterface.OnClickListener { _, _ ->
                context.startActivity(Intent(context,SignIn_Activity::class.java))
                User_Control(context).set_auto_login(false)
                act.finish()
            })
            .setNegativeButton("취소",null)
            .setIcon(R.drawable.icon_info)
    val dialog = builder.create() as AlertDialog
    fun show(){
        dialog.show()
    }
}