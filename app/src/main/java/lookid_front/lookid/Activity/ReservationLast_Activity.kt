package lookid_front.lookid.Activity

import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_reservation_last.*
import lookid_front.lookid.Control.Date_Control
import lookid_front.lookid.Control.Group_adapter_ResLast
import lookid_front.lookid.Control.Json
import lookid_front.lookid.Control.Okhttp
import lookid_front.lookid.Dialog.Basic_Dialog
import lookid_front.lookid.Dialog.Refund_Dialog
import lookid_front.lookid.Entity.Reservation_Entity
import lookid_front.lookid.R
import lookid_front.lookid.R.id.*
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ReservationLast_Activity : AppCompatActivity(){
    lateinit var reservation_Entity : Reservation_Entity
    var devicenum : Int = 0
    lateinit var group_Adapter : Group_adapter_ResLast
    val dateFormat = SimpleDateFormat(Date_Control().dateFormat, Locale.KOREA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_last)
        ResLast_Control().res_init()
    }

    inner class ResLast_Control {
        //page 초기화
        fun res_init(){
            val intent = intent
            reservation_Entity = intent.getSerializableExtra("res") as Reservation_Entity
            devicenum = intent.getIntExtra("res_devicenum",0)
            val useday = intent.getLongExtra("res_useday",0)

            //결제 정보 초기화
            reslast_resname_TextView.text = reservation_Entity.r_name
            reslast_name_TextView.text = reservation_Entity.user.name
            reslast_phone_TextView.text = reservation_Entity.user.phone
            reslast_bank_TextView.text = reservation_Entity.user.bank_toString()
            reslast_startdate_TextView.text = dateFormat.format(reservation_Entity.s_date)
            reslast_enddate_TextView.text = dateFormat.format(reservation_Entity.e_date)
            reslast_address_TextView.text = (reservation_Entity.user.address + " " +reservation_Entity.user.address_detail)
            reslast_devicenum_TextView.text = devicenum.toString()

            val payformat = DecimalFormat("###,###")
            reslast_pay_TextView.text = payformat.format(useday * devicenum * 1500)
            reslast_deposit_TextView.text = payformat.format(reservation_Entity.deposit)
            if(useday * devicenum * 1500 < 50000)
                reslast_postpay_TextView.text = payformat.format(5000)
            reslast_totalpay_TextView.text = payformat.format(reservation_Entity.cost)
            if(reservation_Entity.receipt_item == 0)
                reslast_rec_TextView.text = "택배"
            else
                reslast_rec_TextView.text = "방문"
            if(reservation_Entity.return_item == 0)
                reslast_ret_TextView.text = "택배"
            else
                reslast_ret_TextView.text = "방문"

            //그룹 리사이클러뷰 초기화
            group_Adapter = Group_adapter_ResLast(this@ReservationLast_Activity,reservation_Entity.group_list)
            reslast_grouplist_RecView.adapter = group_Adapter
            reslast_grouplist_RecView.setItemViewCacheSize(100)
        }

        fun Dialog_res(){
            Basic_Dialog(this@ReservationLast_Activity,"예약신청 완료",resources.getString(R.string.res_content)
            , DialogInterface.OnClickListener { _, _ ->
                startActivity(Intent(applicationContext, Main_Activity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            },true).show()
        }

        fun Dialog_cancel(){
            Basic_Dialog(this@ReservationLast_Activity,"취소","정말로 취소하시겠습니까?", DialogInterface.OnClickListener { _, _ ->
                val intent = Intent(applicationContext, Main_Activity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            },true).show()
        }
        fun POST_res(){
            val url = getString(R.string.server_url)+getString(R.string.reservation)
            Log.d("ResLast_Activity",Json().reservation(reservation_Entity))
            asynctask().execute(url,Json().reservation(reservation_Entity))
        }
    }
    inner class asynctask : AsyncTask<String, Void, String>(){
        var url = ""
        override fun doInBackground(vararg params: String): String {
            //POST_예약하기 (url, jsonStr)
            url = params[0]
            return Okhttp(applicationContext).POST(url,params[1])
        }
        override fun onPostExecute(response: String) {
            if(response.isEmpty()) {
                Log.d("ResLast_Activity", "null")
                return
            }
            Log.d("ResLast_Activity", url)
            Log.d("ResLast_Activity", response)
            if(!Json().isJson(response)){
                Toast.makeText(applicationContext,"네트워크 통신 오류", Toast.LENGTH_SHORT).show()
                return
            }
            val json  = JSONObject(response)
            if(json.getBoolean("success"))
                ResLast_Control().Dialog_res()
            else
                Toast.makeText(applicationContext,"예약 실패", Toast.LENGTH_SHORT).show()
        }
    }

    fun reslast_ClickListener(view : View){
        when(view.id){
            R.id.reslast_cancel_Button -> ResLast_Control().Dialog_cancel()
            R.id.reslast_payment_Button ->{
                //서버에 포스트로 결제정보 보내기
                ResLast_Control().POST_res()
            }
            R.id.reslast_refund_View-> Refund_Dialog(this).create().show()
        }
    }
    override fun onPause() {
        asynctask().cancel(true)
        super.onPause()
    }
}