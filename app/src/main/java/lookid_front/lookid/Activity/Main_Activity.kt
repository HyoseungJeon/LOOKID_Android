package lookid_front.lookid.Activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.app_bar_nav.*
import kotlinx.android.synthetic.main.activity_main_navigation.*
import lookid_front.lookid.Control.Json
import lookid_front.lookid.Control.Okhttp
import lookid_front.lookid.Control.Res_adapter
import lookid_front.lookid.Control.User_Control
import lookid_front.lookid.Dialog.SignOut_Dialog
import lookid_front.lookid.Dialog.Exit_Dialog
import lookid_front.lookid.Entity.Reservation_Entity
import lookid_front.lookid.Entity.User
import lookid_front.lookid.R
import org.json.JSONArray
import org.json.JSONObject

class Main_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    override fun onResume() {
        Main_Control().init_Activity()
        super.onResume()
    }
    inner class Main_Control{
        //액티비티 초기화
        fun init_Activity(){
            GET_checkdate()
            GET_res_list()
            //네비게이션 메뉴 초기화
            val toggle = ActionBarDrawerToggle(
                    this@Main_Activity, main_drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            main_drawer_layout.addDrawerListener(toggle)
            toggle.syncState()
            //회원정보 초기화
            main_id_TextView.text = User_Control(applicationContext).get_user().id
            init_WepView()
        }

        fun init_WepView(){
            main_missing_child_WebView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            main_missing_child_WebView.settings.javaScriptEnabled = true
            main_missing_child_WebView.settings.useWideViewPort = true
            main_missing_child_WebView.settings.setSupportZoom(true)
            main_missing_child_WebView.settings.defaultZoom = WebSettings.ZoomDensity.FAR
            main_missing_child_WebView.settings.loadWithOverviewMode = true
            var url = "${getString(R.string.server_url)}${getString(R.string.main_child_ad)}"
            main_missing_child_WebView.loadUrl(url)
        }
        //사용자가 예약한 날짜가 오늘인지 확인 후 실행 activity 결정
        fun GET_checkdate(){ //0
            val url = getString(R.string.server_url)+getString(R.string.check_date)
            //asynctask().execute(url)
        }
        fun GET_signout() { //1
            SignOut_Dialog(this@Main_Activity, "로그아웃", "정말로 로그아웃 하시겠습니까?").show()
            val url = getString(R.string.server_url) + getString(R.string.sign_out)
            asynctask().execute("1",url)
        }
        fun GET_res_list(){ //2
            val url = getString(R.string.server_url) + getString(R.string.selete_res_list)
            asynctask().execute("2",url)
        }
    }
    inner class asynctask : AsyncTask<String, Void, String>(){
        var state : Int = -1
        var url = ""
        override fun doInBackground(vararg params: String): String {
            state = params[0].toInt()
            url = params[1]
            return Okhttp(applicationContext).GET(url)
        }
        override fun onPostExecute(response: String) {
            //넘어온 값이 없을 때 로그 찍고 리턴
            Log.d("Main_Activity", url)
            if(response.isNullOrEmpty()) {
                Toast.makeText(applicationContext,"서버 문제 발생", Toast.LENGTH_SHORT).show()
                Log.d("Main_Activity", "null in")
                return
            }
            Log.d("Main_Activity",response)
            //response 값이 json문이 아니면 통신 오류 메세지 출력
            if(!Json().isJson(response)){
                Toast.makeText(applicationContext,"네트워크 통신 오류", Toast.LENGTH_SHORT).show()
                return
            }
            when(state){
                0->{ //GET_check_date
                    val jsonObj = JSONObject(response)
                    if(!jsonObj.getInt("rv_pid").toString().isEmpty())
                        startActivity(Intent(applicationContext, Map_Activity::class.java).putExtra("rv_pid",jsonObj.getInt("rv_pid")).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
            }
                2->{
                    val jsonAry = JSONArray(response)
                    val resList : ArrayList<Reservation_Entity> = arrayListOf()
                    for (i in 0 until jsonAry.length()){
                        val jsonObj : JSONObject = jsonAry.getJSONObject(i)
                        resList.add(Reservation_Entity(jsonObj.getInt("rv_pid"),jsonObj.getString("r_name"),jsonObj.getLong("s_date"),
                                jsonObj.getLong("e_date"),jsonObj.getInt("state")))
                    }
                    main_reslist_RecView.adapter = Res_adapter(this@Main_Activity, resList)
                }
            }
        }
    }
    fun main_Click_Listener(view : View){
        when(view.id){
            R.id.main_reservation_View -> startActivity(Intent(applicationContext, Reservation_Activity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
            R.id.main_map_View ->startActivity(Intent(applicationContext, Map_Activity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
            R.id.main_checkRes_View ->startActivity(Intent(applicationContext, ResList_Activity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
            R.id.main_signout_Button ->Main_Control().GET_signout()
            R.id.main_logout_View -> Main_Control().GET_signout()
            R.id.main_userinfo_Button -> startActivity(Intent(applicationContext, UserInfo_Activity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
        }
    }
    override fun onBackPressed() {
        if (main_drawer_layout.isDrawerOpen(GravityCompat.START))
            main_drawer_layout.closeDrawer(GravityCompat.START)
        else
            Exit_Dialog(this).create().show()
    }
    override fun onPause() {
        asynctask().cancel(true)
        super.onPause()
    }
}