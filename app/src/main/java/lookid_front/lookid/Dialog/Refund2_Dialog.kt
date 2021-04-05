package lookid_front.lookid.Dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import lookid_front.lookid.R

class Refund2_Dialog(val context : Context){
    val builder = AlertDialog.Builder(context,R.style.DialogStyle)
            .setTitle("결제 약관")
            .setMessage(context.resources.getString(R.string.refund_content))
            .setPositiveButton("확인", null)
            .setIcon(R.drawable.icon_info)
    val dialog = builder.create() as AlertDialog
    fun show(){
        dialog.show()
    }
}