package works.jayesh.webby.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductResponse(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("sku") val sku: String?,
        @SerializedName("description") val description: String?,
        @SerializedName("shortDescription") val shortDescription: String?,
        @SerializedName("price") val price: Double,
        @SerializedName("discountPrice") val discountPrice: Double?,
        @SerializedName("stockQuantity") val stockQuantity: Int,
        @SerializedName("slug") val slug: String,
        @SerializedName("categoryId") val categoryId: Long?,
        @SerializedName("categoryName") val categoryName: String?,
        @SerializedName("sellerId") val sellerId: Long?,
        @SerializedName("sellerName") val sellerName: String?,
        @SerializedName("images") val images: List<String>?,
        @SerializedName("brand") val brand: String?,
        @SerializedName("manufacturer") val manufacturer: String?,
        @SerializedName("weight") val weight: Double?,
        @SerializedName("active") val active: Boolean,
        @SerializedName("featured") val featured: Boolean,
        @SerializedName("status") val status: String?,
        @SerializedName("averageRating") val averageRating: Double?,
        @SerializedName("totalReviews") val totalReviews: Int?,
        @SerializedName("totalSold") val totalSold: Int?,
        @SerializedName("tags") val tags: List<String>?,
        @SerializedName("createdAt") val createdAt: String?,
        @SerializedName("updatedAt") val updatedAt: String?
)

data class CategoryResponse(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("slug") val slug: String,
        @SerializedName("description") val description: String?,
        @SerializedName("parentId") val parentId: Long?,
        @SerializedName("imageUrl") val imageUrl: String?,
        @SerializedName("active") val active: Boolean
)

data class ProductCreateRequest(
        @SerializedName("name") val name: String,
        @SerializedName("description") val description: String?,
        @SerializedName("shortDescription") val shortDescription: String?,
        @SerializedName("price") val price: Double,
        @SerializedName("discountPrice") val discountPrice: Double?,
        @SerializedName("stockQuantity") val stockQuantity: Int,
        @SerializedName("categoryId") val categoryId: Long,
        @SerializedName("images") val images: List<String>?,
        @SerializedName("brand") val brand: String?,
        @SerializedName("manufacturer") val manufacturer: String?,
        @SerializedName("weight") val weight: Double?,
        @SerializedName("tags") val tags: List<String>?
)
