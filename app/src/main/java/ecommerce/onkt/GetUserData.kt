package ecommerce.onkt

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetUserData {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("UserData")
    @Expose
    var userData: List<GetUserDataResponse>? = null

    class GetUserDataResponse {
        @SerializedName("userid")
        @Expose
        var userid: String? = null

        @SerializedName("first_name")
        @Expose
        var firstName: String? = null

        @SerializedName("last_name")
        @Expose
        var lastName: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("contact")
        @Expose
        var contact: String? = null

        @SerializedName("gender")
        @Expose
        var gender: String? = null
    }

}
