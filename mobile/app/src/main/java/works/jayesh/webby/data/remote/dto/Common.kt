package works.jayesh.webby.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
        @SerializedName("success") val success: Boolean,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: T?
)

data class PageResponse<T>(
        @SerializedName("content") val content: List<T>,
        @SerializedName("page") val page: Int,
        @SerializedName("size") val size: Int,
        @SerializedName("totalElements") val totalElements: Long,
        @SerializedName("totalPages") val totalPages: Int,
        @SerializedName("last") val last: Boolean
)
