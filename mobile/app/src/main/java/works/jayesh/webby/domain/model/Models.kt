package works.jayesh.webby.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class User(
        val id: Long,
        val email: String,
        val firstName: String,
        val lastName: String,
        val role: String,
        val phoneNumber: String? = null
) : Parcelable {
    val fullName: String
        get() = "$firstName $lastName"
}

@Parcelize
data class Product(
        val id: Long,
        val name: String,
        val sku: String?,
        val description: String?,
        val shortDescription: String?,
        val price: Double,
        val discountPrice: Double?,
        val stockQuantity: Int,
        val slug: String,
        val categoryId: Long?,
        val categoryName: String?,
        val sellerId: Long?,
        val sellerName: String?,
        val images: List<String>,
        val brand: String?,
        val manufacturer: String?,
        val weight: Double?,
        val active: Boolean,
        val featured: Boolean,
        val status: String?,
        val averageRating: Double?,
        val totalReviews: Int?,
        val totalSold: Int?,
        val tags: @RawValue List<String>,
        val createdAt: String?,
        val updatedAt: String?
) : Parcelable {
    val finalPrice: Double
        get() = discountPrice ?: price
    val discount: Int
        get() =
                if (discountPrice != null && discountPrice < price) {
                    (((price - discountPrice) / price) * 100).toInt()
                } else 0
    val isInStock: Boolean
        get() = stockQuantity > 0
    val mainImage: String
        get() = images.firstOrNull() ?: ""
}

@Parcelize
data class Category(
        val id: Long,
        val name: String,
        val slug: String,
        val description: String?,
        val parentId: Long?,
        val imageUrl: String?,
        val active: Boolean
) : Parcelable

@Parcelize
data class Cart(
        val id: Long,
        val userId: Long,
        val items: @RawValue List<CartItem>,
        val totalAmount: Double,
        val totalItems: Int
) : Parcelable {
    val isEmpty: Boolean
        get() = items.isEmpty()
}

@Parcelize
data class CartItem(
        val id: Long,
        val productId: Long,
        val productName: String,
        val productImage: String?,
        val price: Double,
        val discountPrice: Double?,
        val quantity: Int,
        val subtotal: Double
) : Parcelable {
    val finalPrice: Double
        get() = discountPrice ?: price
}

@Parcelize
data class Order(
        val id: Long,
        val orderNumber: String,
        val userId: Long,
        val userName: String?,
        val items: @RawValue List<OrderItem>,
        val totalAmount: Double,
        val discountAmount: Double?,
        val shippingAmount: Double?,
        val taxAmount: Double?,
        val finalAmount: Double,
        val status: OrderStatus,
        val paymentStatus: String?,
        val shippingAddressId: Long?,
        val shippingAddress: Address?,
        val createdAt: String,
        val updatedAt: String?
) : Parcelable

@Parcelize
data class OrderItem(
        val id: Long,
        val productId: Long,
        val productName: String,
        val productImage: String?,
        val price: Double,
        val quantity: Int,
        val subtotal: Double
) : Parcelable

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    companion object {
        fun fromString(status: String): OrderStatus {
            return try {
                valueOf(status.uppercase())
            } catch (e: Exception) {
                PENDING
            }
        }
    }
}

@Parcelize
data class Address(
        val id: Long,
        val userId: Long,
        val fullName: String,
        val phoneNumber: String,
        val addressLine1: String,
        val addressLine2: String?,
        val city: String,
        val state: String,
        val country: String,
        val zipCode: String,
        val addressType: AddressType,
        val isDefault: Boolean
) : Parcelable {
    val fullAddress: String
        get() = buildString {
            append(addressLine1)
            if (!addressLine2.isNullOrBlank()) append(", $addressLine2")
            append(", $city, $state")
            append(", $country - $zipCode")
        }
}

enum class AddressType {
    HOME,
    WORK,
    OTHER;

    companion object {
        fun fromString(type: String): AddressType {
            return try {
                valueOf(type.uppercase())
            } catch (e: Exception) {
                HOME
            }
        }
    }
}

@Parcelize
data class Review(
        val id: Long,
        val productId: Long,
        val userId: Long,
        val userName: String,
        val rating: Int,
        val title: String?,
        val comment: String?,
        val images: List<String>,
        val verified: Boolean,
        val helpful: Int,
        val createdAt: String
) : Parcelable

@Parcelize
data class Payment(
        val id: Long,
        val orderId: Long,
        val amount: Double,
        val paymentMethod: PaymentMethod,
        val status: String,
        val transactionId: String?,
        val createdAt: String
) : Parcelable

enum class PaymentMethod {
    COD,
    UPI,
    CARD,
    NET_BANKING;

    companion object {
        fun fromString(method: String): PaymentMethod {
            return try {
                valueOf(method.uppercase())
            } catch (e: Exception) {
                COD
            }
        }
    }
}
