package lookid_front.lookid.View

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import lookid_front.lookid.R


class StateEditText : AppCompatEditText{
    lateinit var clearDrawable : Drawable
    lateinit var checkDrawable: Drawable
    lateinit var uncheckDrawable: Drawable
    var min_ems : Int? = null
    var max_ems : Int? = null

    constructor(context: Context) : super(context) { create(null) }
    constructor(context: Context, attrs : AttributeSet) : super(context, attrs) { create(attrs) }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int) : super(context, attrs, defStyleAttr) { create(attrs)}

    fun create(attrs: AttributeSet?){
        var tempDrawable : Drawable = ContextCompat.getDrawable(context, R.drawable.icon_clear)!!
        clearDrawable = DrawableCompat.wrap(tempDrawable)
        DrawableCompat.setTintList(clearDrawable,hintTextColors)
        clearDrawable.setBounds(0,0,clearDrawable.intrinsicWidth,clearDrawable.intrinsicHeight)

        tempDrawable = ContextCompat.getDrawable(context, R.drawable.checkbox_check)!!
        checkDrawable = DrawableCompat.wrap(tempDrawable)
        checkDrawable.setBounds(0,0,checkDrawable.intrinsicWidth,checkDrawable.intrinsicHeight)

        tempDrawable = ContextCompat.getDrawable(context, R.drawable.checkbox_uncheck)!!
        uncheckDrawable = DrawableCompat.wrap(tempDrawable)
        uncheckDrawable.setBounds(0,0,uncheckDrawable.intrinsicWidth,uncheckDrawable.intrinsicHeight)

        setClearIconVisible(false)

        setOnTouchListener(touchListener())
        onFocusChangeListener = focusChangeListener()

        if(attrs != null){
            min_ems = context.obtainStyledAttributes(attrs, R.styleable.stateEditText).getInt(R.styleable.stateEditText_min_ems,minEms)
            max_ems = context.obtainStyledAttributes(attrs, R.styleable.stateEditText).getInt(R.styleable.stateEditText_max_ems,maxEms)
        }
    }

    fun setCheckIconVisible (visible: Boolean){
        checkDrawable.setVisible(visible,false)
        if(visible)
            setCompoundDrawables(null,null,checkDrawable,null)
        else
            setCompoundDrawables(null,null,null,null)
    }

    fun setUnCheckIconVisible (visible: Boolean){
        uncheckDrawable.setVisible(visible,false)
        if(visible)
            setCompoundDrawables(null,null,uncheckDrawable,null)
        else
            setCompoundDrawables(null,null,null,null)
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
            if(hasFocus) {
                setClearIconVisible(text!!.isNotEmpty())
            }
            else {
                setClearIconVisible(false)
                if(min_ems!! <= text!!.length && text!!.length <= max_ems!!)
                    setCheckIconVisible(true)
                else
                    setUnCheckIconVisible(true)
            }
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
