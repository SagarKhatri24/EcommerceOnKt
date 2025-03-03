package ecommerce.onkt

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {

    @FormUrlEncoded
    @POST("signup.php")
    fun getSignupData(
        @Field("firstname") firstname: String?,
        @Field("lastname") lastname: String?,
        @Field("email") email: String?,
        @Field("contact") contact: String?,
        @Field("password") password: String?,
        @Field("gender") gender: String?
    ): Call<GetSignupData>

    @FormUrlEncoded
    @POST("login.php")
    fun getLoginData(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<GetLoginData>

    @FormUrlEncoded
    @POST("updateProfile.php")
    fun updateProfileData(
        @Field("firstname") firstname: String?,
        @Field("lastname") lastname: String?,
        @Field("email") email: String?,
        @Field("contact") contact: String?,
        @Field("password") password: String?,
        @Field("gender") gender: String?,
        @Field("userid") userId: String?
    ): Call<GetSignupData>?

    @FormUrlEncoded
    @POST("deleteProfile.php")
    fun deleteProfileData(
        @Field("userid") userId: String?
    ): Call<GetSignupData>?

    @GET("getUserData.php")
    fun getUserData(): Call<GetUserData>?

    @Multipart
    @POST("updateProfileImage.php")
    fun updateProfileImageData(
        @Part("firstname") firstname: RequestBody?,
        @Part("lastname") lastname: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("contact") contact: RequestBody?,
        @Part("password") password: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part("userid") userId: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Call<UpdateProfileImageData>?

}