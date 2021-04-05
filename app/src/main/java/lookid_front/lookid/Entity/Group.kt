package lookid_front.lookid.Entity

import java.io.Serializable

data class Group(
        var g_pid: Int, //그룹 인덱스
        var child_list: ArrayList<Child>, //피보호자 리스트
        var admin_list: ArrayList<Admin>, //관리자 리스트
        var name: String //그룹명
) : Serializable{
    constructor() : this(0, arrayListOf<Child>(), arrayListOf<Admin>(), "")
    fun isnull() : Boolean{
        if(child_list.size == 0 || admin_list.size == 0)
            return true
        for(i in 0 until child_list.size){
            if(child_list[i].c_name.isNullOrEmpty())
                return true
        }
        for(i in 0 until admin_list.size){
            if(admin_list[i].id.isEmpty())
                return true
        }
        return false
    }
}