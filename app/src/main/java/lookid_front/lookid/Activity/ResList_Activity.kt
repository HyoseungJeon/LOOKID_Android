package lookid_front.lookid.Activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_reslist.*
import lookid_front.lookid.Control.Json
import lookid_front.lookid.Control.Okhttp
import lookid_front.lookid.Control.Res_adapter
import lookid_front.lookid.Entity.Reservation_Entity
import lookid_front.lookid.R
import org.json.JSONArray
import org.json.JSONObject

class ResList_Activity : AppCompatActivity() {
    lateinit var res_adapter : Res_adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reslist)
        ResList_Control().init_Activity()
    }

    inner class ResList_Control(){
        fun init_Activity(){
            GET_res_list()
        }
        fun GET_res_list(){
            val url = getString(R.string.server_url) + getString(R.string.selete_res_list)
            asynctask().execute(url)
        }
    }

    inner class asynctask : AsyncTask<String, Void, String>(){
        var state : Int = -1
        var url = ""
        override fun doInBackground(vararg params: String): String {
            url = params[0]
            return Okhttp(applicationContext).GET(url)
        }
        override fun onPostExecute(response: String) {
            //넘어온 값이 없을 때 로그 찍고 리턴
            if (response.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "서버 문제 발생", Toast.LENGTH_SHORT).show()
                Log.d("ResList_Activity", url)
                Log.d("ResList_Activity", "null in")
                return
            }
            Log.d("ResList_Activity", response)
            //response 값이 json문이 아니면 통신 오류 메세지 출력
            if (!Json().isJson(response)) {
                Toast.makeText(applicationContext, "네트워크 통신 오류", Toast.LENGTH_SHORT).show()
                return
            }
            val jsonAry = JSONArray(response)
            val resList: ArrayList<Reservation_Entity> = arrayListOf()
            for (i in 0 until jsonAry.length()) {
                val jsonObj: JSONObject = jsonAry.getJSONObject(i)
                resList.add(Reservation_Entity(jsonObj.getInt("rv_pid"), jsonObj.getString("r_name"), jsonObj.getLong("s_date"),
                        jsonObj.getLong("e_date"), jsonObj.getInt("state")))
            }
            reslist_res_RecView.adapter = Res_adapter(this@ResList_Activity, resList)
        }
    }
    override fun onPause() {
        asynctask().cancel(true)
        super.onPause()
    }
}