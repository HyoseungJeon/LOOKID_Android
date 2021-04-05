package lookid_front.lookid.Dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import lookid_front.lookid.R

class Exit_Dialog(context : Context) : AlertDialog.Builder(context, R.style.DialogStyle){
    private val act = context as Activity
    override fun create(): AlertDialog {
        setTitle("종료")
        setMessage("정말로 종료하시겠습니까?")
        setPositiveButton("종료") { _, _ -> act.finish()}
        setNegativeButton("취소",null)
        return super.create()
    }
}