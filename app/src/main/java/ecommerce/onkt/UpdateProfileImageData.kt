package ecommerce.onkt

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateProfileImageData {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("Profile")
    @Expose
    var profile: String? = null

}
