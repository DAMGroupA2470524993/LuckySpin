package pt.ipt.dam.luckyspin.data

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository() {

    private val api = Api.instance

    fun getUsers(onResult: (List<User>?) -> Unit) {
        api.getUsers().enqueue(object : Callback<ApiSheety.UserResponse> {
            override fun onResponse(
                call: Call<ApiSheety.UserResponse>,
                response: Response<ApiSheety.UserResponse>
            ) {
                if (response.isSuccessful) {
                    val users = response.body()?.users ?: emptyList()
                    onResult(users)
                } else {
                    onResult(null)
                }
            }
            override fun onFailure(call: Call<ApiSheety.UserResponse>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun createUser(user: User, onResult: (User?) -> Unit) {
        val newUser = User(
            email = user.email,
            hashPass = user.hashPass,
            username = user.username,
            creditos = user.creditos
        )

        val request = ApiSheety.UserRequest(newUser)

        api.createUser(request).enqueue(object : Callback<ApiSheety.UserRequest> {
            override fun onResponse(
                call: Call<ApiSheety.UserRequest>,
                response: Response<ApiSheety.UserRequest>
            ) {
                if (response.isSuccessful) {
                    Log.d("teste_response", response.body().toString())
                    val createdUser = response.body()?.user
                    onResult(createdUser)
                } else {

                    onResult(null)
                }
            }

            override fun onFailure(call: Call<ApiSheety.UserRequest>, t: Throwable) {
                onResult(null)
            }
        })
    }


    fun updateUser(id: Int, user: User, onResult: (User?) -> Unit) {
        val request = ApiSheety.UserRequest(user)
        api.updateUser(id, request).enqueue(object : Callback<ApiSheety.UserRequest> {
            override fun onResponse(
                call: Call<ApiSheety.UserRequest>,
                response: Response<ApiSheety.UserRequest>
            ) {
                if (response.isSuccessful) {
                    onResult(response.body()?.user)
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<ApiSheety.UserRequest>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun deleteUser(id: Int, onResult: (Boolean) -> Unit) {
        api.deleteUser(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                onResult(response.isSuccessful)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onResult(false)
            }
        })
    }

}
