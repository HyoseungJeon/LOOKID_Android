package lookid_front.lookid.Activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_userinfo.*
import lookid_front.lookid.Dialog.Loading_Dialog
import lookid_front.lookid.Control.Json
import lookid_front.lookid.Control.Okhttp
import lookid_front.lookid.Control.User_Control
import lookid_front.lookid.Dialog.Address_Dialog
import lookid_front.lookid.Dialog.Bank_Dialog
import lookid_front.lookid.Entity.User
import lookid_front.lookid.R
import org.json.JSONObject

class UserInfo_Activity : AppCompatActivity() {
    var editmode : Boolean = false
    var bank_list = arrayOf("")
    var user_upload : User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)
        UserInfo_Control().user_init()
    }
    inner class UserInfo_Control{
        fun user_init(){
            editmode = false
            val editary = arrayListOf<View>(userinfo_name_EditText, userinfo_phone_EditText, userinfo_email_EditText,
                    userinfo_addressDet_EditText, userinfo_bank_number_EditText, userinfo_bank_holder_EditText, userinfo_findadd_Button)
            val user : User = User_Control(applicationContext).get_user()

            for(i in 0 until editary.size)
                editary[i].isEnabled = false
            userinfo_bank_name_Button.visibility = View.GONE
            userinfo_id_EditText.setText(user.id)
            userinfo_name_EditText.setText(user.name)
            userinfo_phone_EditText.setText(user.phone)
            userinfo_email_EditText.setText(user.email)
            userinfo_address_TextView.text = user.address
            userinfo_addressDet_EditText.setText(user.address_detail)
            userinfo_bank_name_TextView.text = user.bank_name
            userinfo_bank_number_EditText.setText(user.bank_num)
            userinfo_bank_holder_EditText.setText(user.bank_holder)
        }

        fun user_modify(){
            editmode = true
            val editary = arrayListOf<Int>(R.id.userinfo_name_EditText, R.id.userinfo_phone_EditText, R.id.userinfo_email_EditText,
                    R.id.userinfo_addressDet_EditText, R.id.userinfo_bank_number_EditText, R.id.userinfo_bank_holder_EditText)
            for(i in 0 until editary.size){
                val editText = findViewById<EditText>(editary[i])
                editText.isEnabled = true
            }
            userinfo_bank_name_Button.visibility = View.VISIBLE
            userinfo_findadd_Button.isEnabled = true
        }
        fun user_modfiy_go(){
            if(edit_check()){
                //0. User init
                val user : User = User_Control(applicationContext).get_user()
                user.name = userinfo_name_EditText.text.toString()
                user.phone = userinfo_phone_EditText.text.toString()
                user.email = userinfo_email_EditText.text.toString()
                user.address = userinfo_address_TextView.text.toString()
                user.address_detail = userinfo_addressDet_EditText.text.toString()
                user.bank_name = userinfo_bank_name_TextView.text.toString()
                user.bank_num = userinfo_bank_number_EditText.text.toString()
                user.bank_holder = userinfo_bank_holder_EditText.text.toString()
                //1. User_Control update
                User_Control(applicationContext).set_user(user)
                userinfo_modify_Button.background = getDrawable(R.drawable.icon_modify)
                //2. server update
                PUT_user_modify(user)
                user_init()
            }
        }

        fun edit_check() : Boolean{
            val editary = arrayListOf<EditText>(userinfo_name_EditText, userinfo_phone_EditText, userinfo_email_EditText)
            for(i in 0 until editary.size){
                if(editary[i].text.isNullOrBlank()){
                    Toast.makeText(applicationContext,"필수 정보를 입력해주세요",Toast.LENGTH_SHORT).show()
                    return false
                }
            }
            return true
        }

        fun Dialog_pw_change(){
            val builder = AlertDialog.Builder(this@UserInfo_Activity)
            builder.setTitle("비밀번호 변경")
            val view = layoutInflater.inflate(R.layout.dialog_userinfo_pwchange,null)
            builder.setView(view)
            builder.setPositiveButton("확인",null)
            builder.setNegativeButton("취소",null)
            val dialog = builder.create()
            dialog.setOnShowListener(Dialog_Listener())
            dialog.show()
        }

        fun Dialog_make_sure(){
            val builder = AlertDialog.Builder(this@UserInfo_Activity,R.style.DialogStyle)
            builder.setMessage("정말로 정보를 수정하시겠습니까?")
            builder.setPositiveButton("변경") { dialog, which ->
                UserInfo_Control().user_modfiy_go()
            }
            builder.setNegativeButton("취소",null)
            builder.show()
        }

        fun Dialog_bankname(){
            val bank_Dialog = Bank_Dialog(this@UserInfo_Activity,userinfo_bank_name_TextView.text.toString(),userinfo_bank_name_TextView)
            bank_Dialog.show()
        }

        fun Dialog_search_Address(){
            val address_Dialog = Address_Dialog(this@UserInfo_Activity, userinfo_address_TextView)
            address_Dialog.show()
        }

        fun PUT_user_modify(user : User){
            val url = getString(R.string.server_url) + getString(R.string.user_modify)
            user_upload = user
            asynctask().execute("0",url)
        }

        fun PUT_pw_modify(pw : String){
            val url = getString(R.string.server_url) + getString(R.string.user_modify_pw)
            asynctask().execute("1",url,pw)
        }
    }

    inner class asynctask : AsyncTask<String, Void, String>(){
        var state : Int = -1 //state == 0 : PUT_사용자 정보 변경, state == 1 : POST_사용자 비밀번호 변경
        var loadingDialog = Loading_Dialog(this@UserInfo_Activity)
        override fun onPreExecute() {
            loadingDialog.show()
        }
        override fun doInBackground(vararg params: String): String {
            state = Integer.parseInt(params[0])
            val url = params[1]
            var response : String = ""
            when (state){
                0->response = Okhttp(applicationContext).PUT(url, Json().modify_user(user_upload))
                1-> {
                    val pw = params[2]
                    response = Okhttp(applicationContext).PUT(url, Json().modify_pw(pw))
                }
            }
            return response
        }
        override fun onPostExecute(response: String) {
            if(response.isEmpty()) {
                Log.d("UserInfo_Activity", "null")
                loadingDialog.dismiss()
                return
            }
            Log.d("UserInfo_Activity",response)
            if(!Json().isJson(response)){
                Toast.makeText(applicationContext,"네트워크 통신 오류", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
                return
            }
            val jsonObj = JSONObject(response)
            when (state){
                0->{
                    if(jsonObj.getBoolean("success")) {
                        Toast.makeText(applicationContext, "정보가 변경되었습니다", Toast.LENGTH_LONG).show()
                        UserInfo_Control().user_init()
                    }
                    else Toast.makeText(applicationContext,"사용자 정보 변경 실패",Toast.LENGTH_LONG).show()
                }
                1->{
                    if(jsonObj.getBoolean("success")) Toast.makeText(applicationContext, "정보가 변경되었습니다", Toast.LENGTH_LONG).show()
                    else Toast.makeText(applicationContext,"사용자 정보 변경 실패",Toast.LENGTH_LONG).show()
                }
            }
            loadingDialog.dismiss()
        }
    }
    //Activity 클릭 리스너
    fun userinfo_Click_Listener(view : View){
        when (view.id){
            R.id.userinfo_modify_Button ->{
                if(!editmode) {
                    UserInfo_Control().user_modify()
                    userinfo_modify_Button.background = getDrawable(R.drawable.icon_save)
                }
                else UserInfo_Control().Dialog_make_sure()
            }
            R.id.userinfo_pwchange_Button ->{ UserInfo_Control().Dialog_pw_change() }
            R.id.userinfo_bank_name_Button -> { UserInfo_Control().Dialog_bankname()}
            R.id.userinfo_findadd_Button ->{ UserInfo_Control().Dialog_search_Address()}
        }
    }
    inner class Dialog_Listener : DialogInterface.OnShowListener{
        override fun onShow(dialog: DialogInterface?) {
            val alert = dialog as AlertDialog
            val positiveButton : Button = alert.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val pw1 = alert.findViewById<EditText>(R.id.userinfo_d_pc1_EditText).text.toString()
                val pw2 = alert.findViewById<EditText>(R.id.userinfo_d_pc2_EditText).text.toString()
                if(checkpw(pw1,pw2)){
                    UserInfo_Control().PUT_pw_modify(pw1)
                    alert.dismiss()
                }
            }
        }
        fun checkpw(pw1 : String, pw2 : String) : Boolean{
            if(pw1.isEmpty() || pw2.isEmpty()) {
                Toast.makeText(applicationContext,"비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show()
                return false
            }
            if(!pw1.equals(pw2)){
                Toast.makeText(applicationContext,"1차 2차 비밀번호가 다릅니다",Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }
    }
    override fun onPause() {
        asynctask().cancel(true)
        super.onPause()
    }
}