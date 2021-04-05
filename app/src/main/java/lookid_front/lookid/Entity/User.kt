package lookid_front.lookid.Entity

import java.io.Serializable

data class User(
        var id: String?, //아이디
        var name: String?, //사용자 이름
        var phone: String?, //사용자 번호
        var email: String?, //사용자 이메일
        var address: String?, //사용자 주소
        var bank_name: String?, //은행명
        var bank_num: String?, //계좌번호
        var bank_holder: String? //예금주
) : Serializable{
    var address_detail : String? = null
    constructor() : this("","","","","","","","")
    fun isresnull() : Boolean{
        if(name.isNullOrEmpty() || phone.isNullOrEmpty() || address.isNullOrEmpty() || bank_name.isNullOrEmpty() ||
                bank_num.isNullOrEmpty() || bank_holder.isNullOrEmpty())
            return true
        return false
    }

    fun bank_toString() : String{ return "${bank_name} ${bank_num}\n예금주명 : ${bank_holder}"}
}

