package lookid_front.lookid.Entity

import java.io.Serializable

data class Child(
        var c_pid: Int, //피보호자 인덱스
        var c_name: String, //피보호자 이름
        var x: Double, //x좌표
        var y: Double, //y좌표
        var isMissing: Boolean //미아 여부 -> 수정한 부분 (****물어봐야함****)
) : Serializable {
    constructor() : this(0,"",0.0,0.0,false)
    constructor(c_name : String) : this(0, c_name, 0.0, 0.0,false)
}