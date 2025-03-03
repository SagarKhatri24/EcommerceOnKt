package ecommerce.onkt

class ConstantSp {

    companion object {
        val BASE_URL: String = "http://192.168.1.54/FinalInternshipOn/"
        val SIGNUP_URL: String = BASE_URL + "signup.php"
        val LOGIN_URL: String = BASE_URL + "login.php"
        val UPDATE_PROFILE_URL: String = BASE_URL + "updateProfile.php"
        val DELETE_PROFILE_URL: String = BASE_URL + "deleteProfile.php"
        val GET_USER_URL: String = BASE_URL + "getUserData.php"

        val SERVER_ERROR: String = "Server Error Code : "

        val PREF: String = "pref"
        val USERID: String = "userid"
        val FIRSTNAME: String = "firstname"
        val LASTNAME: String = "lastname"
        val EMAIL: String = "email"
        val CONTACT: String = "contact"
        val PASSWORD: String = "password"
        val GENDER: String = "gender"
        val PROFILE: String = "profile"
        val FCM_ID: String = "fcm_id"
    }
}