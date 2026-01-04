package works.jayesh.webby.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddressResponse(
        @SerializedName("id") val id: Long,
        @SerializedName("userId") val userId: Long,
        @SerializedName("fullName") val fullName: String,
        @SerializedName("phoneNumber") val phoneNumber: String,
        @SerializedName("addressLine1") val addressLine1: String,
        @SerializedName("addressLine2") val addressLine2: String?,
        @SerializedName("city") val city: String,
        @SerializedName("state") val state: String,
        @SerializedName("country") val country: String,
        @SerializedName("zipCode") val zipCode: String,
        @SerializedName("addressType") val addressType: String,
        @SerializedName("isDefault") val isDefault: Boolean
)

data class AddressRequest(
        @SerializedName("fullName") val fullName: String,
        @SerializedName("phoneNumber") val phoneNumber: String,
        @SerializedName("addressLine1") val addressLine1: String,
        @SerializedName("addressLine2") val addressLine2: String?,
        @SerializedName("city") val city: String,
        @SerializedName("state") val state: String,
        @SerializedName("country") val country: String,
        @SerializedName("zipCode") val zipCode: String,
        @SerializedName("addressType") val addressType: String,
        @SerializedName("isDefault") val isDefault: Boolean
)
