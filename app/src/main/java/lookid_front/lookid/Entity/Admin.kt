package lookid_front.lookid.Entity

import java.io.Serializable

data class Admin(
        var user_pid : Int,
        var id : String,
        var name : String
): Serializable {
}