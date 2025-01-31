package pt.ipt.dam.luckyspin.data

data class UserWithoutId(
    val email: String?,
    val hashPass: String?,
    val username: String?,
    val creditos: Int?
)
