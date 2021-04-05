package lookid_front.lookid.Control

import android.content.Context
import lookid_front.lookid.Entity.Child
import lookid_front.lookid.R
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class Map(var mapView: MapView?, var childlist: ArrayList<Child>, context: Context?): MapView(context), MapView.CurrentLocationEventListener {
    var myX : Double ?= null
    var myY : Double ?= null

    init {
        mapView = this
        mapView!!.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading)
        this.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading)
        mapView!!.setCurrentLocationEventListener(this)
        this.setCurrentLocationEventListener(this)
    }

    fun set_childlist(childlist: ArrayList<Child>){
        this.childlist = childlist
        check_Child()
        set_Marker()
    }

    fun check_Child() {
        if (childlist != null) {
            if (myX != null && myY != null) {
                for (i in 0..childlist!!.size - 1) {
                    if (distance(myX!!, myY!!, childlist[i].x, childlist[i].y) > 1000) {
                        //미아일때
                        childlist!![i].isMissing = true
                    }
                }
            }
        }
    }

    fun set_Marker(){
        //마커추가하는거임
        mapView?.removeAllPOIItems()
        for (i in 0..childlist.size - 1) {
            val marker = MapPOIItem()
            val mapPoint1_1 = MapPoint.mapPointWithGeoCoord(childlist!![i].x, childlist!![i].y)
            marker.itemName = childlist!![i].c_name
            marker.mapPoint = mapPoint1_1
            marker.markerType = MapPOIItem.MarkerType.CustomImage
            if (childlist!![i].isMissing == true) {
                marker.setCustomImageResourceId(R.drawable.icon_mark_red)
            } else {
                marker.setCustomImageResourceId(R.drawable.icon_mark_yellow)
            }
            marker.setCustomImageAnchor(0.5f, 1.0f)
            mapView?.addPOIItem(marker)
        }
    }

    // lat1 지점 1 위도, lon1 지점 1 경도, lat2 지점 2 위도, lon2 지점 2 경도 // 지점1과 지점2의 위경도값으로 거리계산하는놈 (미터단위)
    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        var theta: Double = lon1 - lon2
        var dist: Double = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))

        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515;
        dist = dist * 1609.344  //미터로 만들어줌 킬로로바꿀라면 * 1.609344 하면됨

        return dist
    }

    fun deg2rad(deg: Double): Double {
        return (deg * Math.PI / 180.0)
    }

    fun rad2deg(rad: Double): Double {
        return (rad * 180 / Math.PI)
    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCurrentLocationUpdate(p0: MapView?, p1: MapPoint?, p2: Float) {
        myX = p1!!.mapPointGeoCoord.latitude
        myY = p1!!.mapPointGeoCoord.longitude
    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}