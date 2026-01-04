package works.jayesh.webby.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
        @SerializedName("id") val id: Long,
        @SerializedName("productId") val productId: Long,
        @SerializedName("userId") val userId: Long,
        @SerializedName("userName") val userName: String,
        @SerializedName("rating") val rating: Int,
        @SerializedName("title") val title: String?,
        @SerializedName("comment") val comment: String?,
        @SerializedName("images") val images: List<String>?,
        @SerializedName("verified") val verified: Boolean,
        @SerializedName("helpful") val helpful: Int,
        @SerializedName("createdAt") val createdAt: String
)

data class ReviewRequest(
        @SerializedName("productId") val productId: Long,
        @SerializedName("rating") val rating: Int,
        @SerializedName("title") val title: String?,
        @SerializedName("comment") val comment: String?,
        @SerializedName("images") val images: List<String>?
)

data class PaymentResponse(
        @SerializedName("id") val id: Long,
        @SerializedName("orderId") val orderId: Long,
        @SerializedName("amount") val amount: Double,
        @SerializedName("paymentMethod") val paymentMethod: String,
        @SerializedName("status") val status: String,
        @SerializedName("transactionId") val transactionId: String?,
        @SerializedName("createdAt") val createdAt: String
)

data class PaymentRequest(
        @SerializedName("orderId") val orderId: Long,
        @SerializedName("paymentMethod") val paymentMethod: String,
        @SerializedName("amount") val amount: Double
)
