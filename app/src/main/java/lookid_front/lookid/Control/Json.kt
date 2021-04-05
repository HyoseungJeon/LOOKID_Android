package lookid_front.lookid.Control

import android.util.Log
import com.google.gson.Gson
import lookid_front.lookid.Entity.*
import org.json.JSONArray
import org.json.JSONObject

class Json(){
    var jsonObject = JSONObject()

    fun login(id : String, pw : String) : String{
        jsonObject.put("id",id)
        jsonObject.put("pw",pw)
        return jsonObject.toString()
    }

    fun signup(user : User?, pw : String) : String{
        jsonObject.put("id", user!!.id)
        jsonObject.put("pw",pw)
        jsonObject.put("name", user!!.name)
        jsonObject.put("phone", user!!.phone)
        jsonObject.put("mail", user!!.email)
        jsonObject.put("address", user!!.address)
        jsonObject.put("bank_name", user!!.bank_name)
        jsonObject.put("bank_num", user!!.bank_num)
        jsonObject.put("bank_holder", user!!.bank_holder)

        return jsonObject.toString()
    }

    fun modify_user(user: User?) :String{
        jsonObject.put("name", user!!.name)
        jsonObject.put("phone", user!!.phone)
        jsonObject.put("mail", user!!.email)
        jsonObject.put("address", user!!.address)
        jsonObject.put("bank_name", user!!.bank_name)
        jsonObject.put("bank_num", user!!.bank_num)
        jsonObject.put("bank_holder", user!!.bank_holder)

        return jsonObject.toString()
    }

    fun modify_pw(pw : String) : String{
        jsonObject.put("pw",pw)
        return jsonObject.toString()
    }

    fun reservation(reservation_Entity: Reservation_Entity) : String{
        jsonObject = JSONObject(Gson().toJson(reservation_Entity))
        var str = "{\"reservation\":${Gson().toJson(reservation_Entity)}}"
        jsonObject = JSONObject(str)
        var userjson = jsonObject.getJSONObject("reservation").getJSONObject("user")

        jsonObject.getJSONObject("reservation").put("name",userjson.getString("name"))
        jsonObject.getJSONObject("reservation").put("address",userjson.getString("address"))
        jsonObject.getJSONObject("reservation").put("address_detail",userjson.getString("address_detail"))
//        jsonObject.put("address_detail",userjson.getString("address_detail"))
        jsonObject.getJSONObject("reservation").put("bank_holder",userjson.getString("bank_holder"))
        jsonObject.getJSONObject("reservation").put("bank_name",userjson.getString("bank_name"))
        jsonObject.getJSONObject("reservation").put("bank_num",userjson.getString("bank_num"))
        jsonObject.getJSONObject("reservation").put("phone",userjson.getString("phone"))
        jsonObject.getJSONObject("reservation").remove("user")
        val group_list : JSONArray = jsonObject.getJSONObject("reservation").getJSONArray("group_list")
        for(i in 0 until group_list.length()){
            val group_json = JSONObject()
            group_json.put("g_pid",group_list.getJSONObject(i).getString("g_pid"))
            group_json.put("g_name",group_list.getJSONObject(i).getString("name"))
            jsonObject.getJSONObject("reservation").getJSONArray("group_list").getJSONObject(i).put("group",group_json)
            jsonObject.getJSONObject("reservation").getJSONArray("group_list").getJSONObject(i).remove("g_pid")
            jsonObject.getJSONObject("reservation").getJSONArray("group_list").getJSONObject(i).remove("name")
        }
        jsonObject.put("group_list",jsonObject.getJSONObject("reservation").getJSONArray("group_list"))
        jsonObject.getJSONObject("reservation").remove("group_list")
        str = jsonObject.toString()
        str = str.replace("admin_list","admin")
        str = str.replace("child_list","child")

        return str
    }

    fun res_delete(rv_pid : Int) : String{
        jsonObject.put("rv_pid",rv_pid)
        return jsonObject.toString()
    }

    fun to_Res(str : String) : Reservation_Entity{
        val group_list : ArrayList<Group> = arrayListOf()
        val json = JSONObject(str)
        val jsonary_group = json.getJSONArray("group_list")
        for(i in 0 until jsonary_group.length()){
            val json_group = jsonary_group.getJSONObject(i)
            val admin_list : ArrayList<Admin> = arrayListOf()
            val child_list : ArrayList<Child> = arrayListOf()
            val jsonary_admin = json_group.getJSONArray("admin")
            val jsonary_child = json_group.getJSONArray("child")
            for(j in 0 until jsonary_admin.length()){
                val json_admin = jsonary_admin.getJSONObject(j)
                admin_list.add(Admin(json_admin.getInt("user_pid"),json_admin.getString("id"),json_admin.getString("name")))
            }
            for(j in 0 until jsonary_child.length()){
                val json_child = jsonary_child.getJSONObject(j)
                child_list.add(Child(json_child.getString("c_name")))
            }
            val json_groupinfo = json_group.getJSONObject("group")
            group_list.add(Group(json_groupinfo.getInt("g_pid"),child_list,admin_list,json_groupinfo.getString("g_name")))
        }
        val json_res = json.getJSONObject("reservation")
        Log.d("JSON",json_res.toString())
        return Reservation_Entity(json_res.getInt("rv_pid"),json_res.getString("r_name"),json_res.getLong("r_date"),json_res.getLong("s_date"),
                json_res.getLong("e_date"),json_res.getInt("receipt_item"),json_res.getInt("return_item"),json_res.getInt("cost"),
                json_res.getInt("deposit"),json_res.getString("wb_num"),json_res.getInt("state"),group_list,
                User("id",json_res.getString("name"),json_res.getString("phone"),"email",json_res.getString("address"),json_res.getString("bank_name"),
                        json_res.getString("bank_num"),json_res.getString("bank_holder")))
    }

    fun isJson(str : String):Boolean{
        str.trim()
        if(str[0] == '{' || str[0] == '[')
            return true
        return false
    }
}