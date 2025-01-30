package pt.ipt.dam.luckyspin.data

import retrofit2.Call
import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ApiSheety {

    data class UserResponse(
        val user : List<User>
    )

    data class UserRequest(
        val user : User
    )


    @GET("users/")
    fun getUsers(): Call<UserResponse>

    @POST("users/")
     fun createUser(@Body userReq: UserRequest): Call<UserRequest>

    @PUT("users/{id}")
     fun updateUser(@Path("id") id: Int, @Body userReq: UserRequest): Call<UserRequest>

    @DELETE("users/{id}")
     fun deleteUser(@Path("id") id: Int): Call<Void>

}

object Api {
    private const val BASE_URL = "https://api.sheety.co/5d12346ba9702c09e1066fa91a3414fb/luckySpin/"

    val instance: ApiSheety by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiSheety::class.java)
    }
}