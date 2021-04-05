package lookid_front.lookid.Control

class Date_Control(){
    val dateFormat : String = "yyyy-MM-dd"
    fun toDateformat(year : Int, month : Int, day : Int) : String{
        var str : String = ""
        str += "$year-"
        if(month.toString().length == 1)
            str += '0'
        str += "$month-"
        if(day.toString().length == 1)
            str += '0'
        str += "$day"
        return str
    }
}