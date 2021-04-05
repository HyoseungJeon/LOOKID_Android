package lookid_front.lookid.Activity

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_reservation.*
import lookid_front.lookid.Control.*
import lookid_front.lookid.Dialog.Address_Dialog
import lookid_front.lookid.Dialog.Bank_Dialog
import lookid_front.lookid.Dialog.Basic_Dialog
import lookid_front.lookid.Entity.Group
import lookid_front.lookid.Entity.Reservation_Entity
import lookid_front.lookid.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class Reservation_Activity : AppCompatActivity() {
    var Res_Activity : Reservation_Activity? = null
    var devicenum : Int = 0;
    var bank_list = arrayOf("")
    var Reservation_Entity = Reservation_Entity()
    var group_list = arrayListOf<Group>(Group())
    lateinit var group_Adapter : Group_adapter
    var calendar : Calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat(Date_Control().dateFormat, Locale.KOREA)
    var useDay : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)
        Reservation_Control().reservation_init()
    }

    //예약 액티비티 컨트롤 클래스
    inner class Reservation_Control{
        //사용자 정보 초기화
        fun user_init(state : Boolean){
            when(state){
                true->{//회원 정보 넣기
                    val user = User_Control(applicationContext).get_user()
                    res_name_EditText.setText(user.name)
                    res_phone_EditText.setText(user.phone)
                    res_bank_number_EditText.setText(user.bank_num)
                    res_bank_holder_EditText.setText(user.bank_holder)
                    userinfo_bank_name_TextView.text = user.bank_name
                }
                false->{//초기화
                    res_name_EditText.text = null
                    res_phone_EditText.text = null
                    res_bank_number_EditText.text = null
                    res_bank_holder_EditText.text = null
                    userinfo_bank_name_TextView.text = null
                }
            }
        }

        //금액 초기화
        fun pay_init(){
            val startdate = dateFormat.parse(res_startdate_TextView.text.toString())
            val enddate = dateFormat.parse(res_enddate_TextView.text.toString())
            useDay = (enddate.time - startdate.time) / (24*60*60*1000)

            if(devicenum == 0 || useDay.toInt() == 0)
                return

            val payformat = DecimalFormat("###,###")
            var res_pay : Int = 0
            var res_deposit : Int = 0
            var res_postpay : Int = 0

            if(useDay > 0 && devicenum > 0){
                res_pay = (useDay * devicenum * 1500).toInt()
                res_pay_TextView.text = payformat.format(res_pay)
                res_deposit = devicenum * 1000
                res_deposit_TextView.text = payformat.format(res_deposit)
                Reservation_Entity.deposit = res_deposit
                if((useDay * devicenum * 1500) < 50000){
                    res_postpay = 5000
                    res_postpay_TextView.text = payformat.format(res_postpay)
                }
                res_totalpay_TextView.text = payformat.format(res_pay + res_deposit + res_postpay)
                Reservation_Entity.cost = res_pay + res_deposit + res_postpay
            }
        }

        //예약페이지 초기화
        fun reservation_init(){
            Res_Activity = this@Reservation_Activity
            res_ScrollView.setOnScrollChangeListener { nestedScrollView: NestedScrollView?, ox: Int, oy: Int, nx: Int, ny: Int ->
                if(ny > res_userinfo_View.bottom && res_userinfo_View.visibility == View.VISIBLE) {
                    res_userinfo_View.visibility = View.GONE
                    res_user_visControl_CheckBox.isChecked = false
                    res_ScrollView.scrollY = res_userinfo_View.top
                }
            }
            //예약정보 초기화
            var user = User_Control(applicationContext).get_user()
            Reservation_Entity!!.user!!.id = user.id
            Reservation_Entity.receipt_item = 0
            Reservation_Entity.return_item = 0
            //스피너 초기화

            //날짜 텍스트뷰 현재 날짜로 초기화
            res_startdate_TextView.text = dateFormat.format(Date())
            res_enddate_TextView.text = dateFormat.format(Date())

            //그룹 리사이클러뷰 초기화
            group_Adapter = Group_adapter(this@Reservation_Activity,group_list)
            res_grouplist_RecView.adapter = group_Adapter
            res_grouplist_RecView.setItemViewCacheSize(100)
        }

        //날짜 선택 다이얼로그 띄움 (state = 0 : 시작일, state = 1 : 종료일)
        fun Dialog_DatePicker(state : Int){
            val listener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                Log.d("Res_Acitivity",Date_Control().toDateformat(year,month,dayOfMonth))
                var newmonth = month + 1
                when(state){
                    0-> {
                        res_startdate_TextView.text = Date_Control().toDateformat(year,newmonth,dayOfMonth)
                        val startdate = dateFormat.parse(Date_Control().toDateformat(year,newmonth,dayOfMonth))
                        val enddate = dateFormat.parse(res_enddate_TextView.text.toString())
                        if(startdate.after(enddate))
                            res_enddate_TextView.text = Date_Control().toDateformat(year,newmonth,dayOfMonth)
                        }
                    1-> {
                        val startdate = dateFormat.parse(res_startdate_TextView.text.toString())
                        val enddate = dateFormat.parse(Date_Control().toDateformat(year,newmonth,dayOfMonth))
                        if(enddate.before(startdate))
                            Toast.makeText(applicationContext,"종료일이 시작일 이전 입니다",Toast.LENGTH_SHORT).show()
                        else
                            res_enddate_TextView.text = Date_Control().toDateformat(year,newmonth,dayOfMonth)
                    }
                }
                Reservation_Control().pay_init()
            }

            val cal = Calendar.getInstance()
            var date : Date = dateFormat.parse(res_startdate_TextView.text.toString())
            if(state == 1)
                date = dateFormat.parse(res_enddate_TextView.text.toString())
            cal.time = date
            val dateDialog = DatePickerDialog(this@Reservation_Activity,R.style.DialogStyle,listener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),
                    cal.get(Calendar.DATE))

            dateDialog.show()
        }

        fun Dialog_Pay_Info(){

        }

        //주소 검색 다이얼로그
        fun Dialog_search_Address(){
            val address_Dialog = Address_Dialog(this@Reservation_Activity, res_address_EditText)
            address_Dialog.show()
        }

        fun Dialog_bankname(){
            val bank_Dialog = Bank_Dialog(this@Reservation_Activity,userinfo_bank_name_TextView.text.toString(),userinfo_bank_name_TextView)
            bank_Dialog.show()
        }

        fun Dialog_cancel(){
            Basic_Dialog(this@Reservation_Activity,"취소","정말로 뒤로 가시겠습니까?\n작성하신 내용이 초기화 됩니다.",
                    DialogInterface.OnClickListener { _, _ ->
                finish()
            },true).show()
        }

        //결제 정보 초기화 함수
        fun res_init():Boolean{
            Reservation_Entity.r_name = res_resname_EditText.text.toString()
            Reservation_Entity.user.name = res_name_EditText.text.toString()
            Reservation_Entity.user.phone = res_phone_EditText.text.toString()
            Reservation_Entity.user.bank_num = res_bank_number_EditText.text.toString()
            Reservation_Entity.user.bank_holder = res_bank_holder_EditText.text.toString()
            Reservation_Entity.user.bank_name = userinfo_bank_name_TextView.text.toString()
            Reservation_Entity.r_date = Date().time
            Reservation_Entity.s_date = dateFormat.parse(res_startdate_TextView.text.toString()).time
            Reservation_Entity.e_date = dateFormat.parse(res_enddate_TextView.text.toString()).time
            Reservation_Entity.user.address = res_address_EditText.text.toString()
            Reservation_Entity.user.address_detail = res_addressDet_EditText.text.toString()
            Reservation_Entity.state = 1
            Reservation_Entity.group_list = group_Adapter.grouplist

            return Reservation_Entity.null_res()
        }
    }

    //Activity 클릭 리스너
    fun res_Click_Listener(view : View){
        when(view.id){
            R.id.res_rec_del_RadioButton ->{Reservation_Entity.receipt_item = 0}
            R.id.res_rec_vis_RadioButton ->{Reservation_Entity.receipt_item = 1}
            R.id.res_ret_del_RadioButton ->{Reservation_Entity.return_item = 0}
            R.id.res_ret_vis_RadioButton ->{Reservation_Entity.return_item = 1}
            R.id.res_user_visControl_CheckBox ->{
                if(res_user_visControl_CheckBox.isChecked)
                    res_userinfo_View.visibility = View.VISIBLE
                else
                    res_userinfo_View.visibility = View.GONE
            }

            R.id.res_child_visControl_CheckBox ->{
                if(res_child_visControl_CheckBox.isChecked)
                    res_child_View.visibility = View.VISIBLE
                else
                    res_child_View.visibility = View.GONE
            }
            R.id.res_startdate_Button ->Reservation_Control().Dialog_DatePicker(0)
            R.id.res_enddate_Button ->Reservation_Control().Dialog_DatePicker(1)
            R.id.res_auto_CheckBox ->Reservation_Control().user_init(res_auto_CheckBox.isChecked)
            R.id.res_payinfo_Button ->Reservation_Control().Dialog_Pay_Info()
            R.id.res_groupadd_Button ->{
                group_Adapter.add()
                res_grouplist_RecView.scrollToPosition(group_Adapter.itemCount - 1)
            }
            R.id.res_findadd_Button -> Reservation_Control().Dialog_search_Address()
            R.id.res_resvation_Button ->{
                Reservation_Control().pay_init()
                if(Reservation_Control().res_init()) {
                    Log.d("Res_Acitivity",Json().reservation(Reservation_Entity))
                    Toast.makeText(applicationContext, "예약 정보를 모두 입력해주세요", Toast.LENGTH_LONG).show()
                    return
                }
                Log.d("Res_Acitivity",Json().reservation(Reservation_Entity))
                val intent = Intent(applicationContext, ReservationLast_Activity::class.java)
                intent.putExtra("res", Reservation_Entity)
                intent.putExtra("res_devicenum" , group_Adapter.getDevice_num())
                intent.putExtra("res_useday" , useDay)
                startActivity(intent)
            }
            R.id.res_bank_name_Button->Reservation_Control().Dialog_bankname()
            R.id.res_startdate_TextView ->Reservation_Control().Dialog_DatePicker(0)
            R.id.res_enddate_TextView ->Reservation_Control().Dialog_DatePicker(1)
        }
    }
    override fun onBackPressed() {
        Reservation_Control().Dialog_cancel()
    }
}