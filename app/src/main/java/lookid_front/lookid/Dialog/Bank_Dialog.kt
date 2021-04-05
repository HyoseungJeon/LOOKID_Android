package lookid_front.lookid.Dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.View
import lookid_front.lookid.R
import android.widget.RadioButton
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_bank.*


class Bank_Dialog(context: Context, var bankname : String?) : Dialog(context){
    val banklist : Array<String> = context.resources.getStringArray(R.array.bank_list)
    var textView : TextView? = null
    constructor(context: Context, bankname: String?, textView: TextView) : this(context,bankname){ this.textView = textView}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_bank)
        //requestWindowFeature(Window.FEATURE_NO_TITLE)
        //window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //배경 투명으로

        //디스플레이 사이즈 가져옴
        val dm = context.resources.displayMetrics
        //가로를 디스플레이의 0.78만큼, 세로를 디스플레이의 0.6만큼 조정
        window.setLayout((dm.widthPixels * 0.78).toInt(),(dm.heightPixels * 0.6).toInt())

        for(i in 0 until banklist.size){
            val radioButton = RadioButton(context)
            radioButton.id = i
            radioButton.text = banklist[i]
            val colorStateList = context.resources.getColorStateList(R.color.LOOKID_second_ground)
            radioButton.setPadding(20,0,0,0)
            radioButton.buttonTintList = colorStateList
            radioButton.height = context.resources.getDimension(R.dimen.dialog_bank_row_height).toInt()
            radioButton.textSize = 16f
            dialog_bank_RadioGroup.addView(radioButton)
        }
        if(!bankname.isNullOrEmpty())
            dialog_bank_RadioGroup.check(banklist.indexOf(bankname))
        else{
            dialog_bank_RadioGroup.check(0)
            bankname = banklist[0]
        }
        dialog_bank_RadioGroup.setOnCheckedChangeListener { group, checkedId ->
            bankname = banklist[checkedId]
        }
        dialog_bank_cancel_Button.setOnClickListener(Click_Listener())
        dialog_bank_check_Button.setOnClickListener(Click_Listener())
    }

    fun getBank() : String?{ return bankname}

    inner class Click_Listener : View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.dialog_bank_cancel_Button -> dismiss()
                R.id.dialog_bank_check_Button ->{
                    textView!!.text = bankname
                    dismiss()
                }
            }
        }
    }
}