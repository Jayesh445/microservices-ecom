package works.jayesh.webby.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OrderResponse(
        @SerializedName("id") val id: Long,
        @SerializedName("orderNumber") val orderNumber: String,
        @SerializedName("userId") val userId: Long,
        @SerializedName("userName") val userName: String?,
        @SerializedName("items") val items: List<OrderItemResponse>,
        @SerializedName("totalAmount") val totalAmount: Double,
        @SerializedName("discountAmount") val discountAmount: Double?,
        @SerializedName("shippingAmount") val shippingAmount: Double?,
        @SerializedName("taxAmount") val taxAmount: Double?,
        @SerializedName("finalAmount") val finalAmount: Double,
        @SerializedName("status") val status: String,
        @SerializedName("paymentStatus") val paymentStatus: String?,
        @SerializedName("shippingAddressId") val shippingAddressId: Long?,
        @SerializedName("shippingAddress") val shippingAddress: AddressResponse?,
        @SerializedName("createdAt") val createdAt: String,
        @SerializedName("updatedAt") val updatedAt: String?
)

data class OrderItemResponse(
        @SerializedName("id") val id: Long,
        @SerializedName("productId") val productId: Long,
        @SerializedName("productName") val productName: String,
        @SerializedName("productImage") val productImage: String?,
        @SerializedName("price") val price: Double,
        @SerializedName("quantity") val quantity: Int,
        @SerializedName("subtotal") val subtotal: Double
)

data class OrderCreateRequest(
        @SerializedName("userId") val userId: Long,
        @SerializedName("items") val items: List<OrderItemRequest>,
        @SerializedName("shippingAddressId") val shippingAddressId: Long,
        @SerializedName("paymentMethod") val paymentMethod: String
)

data class OrderItemRequest(
        @SerializedName("productId") val productId: Long,
        @SerializedName("quantity") val quantity: Int,
        @SerializedName("price") val price: Double
)
