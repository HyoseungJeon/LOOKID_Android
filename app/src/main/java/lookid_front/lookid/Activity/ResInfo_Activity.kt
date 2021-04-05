package lookid_front.lookid.Activity

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_reservation.*
import kotlinx.android.synthetic.main.activity_resinfo.*
import lookid_front.lookid.Control.*
import lookid_front.lookid.Dialog.Address_Dialog
import lookid_front.lookid.Dialog.Bank_Dialog
import lookid_front.lookid.Dialog.Basic_Dialog
import lookid_front.lookid.Entity.Reservation_Entity
import lookid_front.lookid.R
import lookid_front.lookid.R.id.*
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ResInfo_Activity : AppCompatActivity() {
    lateinit var res : Reservation_Entity
    lateinit var bank_list : Array<String>
    lateinit var group_Adapter : Group_adapter
    val dateFormat = SimpleDateFormat(Date_Control().dateFormat, Locale.KOREA)
    var state : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resinfo)
        ResInfo_Control().init()
    }
    inner class ResInfo_Control(){
        fun init(){
            bank_list = resources.getStringArray(R.array.bank_list)
            val intent = getIntent()
            res = intent.getSerializableExtra("res") as Reservation_Entity
            GET_res_detail()

            if(res.state == 1)
                resinfo_modify_Button.visibility = View.VISIBLE
            else
                resinfo_modify_Button.visibility = View.GONE

            init_view()
            init_state(state)
        }
        fun init_state(modify : Boolean){
            val viewlist_enabled = arrayListOf<View>(resinfo_resname_EditText,resinfo_name_EditText,resinfo_phone_EditText,resinfo_bank_number_EditText
                    ,resinfo_bank_holder_EditText,resinfo_rec_del_RadioButton,resinfo_rec_vis_RadioButton,resinfo_ret_del_RadioButton,resinfo_ret_vis_RadioButton,resinfo_addressDet_EditText
                    ,resinfo_findadd_Button, resinfo_grouplist_RecView, resinfo_startdate_Button,resinfo_enddate_Button)
            val viewlist_vis = arrayListOf<View>(resinfo_groupadd_Button,resinfo_auto_CheckBox, resinfo_bank_name_ImageButton)
            if(modify){
                for(i in 0 until viewlist_enabled.size)
                    viewlist_enabled[i].isEnabled = true
                for(i in 0 until viewlist_vis.size)
                    viewlist_vis[i].visibility = View.VISIBLE
                group_Adapter = Group_adapter(this@ResInfo_Activity,res.group_list,modify,"ResInfo")
                resinfo_grouplist_RecView.adapter = group_Adapter
            }
            else{
                for(i in 0 until viewlist_enabled.size)
                    viewlist_enabled[i].isEnabled = false
                for(i in 0 until viewlist_vis.size)
                    viewlist_vis[i].visibility = View.GONE
                group_Adapter = Group_adapter(this@ResInfo_Activity,res.group_list,modify,"ResInfo")
                resinfo_grouplist_RecView.adapter = group_Adapter
            }
        }
        fun init_view(){
            resinfo_resname_EditText.setText(res.r_name)
            val state_list = resources.getStringArray(R.array.state_list)
            resinfo_state_TextView.text = state_list[res.state]
            if(res.state > 1)
                resinfo_delete_Button.visibility = View.GONE
            resinfo_name_EditText.setText(res.user.name)
            resinfo_phone_EditText.setText(res.user.phone)
            if(res.receipt_item == 0)
                resinfo_rec_del_RadioButton.isChecked = true
            else
                resinfo_rec_vis_RadioButton.isChecked = true
            if(res.return_item == 0)
                resinfo_ret_del_RadioButton.isChecked = true
            else
                resinfo_ret_vis_RadioButton.isChecked = true
            resinfo_bank_name_TextView.text = res.user.bank_name
            resinfo_bank_number_EditText.setText(res.user.bank_num)
            resinfo_bank_holder_EditText.setText(res.user.bank_holder)
            resinfo_startdate_TextView.text = dateFormat.format(Date(res.s_date))
            resinfo_enddate_TextView.text = dateFormat.format(Date(res.e_date))
            resinfo_address_EditText.setText(res.user.address)
            resinfo_addressDet_EditText.setText(res.user.address_detail)
            group_Adapter = Group_adapter(this@ResInfo_Activity,res.group_list,true,"ResInfo")
            resinfo_grouplist_RecView.adapter = group_Adapter
            resinfo_grouplist_RecView.layoutManager = LinearLayoutManager(applicationContext)
            resinfo_grouplist_RecView.setItemViewCacheSize(100)
            pay_init() //금액 정보 view 초기화
        }
        fun pay_init(){
            val devicenum = group_Adapter.getDevice_num()
            val startdate = dateFormat.parse(resinfo_startdate_TextView.text.toString())
            val enddate = dateFormat.parse(resinfo_enddate_TextView.text.toString())
            val useDay : Long = (enddate.time - startdate.time) / (24*60*60*1000)
            Log.d("ResInfo_Activity","device num : $devicenum, useDay : $useDay")

            if(devicenum == 0|| useDay.toInt() == 0)
                return

            val payformat = DecimalFormat("###,###")
            var res_pay : Int = 0
            var res_deposit : Int = 0
            var res_postpay : Int = 0

            if(useDay > 0 && devicenum > 0){
                res_pay = (useDay * devicenum * 1500).toInt()
                resinfo_pay_TextView.text = payformat.format(res_pay)
                res_deposit = devicenum * 1000
                resinfo_deposit_TextView.text = payformat.format(res_deposit)
                res.deposit = res_deposit
                if((useDay * devicenum * 1500) < 50000){
                    res_postpay = 5000
                    resinfo_postpay_TextView.text = payformat.format(res_postpay)
                }
                resinfo_totalpay_TextView.text = payformat.format(res_pay + res_deposit + res_postpay)
                res.cost = res_pay + res_deposit + res_postpay
            }
        }

        fun res_init():Boolean{
            res.r_name = resinfo_resname_EditText.text.toString()
            res.user.name = resinfo_name_EditText.text.toString()
            res.user.phone = resinfo_phone_EditText.text.toString()
            res.user.bank_num = resinfo_bank_number_EditText.text.toString()
            res.user.bank_name = resinfo_bank_name_TextView.text.toString()
            res.user.bank_holder = resinfo_bank_holder_EditText.text.toString()
            res.r_date = Date().time
            res.s_date = dateFormat.parse(resinfo_startdate_TextView.text.toString()).time
            res.e_date = dateFormat.parse(resinfo_enddate_TextView.text.toString()).time
            res.user.address = resinfo_address_EditText.text.toString()
            res.user.address_detail = resinfo_addressDet_EditText.text.toString()
            res.state = 1
            res.group_list = group_Adapter.grouplist

            Log.d("ResInfo_activity",res.toString())
            return res.null_res()
        }

        fun user_init(state : Boolean){
            val viewlist = arrayListOf<View>(resinfo_name_EditText,resinfo_phone_EditText,resinfo_bank_number_EditText,resinfo_bank_holder_EditText, resinfo_bank_name_ImageButton)
            if(state){
                val user = User_Control(applicationContext).get_user()
                //Toast.makeText(applicationContext,user.toString(),Toast.LENGTH_SHORT).show()
                resinfo_name_EditText.setText(user.name)
                resinfo_phone_EditText.setText(user.phone)
                resinfo_bank_name_TextView.text = user.bank_name
                resinfo_bank_number_EditText.setText(user.bank_num)
                resinfo_bank_holder_EditText.setText(user.bank_holder)
                for(i in 0 until viewlist.size)
                    viewlist[i].isEnabled = false
            }
            else{
                resinfo_name_EditText.text = null
                resinfo_phone_EditText.text = null
                resinfo_bank_name_TextView.text = null
                resinfo_bank_number_EditText.text = null
                resinfo_bank_holder_EditText.text = null
                for(i in 0 until viewlist.size)
                    viewlist[i].isEnabled = true
            }
        }

        fun Dialog_DatePicker(state : Int){
            var listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                when(state){
                    0-> {
                        resinfo_startdate_TextView.text = Date_Control().toDateformat(year,month,dayOfMonth)
                        val startdate = dateFormat.parse(resinfo_startdate_TextView.text.toString())
                        val enddate = dateFormat.parse(resinfo_enddate_TextView.text.toString())
                        if(startdate.after(enddate))
                            resinfo_enddate_TextView.text = Date_Control().toDateformat(year,month,dayOfMonth)
                    }
                    1-> {
                        val startdate = dateFormat.parse(resinfo_startdate_TextView.text.toString())
                        val enddate = dateFormat.parse("${year}-${month}-${dayOfMonth}")
                        if(enddate.before(startdate))
                            Toast.makeText(applicationContext,"종료일이 시작일 이전 입니다",Toast.LENGTH_SHORT).show()
                        else
                            resinfo_enddate_TextView.text = Date_Control().toDateformat(year,month,dayOfMonth)
                    }
                }
                ResInfo_Control().pay_init()
            }

            val cal = Calendar.getInstance()
            var date : Date = dateFormat.parse(resinfo_startdate_TextView.text.toString())
            if(state == 1)
                date = dateFormat.parse(resinfo_enddate_TextView.text.toString())
            cal.time = date
            val dateDialog = DatePickerDialog(this@ResInfo_Activity, listener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),
                    cal.get(Calendar.DATE))
            dateDialog.show()
        }

        fun Dialog_search_Address(){
            val Address_Dialog = Address_Dialog(this@ResInfo_Activity, resinfo_address_EditText)
            Address_Dialog.show()
        }

        fun Dialog_bankname(){
            val bank_Dialog = Bank_Dialog(this@ResInfo_Activity,resinfo_bank_name_TextView.text.toString())
            bank_Dialog.setOnShowListener(Dialog_Listener())
            bank_Dialog.show()
        }
        fun Dialog_cancel(){
            Basic_Dialog(this@ResInfo_Activity,"예약 취소","정말로 취소하시겠습니까?", DialogInterface.OnClickListener { _, _ ->
                DELETE_res_cancel()
            },true).show()
        }
        fun GET_res_detail(){ //0
            var url: String = getString(R.string.server_url) + getString(R.string.selete_res_detail)
            url = url.replace("{rv_pid}",res.rv_pid.toString())
            asynctask().execute("0",url)
        }
        fun PUT_res_modify(){ //1
            val url = getString(R.string.server_url) + getString(R.string.reservation_modify)
            asynctask().execute("1",url,Json().reservation(res))
        }
        fun DELETE_res_cancel(){
            val url = getString(R.string.server_url) + getString(R.string.reservation_cancel)
            asynctask().execute("2",url,Json().res_delete(res.rv_pid))
        }
    }

    inner class asynctask : AsyncTask<String,Void,String>(){
        var state :Int = -1;
        var url = ""
        override fun doInBackground(vararg params: String): String {
            var response : String = ""
            state = params[0].toInt()
            url = params[1]
            when(state){
                0->response = Okhttp(applicationContext).GET(url)
                1->response = Okhttp(applicationContext).PUT(url,params[2])
                2->response = Okhttp(applicationContext).DELETE(url,params[2])
            }
            return response
        }
        override fun onPostExecute(response: String) {
            Log.d("ResInfo_Activity",url)//response 값이 json문이 아니면 통신 오류 메세지 출력
            if(response.isNullOrEmpty()) {//넘어온 값이 없을 때 로그 찍고 리턴
                Toast.makeText(applicationContext,"서버 문제 발생", Toast.LENGTH_SHORT).show()
                Log.d("ResInfo_Activity", url)
                Log.d("ResInfo_Activity", "null in")
                return
            }
            Log.d("ResInfo_Activity",response)//response 값이 json문이 아니면 통신 오류 메세지 출력
            if(!Json().isJson(response)){
                Toast.makeText(applicationContext,"네트워크 통신 오류", Toast.LENGTH_SHORT).show()
                return
            }
            when(state){
                0-> {//GET_res_detail
                    res = Json().to_Res(response)
                    ResInfo_Control().init_view()
                    Log.d("ResInfo",res.toString())
                }
                1->{
                    val json = JSONObject(response)
                    if(json.getBoolean("success")){
                        Toast.makeText(applicationContext,"예약 수정 완료",Toast.LENGTH_SHORT).show()
                        this@ResInfo_Activity.state = !this@ResInfo_Activity.state
                        resinfo_modify_Button.background = getDrawable(R.drawable.icon_modify)
                        ResInfo_Control().init_state(this@ResInfo_Activity.state) //view를 수정 불가능으로 초기화
                    }
                    else{
                        Toast.makeText(applicationContext,"예약 수정 실패",Toast.LENGTH_SHORT).show()
                        ResInfo_Control().init()
                        this@ResInfo_Activity.state = !this@ResInfo_Activity.state
                        resinfo_modify_Button.background = getDrawable(R.drawable.icon_modify)
                        ResInfo_Control().init_state(this@ResInfo_Activity.state)
                    }
                }
                2->{
                    val json = JSONObject(response)
                    if(json.getBoolean("success")){
                        Toast.makeText(applicationContext,"예약 삭제 완료",Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else
                        Toast.makeText(applicationContext,"예약 삭제 실패",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    inner class Dialog_Listener : DialogInterface.OnShowListener{
        override fun onShow(dialog: DialogInterface?) {
            val dialog = dialog as Bank_Dialog
            val bank_check_Button = dialog.findViewById<Button>(R.id.dialog_bank_check_Button)
            bank_check_Button.setOnClickListener {
                if(!dialog.getBank().isNullOrEmpty()) {
                    resinfo_bank_name_TextView.setText(dialog.getBank())
                    res.user.bank_name = dialog.getBank()
                    dialog.dismiss()
                }
                else
                    Toast.makeText(applicationContext,"은행을 선택해주세요",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun resinfo_Click_Listener(view: View){
        when(view.id){
            R.id.resinfo_modify_Button->{
                if(!state){ //수정 시도
                    state = !state
                    ResInfo_Control().init_state(state) //view를 수정 가능으로 초기화
                    resinfo_modify_Button.background = getDrawable(R.drawable.icon_save)
                }
                else{ //수정 완료
                    if(ResInfo_Control().res_init()) {//수정 내용 체크
                        Toast.makeText(applicationContext,"예약 정보를 모두 입력해주세요",Toast.LENGTH_SHORT).show()
                        return
                    }
                    //서버에 보내기
                    ResInfo_Control().PUT_res_modify()
                }
            }
            R.id.resinfo_groupadd_Button->{
                group_Adapter.add()
                resinfo_grouplist_RecView.scrollToPosition(group_Adapter.itemCount - 1)
            }
            R.id.resinfo_auto_CheckBox->ResInfo_Control().user_init(resinfo_auto_CheckBox.isChecked)
            R.id.resinfo_startdate_Button->ResInfo_Control().Dialog_DatePicker(0)
            R.id.resinfo_enddate_Button->ResInfo_Control().Dialog_DatePicker(1)
            R.id.resinfo_findadd_Button->ResInfo_Control().Dialog_search_Address()
            R.id.resinfo_rec_del_RadioButton ->{res.receipt_item = 0}
            R.id.resinfo_rec_vis_RadioButton ->{res.receipt_item = 1}
            R.id.resinfo_ret_del_RadioButton ->{res.return_item = 0}
            R.id.resinfo_ret_vis_RadioButton ->{res.return_item = 1}
            R.id.resinfo_bank_name_ImageButton ->{ResInfo_Control().Dialog_bankname()}
            R.id.resinfo_delete_Button->{ResInfo_Control().Dialog_cancel() }
        }
    }
    override fun onPause() {
        asynctask().cancel(true)
        super.onPause()
    }
}