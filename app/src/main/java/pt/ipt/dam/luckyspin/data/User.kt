package pt.ipt.dam.luckyspin.data


data class User(
    val id: Int? = null,
    val email: String?,
    val hashPass: String?,
    val username: String?,
    val creditos: Int?
)