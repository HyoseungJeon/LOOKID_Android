package lookid_front.lookid.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.activity_map_content.*
import kotlinx.android.synthetic.main.activity_map_navigation.*
import lookid_front.lookid.Control.*
import lookid_front.lookid.Control.Map
import lookid_front.lookid.Dialog.Map_Dialog
import lookid_front.lookid.Entity.Admin
import lookid_front.lookid.Entity.Child
import lookid_front.lookid.Entity.Group

import net.daum.mf.map.api.*
import net.daum.mf.map.api.MapPoint.mapPointWithGeoCoord
import lookid_front.lookid.R
import lookid_front.lookid.R.id.group_RecView
import org.json.JSONArray
import org.json.JSONObject


class Map_Activity : AppCompatActivity() {
    lateinit var unmissingAdapter: Unmissing_adapter
    lateinit var missingAdapter: Missing_adapter
    lateinit var group_adapter: Group_map_adapter
    val alarmMap: HashMap<Int, Boolean> = hashMapOf()
    var rv_pid: Int = -1

    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSIONS_REQUEST_CODE = 100
    internal var REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    var mMapView: Map? = null
    val trackingImageAnchorPointOffset = MapPOIItem.ImageOffset(31, 0) // 이미지 상의 좌표조정값
    var g_pid: Int = 0

    val handler = Handler()
    var handlerTask : Runnable ?= null

    var unmissing_childlist = arrayListOf<Child>()
    var missing_childlist = arrayListOf<Child>()

