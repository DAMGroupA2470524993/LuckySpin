package pt.ipt.dam.luckyspin.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class MbwayRequest(
    val payment: Payment
)

data class Payment(
    val amount: Amount,
    val identifier: String,
    val customerPhone: String,
    val countryCode: String
)

data class Amount(
    val currency: String,
    val value: Int
)

data class MbwayPaymentResponse(
    val transactionStatus: String,
    val transactionID: String?,
    val reference: String?
)


interface MbWAY {
    @Headers(
        "accept: application/json",
        "content-type: application/json",
        "Authorization: ApiKey demo-ed56-83d1-1326-cb5"
    )
    @POST("mbway/create")
    fun createMbwayPayment(
        @Body request: MbwayRequest
    ): Call<MbwayPaymentResponse>
}
