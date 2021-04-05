package lookid_front.lookid.Control

import android.content.Context
import android.util.Log
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class Okhttp() {
    var context : Context? = null
    val client : OkHttpClient = OkHttpClient()
    lateinit var response: Response
    var token : String? = null
    init {
        client.newBuilder()
                .connectTimeout(5,TimeUnit.SECONDS)
                .readTimeout(5,TimeUnit.SECONDS)
                .writeTimeout(5,TimeUnit.SECONDS)
                .build()
    }
    constructor(context : Context) : this(){
        this.context = context
        token = User_Control(context).get_token()
    }

    fun GET(url: String):String{
        try {
            val builder= Request.Builder()
                    .url(url)
                    .get()
            if(!token.isNullOrEmpty())
                builder.header("Authorization",token!!)

            val request = builder.build()
            response = client.newCall(request).execute()
            return response.body()?.string()!!
        }catch (e: IOException){
            return e.toString()
        }
    }

    fun POST(url: String, jsonbody: String):String{
        try {
            val builder= Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("application/json"), jsonbody))
            if(!token.isNullOrEmpty())
                builder.header("Authorization",token!!)
            val request = builder.build()
            response = client.newCall(request).execute()
            if(!response.header("Authorization").isNullOrEmpty())
                User_Control(context!!).set_token(response.header("Authorization").toString())
            return response.body()?.string()!!
        }catch (e: IOException){
            return e.toString()
        }
    }

    fun DELETE(url: String, jsonbody: String):String{
        try {
            val builder= Request.Builder()
                    .url(url)
                    .delete(RequestBody.create(MediaType.parse("application/json"), jsonbody))
            Log.d("Okhttp",jsonbody)
            if(!token.isNullOrEmpty())
                builder.header("Authorization",token!!)

            val request = builder.build()
            response = client.newCall(request).execute()
            return response.body()?.string()!!
        }catch (e: IOException){
            return e.toString()
        }
    }

    fun PUT(url: String, jsonbody: String):String{
        try {
            val builder= Request.Builder()
                    .url(url)
                    .put(RequestBody.create(MediaType.parse("application/json"), jsonbody))
            if(!token.isNullOrEmpty())
                builder.header("Authorization",token!!)

            val request = builder.build()
            response = client.newCall(request).execute()
            return response.body()?.string()!!
        }catch (e: IOException){
            return e.toString()
        }
    }
}