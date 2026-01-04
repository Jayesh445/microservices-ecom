package works.jayesh.webby.navigation

sealed class Destinations(val route: String) {
    // Authentication
    object Login : Destinations("login")
    object Register : Destinations("register")
    object Otp : Destinations("otp/{email}") {
        fun createRoute(email: String) = "otp/$email"
    }

    // Main App
    object Home : Destinations("home")
    object Search : Destinations("search")
    object Cart : Destinations("cart")
    object Orders : Destinations("orders")
    object Profile : Destinations("profile")

    // Products
    object ProductDetails : Destinations("product/{productId}") {
        fun createRoute(productId: Long) = "product/$productId"
    }
    object ProductList : Destinations("products?categoryId={categoryId}&query={query}") {
        fun createRoute(categoryId: Long? = null, query: String? = null): String {
            val params =
                    buildList {
                                if (categoryId != null) add("categoryId=$categoryId")
                                if (query != null) add("query=$query")
                            }
                            .joinToString("&")
            return if (params.isEmpty()) "products" else "products?$params"
        }
    }
    object CategoryList : Destinations("categories")

    // Cart & Checkout
    object Checkout : Destinations("checkout")
    object AddressSelection : Destinations("address_selection")
    object AddressAdd : Destinations("address_add")
    object AddressEdit : Destinations("address_edit/{addressId}") {
        fun createRoute(addressId: Long) = "address_edit/$addressId"
    }
    object PaymentMethod : Destinations("payment_method")

    // Orders
    object OrderDetails : Destinations("order/{orderId}") {
        fun createRoute(orderId: Long) = "order/$orderId"
    }
    object OrderTracking : Destinations("order_tracking/{orderId}") {
        fun createRoute(orderId: Long) = "order_tracking/$orderId"
    }

    // Profile
    object EditProfile : Destinations("edit_profile")
    object AddressList : Destinations("address_list")
    object Wishlist : Destinations("wishlist")
    object Settings : Destinations("settings")

    // Reviews
    object WriteReview : Destinations("write_review/{productId}") {
        fun createRoute(productId: Long) = "write_review/$productId"
    }
    object ReviewList : Destinations("reviews/{productId}") {
        fun createRoute(productId: Long) = "reviews/$productId"
    }
}

sealed class BottomNavItem(
        val route: String,
        val title: String,
        val icon: String // Will use Icons in actual implementation
) {
    object Home : BottomNavItem(route = Destinations.Home.route, title = "Home", icon = "home")

    object Categories :
            BottomNavItem(
                    route = Destinations.CategoryList.route,
                    title = "Categories",
                    icon = "category"
            )

    object Cart : BottomNavItem(route = Destinations.Cart.route, title = "Cart", icon = "cart")

    object Orders :
            BottomNavItem(route = Destinations.Orders.route, title = "Orders", icon = "receipt")

    object Profile :
            BottomNavItem(route = Destinations.Profile.route, title = "Profile", icon = "person")
}
