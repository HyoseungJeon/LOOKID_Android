package lookid_front.lookid.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.EditText
import android.widget.TextView
import lookid_front.lookid.Entity.Address
import lookid_front.lookid.R
import lookid_front.lookid.R.id.text
import lookid_front.lookid.R.id.textView

class Address_Dialog(context: Context) : Dialog(context) {
    var textView : TextView? = null
    var editText : EditText? = null
    constructor(context: Context, textView : TextView) : this(context){
        this.textView = textView
    }
    constructor(context: Context, editText: EditText) : this(context){
        this.editText = editText
    }
    var arg : String? = null
    var handler : Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_address)
        //url 장착 후 동작 해야됨
        init_Activity()
        handler = Handler()
    }

    fun init_Activity(){
        val webView : WebView = findViewById(R.id.address_dialog_WebView)
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.addJavascriptInterface(address_Interface(), "LOOKID")
        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(Address().kakaoApi)
    }
    fun getAddress() : String? { return arg }
    inner class address_Interface{
        @android.webkit.JavascriptInterface
        fun setAddress(arg1 : String, arg2 : String, arg3 : String){
            handler!!.post(Runnable {
                arg = "(${arg1}) ${arg2} ${arg3}"
                if(editText != null)
                    editText!!.setText(arg)
                else
                    textView!!.setText(arg)
                init_Activity()
                dismiss()
            })
        }
    }
}