    //전체 GROUPLIST 전역변수
    var grouplist = arrayListOf<Group>()
    //전체 CHILDLIST 전역변수
    var childlist = arrayListOf<Child>()

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) // 맵이 사용되는 동안 화면 끄지 않기.
        mapControl().init()
        Log.d("Map_Activity", "oncreate")
    }

    inner class mapControl {
        fun init() {
            GET_Map_init(rv_pid) //******************************
            grouplist.add(Group(0, arrayListOf<Child>(), arrayListOf<Admin>(), "햇님반"))
            grouplist.add(Group(1, arrayListOf<Child>(), arrayListOf<Admin>(), "별님반"))

            group_adapter = Group_map_adapter(this@Map_Activity, grouplist) { group -> g_pid = group.g_pid; Toast.makeText(this@Map_Activity, group.name + " 입니다", Toast.LENGTH_SHORT).show() }
            group_RecView.adapter = group_adapter
            group_RecView.layoutManager = LinearLayoutManager(applicationContext, LinearLayout.HORIZONTAL, false)

            mapView_init()  // 가공 작업
            mapView_start()

            user_location.setOnCheckedChangeListener { buttonView, isChecked ->
                if (user_location.isChecked) {
                    user_location.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_mylocation))
                    mMapView?.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading) // 현위치가 중앙으로 이동(이후 지도 중앙으로가는건 강제임...)
                    mMapView?.setCustomCurrentLocationMarkerTrackingImage(R.drawable.icon_mark_blue, trackingImageAnchorPointOffset)  //커스텀된 마커 표시
                } else {
                    user_location.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_mylocation_gray))
                    mMapView?.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff) // 본인 강제 중앙 없애기
                    mMapView?.setShowCurrentLocationMarker(true) //TrackingModeOff를 해두 현위치 마커 표시하기
                    mMapView?.setCustomCurrentLocationMarkerTrackingImage(R.drawable.icon_mark_blue, trackingImageAnchorPointOffset)  //커스텀된 마커 표시 -> 안되누
                }
            }

            user_alarm.setOnCheckedChangeListener { buttonView, isChecked ->
                if (user_alarm.isChecked)
                    user_alarm.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_alarm))
                else
                    user_alarm.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_alarm_no))
            }
            mapinfo.setOnClickListener { Map_Dialog(this@Map_Activity).create().show() }
        }

        fun mapView_init() {
            mMapView = Map(mMapView, childlist, this@Map_Activity)
            val mapViewContainer = map_view
            mapViewContainer.addView(mMapView)
            gpsCheck()
        }

        fun mapView_start() {
            handlerTask = object : Runnable {
                override fun run() {
                    Log.i("innerclass set_up()", "asdf" + getGpid())
                    Log.d("alarm_list",alarmMap.toString())

                    //GET_Map_Loc(g_pid)  //*********************************

                    if(g_pid == 0) {
                        childlist.clear()
                        childlist.add(Child(0, "전효승", 37.570035, 126.983887, false))
                        childlist.add(Child(1, "박지상", 37.602655, 126.955193, false))
                    }
                    if(g_pid == 1){
                        childlist.clear()
                        childlist.add(Child(0, "양유림", 37.602315, 126.954705, false))
                        childlist.add(Child(1, "김동민", 37.601643, 126.955606, false))
                    }
                    mMapView!!.set_childlist(childlist)
                    childlist_setup()
                    check_alarm()
                    rec()
                    handler.postDelayed(this,5000)
                }
            }
            handler.post(handlerTask)
        }

        fun childlist_setup() {
            val unmissing_childlist = arrayListOf<Child>()
            val missing_childlist = arrayListOf<Child>()
            for (i in childlist) {
                if (i.isMissing) {
                    missing_childlist.add(i)
                    if (alarmMap[i.c_pid] == null)
                        alarmMap[i.c_pid] = false
                } else {
                    unmissing_childlist.add(i)
                    alarmMap.remove(i.c_pid)
                }
            }
            this@Map_Activity.missing_childlist = missing_childlist
            this@Map_Activity.unmissing_childlist = unmissing_childlist
            if (missing_childlist.size == 0) {
                textView51.visibility = View.GONE
            }
            else{
                textView51.visibility = View.VISIBLE
            }
        }

        fun gpsCheck() {
            if (!checkLocationServicesStatus())
                showDialogForLocationServiceSetting()
            else
                checkRunTimePermission()
        }

        fun check_alarm() {
            for (i in alarmMap) {
                if (user_alarm.isChecked == true && !i.value)
                    Alert()
            }
        }

        fun rec() {
            val toggle = ActionBarDrawerToggle(
                    this@Map_Activity, map_drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            map_drawer_layout.addDrawerListener(toggle)
            toggle.syncState()
            unmissingAdapter = Unmissing_adapter(this@Map_Activity, unmissing_childlist) { child -> mMapView?.moveCamera(CameraUpdateFactory.newMapPoint(mapPointWithGeoCoord(child.x, child.y))); user_location.isChecked = false; map_drawer_layout.closeDrawer(GravityCompat.START) }
            missingAdapter = Missing_adapter(this@Map_Activity, missing_childlist) { child -> mMapView?.moveCamera(CameraUpdateFactory.newMapPoint(mapPointWithGeoCoord(child.x, child.y))); user_location.isChecked = false; map_drawer_layout.closeDrawer(GravityCompat.START) }
            map_reslist_RecView.adapter = unmissingAdapter
            map_missing_reslist_RecView.adapter = missingAdapter
        }

        fun Alert() {
            createNotificationChannel(applicationContext, NotificationManagerCompat.IMPORTANCE_MAX,
                    false, getString(R.string.app_name), "App notification channel")

            val channelId = "$packageName-${getString(R.string.app_name)}" // 알람뜰때 앱이름뜨게
            val intent = Intent(baseContext, this@Map_Activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_NEW_TASK //알람 인탠드
            val pendingIntent = PendingIntent.getActivity(baseContext, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT)    //

            val builder = NotificationCompat.Builder(this@Map_Activity, channelId)  //
            builder.setSmallIcon(R.drawable.ic_recent_copy)    // 알람 왼쪽 아이콘부분
            builder.setContentTitle("제목부분 미아생김")    //
            builder.setContentText("내용부분 미드미아")    //
            builder.priority = NotificationCompat.PRIORITY_MAX   //  우선순윈데 걍 맥스때림
            builder.setDefaults(Notification.DEFAULT_VIBRATE) // 진동울리게
            builder.setAutoCancel(true)   // 알람 누르면 사라지게
            builder.setFullScreenIntent(pendingIntent, true) //왜 풀스크린 안뜨냐


            val notificationManager = NotificationManagerCompat.from(applicationContext)
            notificationManager.notify(0, builder.build())    // 11
        }

        fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean,
                                      name: String, description: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "${context.packageName}-$name"
                val channel = NotificationChannel(channelId, name, importance)
                channel.description = description
                channel.setShowBadge(showBadge)

                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun GET_Map_init(rv_pid: Int) {
            val url: String = ""
            //asynctask().execute("0",url)
        }

        fun GET_Map_Loc(g_pid: Int) {
            val url: String = ""
            //asynctask().execute("1",url)
        }
    }

    fun getGpid(): Int {
        return g_pid
    }

    @SuppressLint("StaticFieldLeak")
    inner class asynctask : AsyncTask<String, Void, String>() {
        var state: Int = 0 // 0 : get_map , 1 : get_map_loc
        override fun doInBackground(vararg params: String): String {
            //POST_예약하기 (url, jsonStr)
            state = params[0].toInt()
            val url = params[1]
            return Okhttp(applicationContext).GET(url)
        }

        override fun onPostExecute(response: String) {
            if (response.isEmpty()) {
                Log.d("Map_Activity", "null")
                return
            }
            if (!Json().isJson(response)) {
                Toast.makeText(applicationContext, "네트워크 통신 오류", Toast.LENGTH_SHORT).show()
                Log.d("Map_Activity", response)
                return
            }
            val jsonArray = JSONArray(response)
            when (state) {
                0 -> {
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                        grouplist.add(Group(jsonObject.getInt("g_pid"), arrayListOf<Child>(),
                                arrayListOf<Admin>(), jsonObject.getString("g_name")))
                    }
                }
                1 -> {
                    if (!childlist[0].c_name.equals(jsonArray.getJSONObject(0).getString("c_name"))) {
                        childlist.clear()
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            childlist.add(Child(i, jsonObject.getString("c_name"),
                                    jsonObject.getDouble("loc_x"), jsonObject.getDouble("loc_y"), false))
                        }
                    } else {
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                            childlist[i] = Child(i, jsonObject.getString("c_name"),
                                    jsonObject.getDouble("loc_x"), jsonObject.getDouble("loc_y"), false)
                        }
                    }
                }
            }
        }
    }

    //gps켜져있는지 확인하는 메소드 -> gps가 켜져있어야 현재위치 마커가능, 현위치 위경도 좌표값 가져올수있음(거리계산 함수에 써야함)
    override fun onRequestPermissionsResult(permsRequestCode: Int, permissions: Array<String>, grandResults: IntArray) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            var check_result = true

            // 모든 퍼미션을 허용했는지 체크합니다.

            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {
                Log.d("@@@", "start")
                //위치 값을 가져올 수 있음
                mMapView?.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
                mMapView?.setCustomCurrentLocationMarkerTrackingImage(R.drawable.icon_mark_blue, trackingImageAnchorPointOffset)

            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다. 2가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(this@Map_Activity, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@Map_Activity, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    internal fun checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this@Map_Activity, Manifest.permission.ACCESS_FINE_LOCATION)
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음
            mMapView?.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
            mMapView?.setCustomCurrentLocationMarkerTrackingImage(R.drawable.icon_mark_blue, trackingImageAnchorPointOffset)
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@Map_Activity, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(this@Map_Activity, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                        this@Map_Activity, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE)
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                        this@Map_Activity, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    //여기부터는 GPS 활성화 하기위한  메소드들
    private fun showDialogForLocationServiceSetting() {
        val builder = AlertDialog.Builder(this@Map_Activity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 하시겠습니까?")
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton(
                "취소"
        ) { dialog, id -> dialog.cancel() }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음")
                        checkRunTimePermission()
                        return
                    }
                }
        }
    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onResume() {
        for (i in alarmMap)
            i.setValue(true)
        super.onResume()
    }

    override fun onDestroy() {
        Log.d("Map_activity", "onDestroy")
        handler.removeCallbacks(handlerTask)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (map_drawer_layout.isDrawerOpen(GravityCompat.START)) {
            map_drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}