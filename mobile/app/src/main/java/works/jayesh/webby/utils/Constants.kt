package works.jayesh.webby.utils

object Constants {
    // API
    const val BASE_URL = "http://10.108.177.188:8080/api/"
    const val TIMEOUT_SECONDS = 30L

    // DataStore
    const val DATASTORE_NAME = "webby_preferences"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_ROLE = "user_role"

    // Pagination
    const val PAGE_SIZE = 20
    const val INITIAL_PAGE = 0

    // Debounce
    const val SEARCH_DEBOUNCE_MILLIS = 500L

    // Price Format
    const val CURRENCY_SYMBOL = "₹"

    // Order Status Colors
    const val STATUS_PENDING = 0xFFFFA500
    const val STATUS_CONFIRMED = 0xFF4CAF50
    const val STATUS_PROCESSING = 0xFF2196F3
    const val STATUS_SHIPPED = 0xFF9C27B0
    const val STATUS_DELIVERED = 0xFF4CAF50
    const val STATUS_CANCELLED = 0xFFF44336

    // Image Placeholders
    const val PLACEHOLDER_PRODUCT = "https://via.placeholder.com/400x400?text=Product"
    const val PLACEHOLDER_USER = "https://via.placeholder.com/200x200?text=User"
}
