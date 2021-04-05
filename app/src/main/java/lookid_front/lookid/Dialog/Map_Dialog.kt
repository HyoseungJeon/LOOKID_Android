package lookid_front.lookid.Dialog

import android.app.AlertDialog
import android.content.Context
import lookid_front.lookid.R

class Map_Dialog(context: Context) : AlertDialog.Builder(context, R.style.DialogStyle){
    override fun create(): AlertDialog {
        setTitle("마커 색의 의미")
        setMessage(context.resources.getString(R.string.map_info))
        setPositiveButton("확인", null)
        return super.create()
    }

}