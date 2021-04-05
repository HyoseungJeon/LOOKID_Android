package lookid_front.lookid.Activity

import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_findinfo.*
import lookid_front.lookid.Control.Json
import lookid_front.lookid.Control.Okhttp
import lookid_front.lookid.R
import org.json.JSONObject

class FindInfo_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findinfo)

        FindInfo_Control().init()
    }

    inner class FindInfo_Control{
        fun init(){
            findinfo_tab_TabHost.setup()
            findinfo_tab_TabHost.addTab(findinfo_tab_TabHost.newTabSpec("id").setContent(R.id.findinfo_id_tab).setIndicator("아이디"))
            findinfo_tab_TabHost.addTab(findinfo_tab_TabHost.newTabSpec("pw").setContent(R.id.findinfo_pw_tab).setIndicator("비밀번호"))

            findinfo_name_EditText.addTextChangedListener(EditListener(findinfo_findid_Button,findinfo_name_EditText,findinfo_phone_EditText))
            findinfo_phone_EditText.addTextChangedListener(EditListener(findinfo_findid_Button,findinfo_name_EditText,findinfo_phone_EditText))

            findinfo_id_EditText.addTextChangedListener(EditListener(findinfo_findpw_Button,findinfo_id_EditText,findinfo_email_EditText))
            findinfo_email_EditText.addTextChangedListener(EditListener(findinfo_findpw_Button,findinfo_id_EditText,findinfo_email_EditText))
        }

        fun edit_check_id():Boolean{
            if(findinfo_name_EditText.text.isNullOrEmpty() && findinfo_phone_EditText.text.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "이름과 연락처를 입력해주세요", Toast.LENGTH_LONG).show()
                return false
            }
            if (findinfo_name_EditText.text.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "이름을 입력해주세요", Toast.LENGTH_LONG).show()
                return false
            }
            if (findinfo_phone_EditText.text.isNullOrEmpty()){
                Toast.makeText(applicationContext, "연락처를 입력해주세요", Toast.LENGTH_LONG).show()
                return false
            }
            return true
        }

        fun edit_check_pw():Boolean{
            if(findinfo_id_EditText.text.isNullOrEmpty() && findinfo_email_EditText.text.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "아이디와 이메일을 입력해주세요", Toast.LENGTH_LONG).show()
                return false
            }
            if (findinfo_id_EditText.text.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "아이디를 입력해주세요", Toast.LENGTH_LONG).show()
                return false
            }
            if (findinfo_email_EditText.text.isNullOrEmpty()){
                Toast.makeText(applicationContext, "이메일을 입력해주세요", Toast.LENGTH_LONG).show()
                return false
            }
            return true
        }

        fun GET_find_id(name : String, phone : String){
            var url = getString(R.string.server_url) + getString(R.string.find_id)
            url = url.replace("{name}",name,false)
            url = url.replace("{phone}",phone,true)
            //Log.d("Findinfo_Activity",url)
            asynctask().execute("0",url)
        }

        fun GET_find_pw(id : String, email : String){
            var url = getString(R.string.server_url) + getString(R.string.find_pw)
            url = url.replace("{id}",id)
            url = url.replace("{mail}",email)
            asynctask().execute("1",url)
        }

        fun Dialog_findid(id : String){
            val builder = AlertDialog.Builder(this@FindInfo_Activity)
            builder.setMessage("회원님의 아이디는 ${id} 입니다")
            builder.setCancelable(false)
            builder.setPositiveButton("확인") { _, _ -> finish() }
            builder.show()
        }

        fun Dialog_findpw(){
            val builder = AlertDialog.Builder(this@FindInfo_Activity)
            builder.setMessage("회원님의 이메일로 임시 비밀번호가 전송되었습니다")
            builder.setCancelable(false)
            builder.setPositiveButton("확인") { _, _ -> finish() }
            builder.show()
        }
    }

    inner class asynctask : AsyncTask<String, Void, String>(){
        var state : Int = -1 //state == 0 : GET_아이디 찾기, state == 1 : GET_비밀번호 찾기
        var url = ""
        override fun doInBackground(vararg params: String): String {
            state = Integer.parseInt(params[0])
            url = params[1]
            return Okhttp().GET(url)
        }
        override fun onPostExecute(response: String) {
            if(response.isEmpty()){
                Toast.makeText(applicationContext,"서버 오류 발생",Toast.LENGTH_SHORT).show()
                Log.d("FindInfo_Activity","response is null")
                return
            }
            Log.d("FindInfo_Activity",url)
            Log.d("FindInfo_Activity",response)
            if(!Json().isJson(response)){
                Toast.makeText(applicationContext,"네트워크 통신 오류", Toast.LENGTH_SHORT).show()
                return
            }
            val jsonObj = JSONObject(response)
            when (state){
                0->{
                    if(!jsonObj.getString("id").isNullOrEmpty())
                        FindInfo_Control().Dialog_findid(jsonObj.getString("id"))
                    else
                        Toast.makeText(applicationContext,"아이디가 존재하지 않습니다",Toast.LENGTH_SHORT).show()
                }
                1->{
                    if(jsonObj.getBoolean("message"))
                        FindInfo_Control().Dialog_findpw()
                    else
                        Toast.makeText(applicationContext,"아이디가 존재하지 않습니다",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onPause() {
        asynctask().cancel(true)
        super.onPause()
    }
    //Activity 클릭 리스너
    fun findinfo_Click_Listener(view : View){
        when(view.id){
            R.id.findinfo_findid_Button ->{
                if(FindInfo_Control().edit_check_id()){
                    val name = findinfo_name_EditText.text.toString()
                    val phone = findinfo_phone_EditText.text.toString()
                    FindInfo_Control().GET_find_id(name,phone)
                }
            }
            R.id.findinfo_findpw_Button ->{
                if(FindInfo_Control().edit_check_pw()){
                    val id = findinfo_id_EditText.text.toString()
                    val email = findinfo_email_EditText.text.toString()
                    FindInfo_Control().GET_find_pw(id,email)
                }
            }
        }
    }

    inner class EditListener(val button: Button, val editText1 : EditText, val editText2: EditText) : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            button.isEnabled = editText1.text.isNotEmpty() && editText2.text.isNotEmpty()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
}