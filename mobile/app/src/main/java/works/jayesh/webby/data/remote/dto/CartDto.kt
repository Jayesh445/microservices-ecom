package works.jayesh.webby.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CartResponse(
        @SerializedName("id") val id: Long,
        @SerializedName("userId") val userId: Long,
        @SerializedName("items") val items: List<CartItemResponse>,
        @SerializedName("totalAmount") val totalAmount: Double,
        @SerializedName("totalItems") val totalItems: Int
)

data class CartItemResponse(
        @SerializedName("id") val id: Long,
        @SerializedName("productId") val productId: Long,
        @SerializedName("productName") val productName: String,
        @SerializedName("productImage") val productImage: String?,
        @SerializedName("price") val price: Double,
        @SerializedName("discountPrice") val discountPrice: Double?,
        @SerializedName("quantity") val quantity: Int,
        @SerializedName("subtotal") val subtotal: Double
)

data class AddToCartRequest(
        @SerializedName("productId") val productId: Long,
        @SerializedName("quantity") val quantity: Int
)
