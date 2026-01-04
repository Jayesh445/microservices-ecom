# Complete Navigation Guide - Webby E-Commerce App

## 📋 Table of Contents

1. [Navigation Overview](#navigation-overview)
2. [Setup & Configuration](#setup--configuration)
3. [Destinations & Routes](#destinations--routes)
4. [Navigation Graph](#navigation-graph)
5. [Bottom Navigation](#bottom-navigation)
6. [Navigation Operations](#navigation-operations)
7. [Passing Data Between Screens](#passing-data-between-screens)
8. [Deep Linking](#deep-linking)
9. [Best Practices](#best-practices)
10. [Complete Flow Diagrams](#complete-flow-diagrams)

---

## 1. Navigation Overview

### What is Navigation?

Navigation in Android refers to the interactions that allow users to navigate across, into, and back out from the different pieces of content within your app.

### Jetpack Navigation Component

- **Type-safe** navigation between destinations
- **Automatic** back stack management
- **Built-in** support for deep links
- **Integration** with Material components (Bottom Nav, Nav Drawer)

### Our App's Navigation Structure

```
Webby App
├── Auth Flow (Not Logged In)
│   ├── Login Screen
│   ├── Register Screen
│   └── OTP Screen
│
└── Main App Flow (Logged In)
    ├── Home Screen (Bottom Nav)
    ├── Categories Screen (Bottom Nav)
    ├── Cart Screen (Bottom Nav)
    ├── Orders Screen (Bottom Nav)
    ├── Profile Screen (Bottom Nav)
    │
    ├── Product Details
    ├── Product List
    ├── Search
    ├── Checkout
    ├── Address Management
    ├── Order Details
    ├── Reviews
    └── Settings
```

---

## 2. Setup & Configuration

### Dependencies (build.gradle.kts)

```kotlin
dependencies {
    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // For ViewModel integration (optional)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
}
```

### Basic Setup in MainActivity

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WebbyTheme {
                WebbyApp()  // Entry point with navigation
            }
        }
    }
}
```

---

## 3. Destinations & Routes

### What are Destinations?

Destinations are individual screens/composables in your app that users can navigate to.

### Route Definition Pattern

```kotlin
// File: navigation/Destinations.kt

sealed class Destinations(val route: String) {
    // Simple routes (no parameters)
    data object Home : Destinations("home")
    data object Cart : Destinations("cart")
    data object Profile : Destinations("profile")

    // Routes with parameters
    data object ProductDetails : Destinations("product/{productId}") {
        fun createRoute(productId: Long) = "product/$productId"
    }

    data object OrderDetails : Destinations("order/{orderId}") {
        fun createRoute(orderId: Long) = "order/$orderId"
    }
}
```

### Why Sealed Classes?

✅ **Type Safety**: Compiler checks at build time  
✅ **Exhaustive When**: Must handle all cases  
✅ **Organization**: All routes in one place  
✅ **Refactoring**: Easy to rename/update routes

### Route Naming Conventions

```kotlin
// ✅ Good: Descriptive, lowercase, hyphenated
"product-details/{id}"
"user-profile"
"order-history"

// ❌ Bad: CamelCase, spaces, unclear
"ProductDetails"
"user profile"
"screen1"
```

### Complete Routes in Our App

#### Auth Routes

```kotlin
data object Login : Destinations("login")
data object Register : Destinations("register")
data object Otp : Destinations("otp/{email}") {
    fun createRoute(email: String) = "otp/$email"
}
```

#### Main App Routes

```kotlin
// Bottom Navigation
data object Home : Destinations("home")
data object CategoryList : Destinations("categories")
data object Cart : Destinations("cart")
data object Orders : Destinations("orders")
data object Profile : Destinations("profile")

// Product Routes
data object ProductDetails : Destinations("product/{productId}") {
    fun createRoute(productId: Long) = "product/$productId"
}
data object ProductList : Destinations("products")
data object Search : Destinations("search")

// Checkout Routes
data object Checkout : Destinations("checkout")
data object AddressSelection : Destinations("address-selection")
data object AddressAdd : Destinations("address-add")
data object AddressEdit : Destinations("address-edit/{addressId}") {
    fun createRoute(addressId: Long) = "address-edit/$addressId"
}
data object PaymentMethod : Destinations("payment-method")

// Order Routes
data object OrderDetails : Destinations("order/{orderId}") {
    fun createRoute(orderId: Long) = "order/$orderId"
}
data object OrderTracking : Destinations("order-tracking/{orderId}") {
    fun createRoute(orderId: Long) = "order-tracking/$orderId"
}

// Profile Routes
data object EditProfile : Destinations("edit-profile")
data object AddressList : Destinations("addresses")
data object Wishlist : Destinations("wishlist")
data object Settings : Destinations("settings")

// Review Routes
data object WriteReview : Destinations("write-review/{productId}") {
    fun createRoute(productId: Long) = "write-review/$productId"
}
data object ReviewList : Destinations("reviews/{productId}") {
    fun createRoute(productId: Long) = "reviews/$productId"
}
```

---

## 4. Navigation Graph

### What is NavHost?

`NavHost` is a container that displays the current destination based on the route.

### App Entry Point

```kotlin
@Composable
fun WebbyApp() {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }

    // Route based on auth state
    if (isLoggedIn) {
        MainScreen(navController)
    } else {
        AuthNavHost(navController, onLoginSuccess = { isLoggedIn = true })
    }
}
```

#### Key Concepts:

- **`rememberNavController()`**: Creates nav controller, survives recomposition
- **State Management**: Controls which flow to show (Auth vs Main)
- **Callbacks**: Pass success handlers between flows

### Auth Navigation Graph

```kotlin
@Composable
fun AuthNavHost(
    navController: NavHostController,
    onLoginSuccess: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.Login.route  // First screen
    ) {
        // Login Destination
        composable(Destinations.Login.route) {
            LoginScreen(
                onLoginSuccess = onLoginSuccess,
                onNavigateToRegister = {
                    navController.navigate(Destinations.Register.route)
                },
                onNavigateToOtp = { email ->
                    navController.navigate(Destinations.Otp.createRoute(email))
                }
            )
        }

        // Register Destination
        composable(Destinations.Register.route) {
            RegisterScreen(
                onRegisterSuccess = onLoginSuccess,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // OTP Destination with email parameter
        composable(
            route = Destinations.Otp.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OtpScreen(
                email = email,
                onVerifySuccess = onLoginSuccess,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
```

### Main Navigation Graph

```kotlin
@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.Home.route,
        modifier = modifier
    ) {
        // Home Screen
        composable(Destinations.Home.route) {
            HomeScreen(
                onNavigateToProduct = { productId ->
                    navController.navigate(
                        Destinations.ProductDetails.createRoute(productId)
                    )
                },
                onNavigateToCart = {
                    navController.navigate(Destinations.Cart.route)
                },
                onNavigateToSearch = {
                    navController.navigate(Destinations.Search.route)
                }
            )
        }

        // Product Details with parameter
        composable(
            route = Destinations.ProductDetails.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            ProductDetailsScreen(
                productId = productId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToCart = {
                    navController.navigate(Destinations.Cart.route)
                }
            )
        }

        // Cart Screen
        composable(Destinations.Cart.route) {
            CartScreen(
                onNavigateToCheckout = {
                    navController.navigate(Destinations.Checkout.route)
                },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // Add more destinations...
    }
}
```

### Main Screen with Scaffold

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value?.destination?.route

    // Show bottom bar only on main screens
    val showBottomBar = remember(currentRoute) {
        listOf(
            Destinations.Home.route,
            Destinations.CategoryList.route,
            Destinations.Cart.route,
            Destinations.Orders.route,
            Destinations.Profile.route
        ).contains(currentRoute)
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        MainNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
```

---

## 5. Bottom Navigation

### Bottom Navigation Bar

```kotlin
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // Define bottom nav items
    val items = listOf(
        Triple(Destinations.Home.route, "Home", Icons.Default.Home),
        Triple(Destinations.CategoryList.route, "Categories", Icons.Default.Category),
        Triple(Destinations.Cart.route, "Cart", Icons.Default.ShoppingCart),
        Triple(Destinations.Orders.route, "Orders", Icons.Default.Receipt),
        Triple(Destinations.Profile.route, "Profile", Icons.Default.Person)
    )

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value?.destination?.route

    NavigationBar {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            // Pop everything up to home
                            popUpTo(Destinations.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
```

### Bottom Navigation Options Explained

#### `popUpTo`

Removes destinations from back stack up to specified route

```kotlin
navController.navigate("profile") {
    popUpTo("home")  // Removes everything between home and current
}
```

#### `inclusive`

Include the popUpTo destination in removal

```kotlin
navController.navigate("home") {
    popUpTo("login") { inclusive = true }  // Remove login too
}
```

#### `saveState` & `restoreState`

Save/restore screen state when switching tabs

```kotlin
navController.navigate(route) {
    popUpTo(startDestination) { saveState = true }
    restoreState = true
}
```

#### `launchSingleTop`

Don't create new instance if already at top

```kotlin
navController.navigate("home") {
    launchSingleTop = true  // Reuse existing home screen
}
```

---

## 6. Navigation Operations

### Basic Navigation

```kotlin
// Navigate forward
navController.navigate("product-details")

// Navigate with parameters
navController.navigate(Destinations.ProductDetails.createRoute(123))

// Navigate back (pop current screen)
navController.navigateUp()

// Navigate back (alternative)
navController.popBackStack()
```

### Advanced Navigation

```kotlin
// Navigate and clear back stack
navController.navigate("home") {
    popUpTo(navController.graph.startDestinationId) {
        inclusive = true
    }
}

// Navigate with result (coming back with data)
navController.navigate("address-selection") {
    // Handle result in previous screen
    navController.previousBackStackEntry
        ?.savedStateHandle
        ?.set("selected_address", addressId)
}

// Get result in previous screen
val selectedAddress = navController
    .currentBackStackEntry
    ?.savedStateHandle
    ?.get<Long>("selected_address")
```

### Checking Navigation State

```kotlin
// Check if can navigate back
val canGoBack = navController.previousBackStackEntry != null

// Get current route
val currentRoute = navController.currentBackStackEntryAsState()
    .value?.destination?.route

// Check if on specific route
val isHome = currentRoute == Destinations.Home.route
```

---

## 7. Passing Data Between Screens

### Method 1: Route Parameters (Recommended for IDs)

```kotlin
// Define route with parameter
data object ProductDetails : Destinations("product/{productId}") {
    fun createRoute(productId: Long) = "product/$productId"
}

// Navigate with parameter
navController.navigate(Destinations.ProductDetails.createRoute(123))

// Receive parameter in destination
composable(
    route = Destinations.ProductDetails.route,
    arguments = listOf(
        navArgument("productId") { type = NavType.LongType }
    )
) { backStackEntry ->
    val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
    ProductDetailsScreen(productId = productId)
}
```

### Method 2: Parcelable Objects (Complex Data)

```kotlin
// 1. Make your model Parcelable
@Parcelize
data class Product(
    val id: Long,
    val name: String,
    val price: Double
) : Parcelable

// 2. Pass as JSON string (requires Gson/Kotlinx Serialization)
val productJson = Gson().toJson(product)
val encodedJson = URLEncoder.encode(productJson, "UTF-8")
navController.navigate("product-details/$encodedJson")

// 3. Receive and decode
val productJson = backStackEntry.arguments?.getString("product")
val product = Gson().fromJson(productJson, Product::class.java)
```

### Method 3: SavedStateHandle (Return Results)

```kotlin
// In destination screen (returning data)
navController.previousBackStackEntry
    ?.savedStateHandle
    ?.set("result_key", resultData)
navController.navigateUp()

// In calling screen (receiving data)
LaunchedEffect(Unit) {
    val result = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>("result_key")

    if (result != null) {
        // Handle result
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.remove<String>("result_key")
    }
}
```

### Argument Types Supported

```kotlin
navArgument("id") { type = NavType.LongType }
navArgument("name") { type = NavType.StringType }
navArgument("price") { type = NavType.FloatType }
navArgument("isActive") { type = NavType.BoolType }
navArgument("count") { type = NavType.IntType }

// Optional arguments
navArgument("query") {
    type = NavType.StringType
    nullable = true
    defaultValue = null
}
```

---

## 8. Deep Linking

### What is Deep Linking?

Allows users to navigate directly to specific screens from outside the app (notifications, web links, etc.)

### Setup Deep Links

```kotlin
composable(
    route = "product/{productId}",
    deepLinks = listOf(
        navDeepLink {
            uriPattern = "webby://product/{productId}"
        },
        navDeepLink {
            uriPattern = "https://webby.com/product/{productId}"
        }
    ),
    arguments = listOf(
        navArgument("productId") { type = NavType.LongType }
    )
) { backStackEntry ->
    val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
    ProductDetailsScreen(productId)
}
```

### Manifest Configuration

```xml
<activity android:name=".MainActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <data
            android:scheme="webby"
            android:host="product" />
        <data
            android:scheme="https"
            android:host="webby.com"
            android:pathPrefix="/product" />
    </intent-filter>
</activity>
```

### Handling Deep Links

```kotlin
// Links that will open ProductDetails:
// webby://product/123
// https://webby.com/product/123
```

---

## 9. Best Practices

### ✅ DO's

1. **Use Sealed Classes for Routes**

```kotlin
sealed class Destinations(val route: String) {
    data object Home : Destinations("home")
}
```

2. **Handle Back Navigation**

```kotlin
BackHandler(enabled = cartNotEmpty) {
    // Show confirmation dialog
    showDialog = true
}
```

3. **Save State for Bottom Navigation**

```kotlin
navController.navigate(route) {
    popUpTo(startDestination) { saveState = true }
    restoreState = true
}
```

4. **Use `launchSingleTop`**

```kotlin
navController.navigate(route) {
    launchSingleTop = true
}
```

5. **Check Navigation State**

```kotlin
if (navController.currentBackStackEntry != null) {
    navController.navigateUp()
}
```

### ❌ DON'Ts

1. **Don't Hardcode Routes**

```kotlin
// ❌ Bad
navController.navigate("product/123")

// ✅ Good
navController.navigate(Destinations.ProductDetails.createRoute(123))
```

2. **Don't Pass Large Objects in Routes**

```kotlin
// ❌ Bad - Complex object in route
navController.navigate("screen/${complexObject}")

// ✅ Good - Just pass ID, fetch data in destination
navController.navigate("screen/${object.id}")
```

3. **Don't Ignore Back Stack Management**

```kotlin
// ❌ Bad - Back stack grows infinitely
Button(onClick = { navController.navigate("home") })

// ✅ Good - Manage back stack
Button(onClick = {
    navController.navigate("home") {
        popUpTo(navController.graph.startDestinationId)
    }
})
```

4. **Don't Create NavController in Regular Composables**

```kotlin
// ❌ Bad
@Composable
fun MyScreen() {
    val navController = rememberNavController()
}

// ✅ Good - Pass as parameter
@Composable
fun MyScreen(navController: NavHostController) {
    // Use passed navController
}
```

---

## 10. Complete Flow Diagrams

### User Journey Flows

#### 1. Authentication Flow

```
┌─────────────┐
│ App Launch  │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Check Auth  │◄─────────────┐
└──────┬──────┘              │
       │                     │
       ├─── Not Logged In    │
       │                     │
       ▼                     │
┌─────────────┐              │
│Login Screen │              │
└──────┬──────┘              │
       │                     │
       ├─── Email/Password   │
       │                     │
       ▼                     │
┌─────────────┐              │
│  API Call   │              │
└──────┬──────┘              │
       │                     │
       ├─── Success          │
       │                     │
       ▼                     │
┌─────────────┐              │
│  Main App   │──────────────┘
└─────────────┘
       │
       ├─── Register → OTP → Login
       │
       └─── Forgot Password → OTP → Reset → Login
```

#### 2. Shopping Flow

```
┌──────────┐
│   Home   │
└─────┬────┘
      │
      ▼
┌──────────────────┐
│ Browse Products  │
└─────┬────────────┘
      │
      ▼
┌──────────────────┐
│Product Details   │
└─────┬────────────┘
      │
      ├─── Add to Cart
      │
      ▼
┌──────────────────┐
│   Cart Screen    │
└─────┬────────────┘
      │
      ▼
┌──────────────────┐
│   Checkout       │
└─────┬────────────┘
      │
      ├─── Select/Add Address
      │
      ├─── Choose Payment
      │
      ▼
┌──────────────────┐
│  Place Order     │
└─────┬────────────┘
      │
      ▼
┌──────────────────┐
│ Order Success    │
└─────┬────────────┘
      │
      ▼
┌──────────────────┐
│Order Details     │
└──────────────────┘
```

#### 3. Bottom Navigation Flow

```
┌─────────┐  ┌────────────┐  ┌──────┐  ┌────────┐  ┌─────────┐
│  Home   │──│ Categories │──│ Cart │──│ Orders │──│ Profile │
└────┬────┘  └─────┬──────┘  └──┬───┘  └───┬────┘  └────┬────┘
     │             │             │          │            │
     │             │             │          │            │
     ▼             ▼             ▼          ▼            ▼
┌─────────┐  ┌────────────┐  ┌──────┐  ┌────────┐  ┌─────────┐
│Products │  │  Category  │  │Items │  │ Order  │  │Settings │
│ Search  │  │  Products  │  │      │  │Details │  │Addresses│
│         │  │            │  │      │  │        │  │Wishlist │
└─────────┘  └────────────┘  └──────┘  └────────┘  └─────────┘
```

### Back Stack Examples

#### Example 1: Product Browsing

```
Action: Home → Product Details → Cart → Checkout

Back Stack:
┌──────────────┐  ← Current
│  Checkout    │
├──────────────┤
│   Cart       │
├──────────────┤
│Product Detail│
├──────────────┤
│   Home       │
└──────────────┘

Back Navigation: Checkout → Cart → Product Detail → Home
```

#### Example 2: Bottom Navigation

```
Action: Home → Cart → Orders (via bottom nav)

Back Stack:
┌──────────────┐  ← Current
│   Orders     │
├──────────────┤
│   Home       │  (Cart removed by popUpTo)
└──────────────┘

Back Navigation: Orders → Home → Exit
```

#### Example 3: Deep Link

```
Deep Link: webby://product/123

Back Stack:
┌──────────────┐  ← Current (Created by deep link)
│Product Detail│
└──────────────┘

Back Navigation: Product Detail → Exit
```

---

## 📚 Summary Cheat Sheet

### Key Components

- **NavController**: Manages navigation
- **NavHost**: Container for destinations
- **Composable**: Individual screen
- **Route**: String identifier for destination

### Navigation Actions

```kotlin
// Forward
navController.navigate(route)

// Back
navController.navigateUp()
navController.popBackStack()

// With options
navController.navigate(route) {
    popUpTo(route) { inclusive = true }
    launchSingleTop = true
    restoreState = true
}
```

### Getting Current Route

```kotlin
val currentRoute = navController
    .currentBackStackEntryAsState()
    .value?.destination?.route
```

### Passing Data

```kotlin
// URL parameters
"product/{id}"

// SavedStateHandle
navController.previousBackStackEntry
    ?.savedStateHandle
    ?.set("key", value)
```

---

**This guide covers everything about navigation in the Webby E-Commerce app from scratch!** 🎯
