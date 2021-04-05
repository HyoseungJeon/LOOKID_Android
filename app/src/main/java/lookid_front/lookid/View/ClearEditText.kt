package lookid_front.lookid.View

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import lookid_front.lookid.R


class ClearEditText : AppCompatEditText{
    lateinit var clearDrawable : Drawable
    var min_ems : Int? = null
    var max_ems : Int? = null
    init {
        create()
    }
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs : AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int) : super(context, attrs, defStyleAttr)

    fun create(){
        val tempDrawable : Drawable = ContextCompat.getDrawable(context, R.drawable.icon_clear)!!
        clearDrawable = DrawableCompat.wrap(tempDrawable)
        setHintTextColor(ContextCompat.getColor(context,R.color.LOOKID_second_ground))
        DrawableCompat.setTintList(clearDrawable,hintTextColors)
        clearDrawable.setBounds(0,0,clearDrawable.intrinsicWidth,clearDrawable.intrinsicHeight)
        setClearIconVisible(false)
        setOnTouchListener(touchListener())
        onFocusChangeListener = focusChangeListener()
    }

    fun setClearIconVisible (visible : Boolean){
        clearDrawable.setVisible(visible,false)
        if(visible)
            setCompoundDrawables(null,null,clearDrawable,null)
        else
            setCompoundDrawables(null,null,null,null)
    }

    inner class focusChangeListener : OnFocusChangeListener{
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if(hasFocus)
                setClearIconVisible(text!!.isNotEmpty())
            else
                setClearIconVisible(false)
        }
    }

    inner class touchListener : OnTouchListener{
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val x : Int = event.x.toInt()
            if(clearDrawable.isVisible && x > width - paddingRight - clearDrawable.intrinsicWidth) {
                if (event.action == MotionEvent.ACTION_UP) {
                    error = null
                    text = null
                }
                return true
            }
            return false
        }
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        if(isFocused)
            setClearIconVisible(!text.isNullOrEmpty())
    }
}
