package lookid_front.lookid.Dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.widget.TextView
import lookid_front.lookid.R

class Refund_Dialog(context : Context) : AlertDialog.Builder(context,R.style.DialogStyle){
    private val act = context as Activity
    override fun create(): AlertDialog {
        setTitle("결제 약관")
        setMessage(context.resources.getString(R.string.refund_content))
        setPositiveButton("확인", null)
        setIcon(R.drawable.icon_info)

        return super.create()
    }
}