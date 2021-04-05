package lookid_front.lookid.Control

import android.content.Context
import android.content.Context.MODE_PRIVATE
import lookid_front.lookid.Entity.User

class User_Control(context: Context){
    val sharedPreferences  = context.getSharedPreferences("User_Info", MODE_PRIVATE)
    val editPreferences = sharedPreferences.edit()

    fun set_token(token : String) {editPreferences.putString("token",token).apply()}
    fun set_auto_login(auto_login : Boolean) {editPreferences.putBoolean("auto_login",auto_login).apply()}
    fun set_user(user : User) {
        editPreferences.putString("id",user.id)
        editPreferences.putString("name",user.name)
        editPreferences.putString("phone",user.phone)
        editPreferences.putString("email",user.email)
        editPreferences.putString("address",user.address)
        editPreferences.putString("bank_name",user.bank_name)
        editPreferences.putString("bank_number",user.bank_num)
        editPreferences.putString("bank_holder",user.bank_holder)
        editPreferences.apply()
    }

    fun get_token() : String? { return sharedPreferences.getString("token",null)}
    fun get_auto_login() : Boolean { return sharedPreferences.getBoolean("auto_login",false)}
    fun get_user() : User{
        var user = User(
                sharedPreferences.getString("id",""), sharedPreferences.getString("name", ""), sharedPreferences.getString("phone", "")
                , sharedPreferences.getString("email", ""), sharedPreferences.getString("address", ""),
                sharedPreferences.getString("bank_name", "")
                , sharedPreferences.getString("bank_number", ""), sharedPreferences.getString("bank_holder", "")
        )
        return user
    }
}