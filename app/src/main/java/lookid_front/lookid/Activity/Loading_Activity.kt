package lookid_front.lookid.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import lookid_front.lookid.R

class Loading_Activity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        startActivity(Intent(applicationContext,SignIn_Activity::class.java))
        finish()
    }
}