# Webby E-Commerce App - Complete Technology Documentation

## 📚 Table of Contents

1. [Kotlin Language Features](#kotlin-language-features)
2. [Jetpack Compose](#jetpack-compose)
3. [Android Architecture Components](#android-architecture-components)
4. [Annotations Reference](#annotations-reference)
5. [Material Design 3](#material-design-3)
6. [Navigation Component](#navigation-component)
7. [Networking Libraries](#networking-libraries)
8. [Dependency Injection](#dependency-injection)
9. [Asynchronous Programming](#asynchronous-programming)
10. [Data Persistence](#data-persistence)

---

## 1. Kotlin Language Features

### Keywords Used in This Project

#### `data class`

```kotlin
data class User(val id: Long, val name: String)
```

- **Purpose**: Automatically generates `equals()`, `hashCode()`, `toString()`, `copy()`, and component functions
- **Usage**: Used for DTOs and domain models throughout the app
- **Example**: `ProductDto`, `CartDto`, `User`, `Product`

#### `sealed class`

```kotlin
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}
```

- **Purpose**: Restricted class hierarchy representing a fixed set of types
- **Usage**: State management, navigation destinations, API responses
- **Benefits**: Exhaustive when expressions, type safety

#### `object` & `data object`

```kotlin
object Constants {
    const val BASE_URL = "http://10.0.2.2:8080/api/"
}

data object Loading : UiState<Nothing>()
```

- **Purpose**: Singleton pattern, single instance throughout the app
- **Usage**: Constants, utility classes, sealed class members
- **Difference**: `data object` includes `toString()`, `equals()`, `hashCode()`

#### `companion object`

```kotlin
class MyClass {
    companion object {
        const val TAG = "MyClass"
        fun create(): MyClass = MyClass()
    }
}
```

- **Purpose**: Static members within a class
- **Usage**: Factory methods, constants related to the class

#### `enum class`

```kotlin
enum class OrderStatus {
    PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}
```

- **Purpose**: Fixed set of constants with type safety
- **Usage**: Order status, payment methods, address types

#### `const val` vs `val`

```kotlin
const val COMPILE_TIME = "Known at compile time"  // Compile-time constant
val RUNTIME = System.currentTimeMillis()           // Runtime constant
```

- **const val**: Inlined at compile time, primitive types and String only
- **val**: Immutable variable, evaluated at runtime

#### Extension Functions

```kotlin
fun Double.toRupees(): String = "₹${String.format("%.2f", this)}"

val price = 199.99
println(price.toRupees())  // Output: ₹199.99
```

- **Purpose**: Add functions to existing classes without inheritance
- **Usage**: `Extensions.kt` contains formatting and validation extensions

#### Lambda Expressions

```kotlin
Button(onClick = { /* Lambda */ }) { Text("Click") }

val numbers = listOf(1, 2, 3)
numbers.filter { it > 1 }  // 'it' is implicit parameter
```

- **Purpose**: Anonymous functions, higher-order functions
- **Usage**: Event handlers, callbacks, functional operations

#### Trailing Lambda

```kotlin
Button(onClick = { }) {  // Last lambda moved outside parentheses
    Text("Click Me")
}
```

- **Purpose**: Cleaner syntax when last parameter is a lambda
- **Usage**: All Compose functions (Button, Column, LazyColumn, etc.)

#### `by` Delegation

```kotlin
val viewModel: MyViewModel by viewModel()
var state by remember { mutableStateOf("") }
```

- **Purpose**: Property delegation, delegates getter/setter to another object
- **Usage**: ViewModel injection, Compose state management

#### Nullable Types & Safe Calls

```kotlin
var name: String? = null
val length = name?.length ?: 0  // Safe call with elvis operator
```

- **`?`**: Nullable type
- **`?.`**: Safe call operator
- **`?:`**: Elvis operator (default value)
- **`!!`**: Non-null assertion (use sparingly!)

#### Scope Functions

```kotlin
// let - execute lambda on non-null
user?.let { println(it.name) }

// apply - configure object
val user = User().apply { name = "John" }

// also - side effects
val list = mutableListOf<String>().also { println("Created") }

// run - execute lambda and return result
val result = user.run { "$firstName $lastName" }

// with - similar to run but not an extension
with(user) { println(name) }
```

#### Type Aliases

```kotlin
typealias ProductList = List<Product>
typealias OnClick = () -> Unit
```

- **Purpose**: Create alternative names for existing types
- **Usage**: Simplify complex type signatures

---

## 2. Jetpack Compose

### Core Concepts

#### Composable Functions

```kotlin
@Composable
fun MyScreen() {
    Text("Hello World")
}
```

- **`@Composable` annotation**: Marks function as UI component
- **Declarative UI**: Describe what the UI should look like
- **No XML**: Pure Kotlin code

#### State Management

##### `remember`

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count++ }) {
        Text("Count: $count")
    }
}
```

- **Purpose**: Store state across recompositions
- **Lifecycle**: Survives recomposition but not configuration changes
- **Usage**: Local UI state (text field input, expanded state)

##### `rememberSaveable`

```kotlin
var text by rememberSaveable { mutableStateOf("") }
```

- **Purpose**: Survives configuration changes (rotation, etc.)
- **Usage**: Important state that should persist

##### `mutableStateOf`

```kotlin
val state = mutableStateOf(initialValue)
state.value = newValue  // Update

// Or with delegation
var state by mutableStateOf(initialValue)
state = newValue  // Update
```

- **Purpose**: Observable state for Compose
- **Behavior**: UI recomposes when value changes

##### `derivedStateOf`

```kotlin
val filteredItems by remember(items, query) {
    derivedStateOf {
        items.filter { it.contains(query) }
    }
}
```

- **Purpose**: Compute state from other state
- **Optimization**: Recalculates only when dependencies change

#### Compose Annotations

##### `@Composable`

- **Required** for all UI functions
- **Enables**: Compose compiler optimizations
- **Restriction**: Can only be called from other @Composable functions

##### `@Preview`

```kotlin
@Preview(showBackground = true)
@Composable
fun MyScreenPreview() {
    MyScreen()
}
```

- **Purpose**: Show preview in Android Studio
- **Parameters**: `showBackground`, `uiMode`, `device`, `fontScale`

##### `@OptIn(ExperimentalMaterial3Api::class)`

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen() {
    TopAppBar(title = { Text("Title") })
}
```

- **Purpose**: Use experimental APIs
- **Warning**: API may change in future versions

#### Layout Composables

##### `Column` - Vertical Layout

```kotlin
Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text("Item 1")
    Text("Item 2")
}
```

##### `Row` - Horizontal Layout

```kotlin
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Text("Left")
    Text("Right")
}
```

##### `Box` - Stack Layout

```kotlin
Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
) {
    Image(...)  // Background
    Text("Overlay")  // Foreground
}
```

##### `LazyColumn` - Vertical Scrolling List

```kotlin
LazyColumn {
    items(products) { product ->
        ProductCard(product)
    }
}
```

- **Purpose**: Efficient list with recycling (like RecyclerView)
- **Lazy**: Items created on demand

##### `LazyRow` - Horizontal Scrolling List

```kotlin
LazyRow {
    items(categories) { category ->
        CategoryChip(category)
    }
}
```

#### Modifier System

```kotlin
Modifier
    .fillMaxSize()          // Fill entire parent
    .fillMaxWidth()         // Fill width only
    .fillMaxHeight()        // Fill height only
    .size(100.dp)           // Fixed size
    .width(200.dp)          // Fixed width
    .height(50.dp)          // Fixed height
    .padding(16.dp)         // All sides
    .padding(horizontal = 16.dp, vertical = 8.dp)
    .background(Color.Red)  // Background color
    .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
    .clickable { }          // Click handler
    .clip(RoundedCornerShape(12.dp))  // Clip shape
    .weight(1f)             // Flex weight in Row/Column
    .align(Alignment.Center) // Alignment in Box
    .shadow(4.dp)           // Elevation shadow
```

**Modifier Order Matters!**

```kotlin
// Padding inside background
Modifier.background(Color.Red).padding(16.dp)

// Padding outside background
Modifier.padding(16.dp).background(Color.Red)
```

#### Side Effects

##### `LaunchedEffect`

```kotlin
LaunchedEffect(key1) {
    // Coroutine launched when key1 changes
    viewModel.loadData()
}
```

- **Purpose**: Launch coroutine tied to composable lifecycle
- **Cancellation**: Automatically cancelled when composable leaves composition

##### `DisposableEffect`

```kotlin
DisposableEffect(Unit) {
    val listener = setupListener()
    onDispose {
        removeListener(listener)
    }
}
```

- **Purpose**: Cleanup side effects
- **Usage**: Event listeners, subscriptions

##### `SideEffect`

```kotlin
SideEffect {
    // Execute on every successful recomposition
    analytics.log("Screen viewed")
}
```

- **Purpose**: Non-compose code that should run after recomposition

---

## 3. Android Architecture Components

### Navigation Component

#### NavController

```kotlin
val navController = rememberNavController()
```

- **Purpose**: Manage navigation between destinations
- **Functions**: `navigate()`, `navigateUp()`, `popBackStack()`

#### NavHost

```kotlin
NavHost(
    navController = navController,
    startDestination = "home"
) {
    composable("home") { HomeScreen() }
    composable("details/{id}") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id")
        DetailsScreen(id)
    }
}
```

- **Purpose**: Container for navigation graph
- **Routes**: String-based destinations

#### Route Arguments

```kotlin
// Define route with argument
composable(
    route = "product/{productId}",
    arguments = listOf(navArgument("productId") { type = NavType.LongType })
) { backStackEntry ->
    val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
    ProductDetailsScreen(productId)
}

// Navigate with argument
navController.navigate("product/123")
```

### ViewModel (Coming Soon)

```kotlin
class HomeViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    fun loadProducts() {
        viewModelScope.launch {
            _products.value = repository.getProducts()
        }
    }
}
```

- **Purpose**: Manage UI state, survive configuration changes
- **Lifecycle**: Survives rotation, killed when activity finishes

---

## 4. Annotations Reference

### Kotlin Annotations

#### `@Parcelize`

```kotlin
@Parcelize
data class User(val id: Long, val name: String) : Parcelable
```

- **Plugin**: `kotlin-parcelize`
- **Purpose**: Auto-generate Parcelable implementation
- **Usage**: Pass objects between screens via navigation

#### `@SerializedName`

```kotlin
data class ProductDto(
    @SerializedName("product_id")
    val productId: Long,

    @SerializedName("product_name")
    val productName: String
)
```

- **Library**: Gson
- **Purpose**: Map JSON field names to Kotlin property names
- **Usage**: All DTOs for API communication

### Compose Annotations

#### `@Composable`

- Marks composable functions

#### `@Preview`

- Preview in Android Studio

#### `@OptIn`

- Use experimental APIs

### Android Annotations (Common)

#### `@StringRes`, `@ColorRes`, `@DrawableRes`

```kotlin
fun showMessage(@StringRes messageId: Int) {
    getString(messageId)
}
```

- **Purpose**: Type safety for resource IDs

#### `@MainThread`, `@WorkerThread`

```kotlin
@MainThread
fun updateUI() { }

@WorkerThread
fun doNetworkCall() { }
```

- **Purpose**: Document threading requirements

---

## 5. Material Design 3

### Components Used

#### `Button`

```kotlin
Button(
    onClick = { },
    modifier = Modifier.fillMaxWidth(),
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
    )
) {
    Text("Click Me")
}
```

#### `OutlinedButton`

```kotlin
OutlinedButton(onClick = { }) {
    Text("Outlined")
}
```

#### `TextField` vs `OutlinedTextField`

```kotlin
OutlinedTextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("Email") },
    singleLine = true,
    isError = hasError,
    supportingText = { Text("Error message") }
)
```

#### `Card`

```kotlin
Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )
) {
    // Content
}
```

#### `Scaffold`

```kotlin
Scaffold(
    topBar = { TopAppBar(...) },
    bottomBar = { BottomNavigationBar(...) },
    floatingActionButton = { FloatingActionButton(...) }
) { paddingValues ->
    // Content with paddingValues applied
}
```

#### `TopAppBar`

```kotlin
TopAppBar(
    title = { Text("Title") },
    navigationIcon = {
        IconButton(onClick = { }) {
            Icon(Icons.Default.ArrowBack, "Back")
        }
    },
    actions = {
        IconButton(onClick = { }) {
            Icon(Icons.Default.Search, "Search")
        }
    }
)
```

#### `NavigationBar` (Bottom Navigation)

```kotlin
NavigationBar {
    items.forEach { item ->
        NavigationBarItem(
            icon = { Icon(item.icon, item.label) },
            label = { Text(item.label) },
            selected = currentRoute == item.route,
            onClick = { navController.navigate(item.route) }
        )
    }
}
```

### Material Theme

#### ColorScheme

```kotlin
MaterialTheme.colorScheme.primary
MaterialTheme.colorScheme.secondary
MaterialTheme.colorScheme.background
MaterialTheme.colorScheme.surface
MaterialTheme.colorScheme.error
MaterialTheme.colorScheme.onPrimary  // Text on primary color
```

#### Typography

```kotlin
MaterialTheme.typography.displayLarge
MaterialTheme.typography.headlineMedium
MaterialTheme.typography.titleLarge
MaterialTheme.typography.bodyMedium
MaterialTheme.typography.labelSmall
```

#### Shapes

```kotlin
MaterialTheme.shapes.small       // 4.dp
MaterialTheme.shapes.medium      // 8.dp
MaterialTheme.shapes.large       // 16.dp
```

---

## 6. Navigation Component

### Destination Definition

```kotlin
sealed class Destinations(val route: String) {
    data object Home : Destinations("home")
    data object Cart : Destinations("cart")

    data object ProductDetails : Destinations("product/{productId}") {
        fun createRoute(productId: Long) = "product/$productId"
    }
}
```

### Navigation Operations

#### Navigate Forward

```kotlin
navController.navigate("details")
```

#### Navigate with Arguments

```kotlin
navController.navigate(Destinations.ProductDetails.createRoute(123))
```

#### Navigate Back

```kotlin
navController.navigateUp()
// or
navController.popBackStack()
```

#### Navigate with Pop

```kotlin
navController.navigate("home") {
    popUpTo("login") { inclusive = true }
    launchSingleTop = true
}
```

- **`popUpTo`**: Remove destinations from back stack
- **`inclusive`**: Include the popUpTo destination in removal
- **`launchSingleTop`**: Don't create new instance if already on top

#### Save/Restore State

```kotlin
navController.navigate(route) {
    popUpTo(startDestination) { saveState = true }
    launchSingleTop = true
    restoreState = true
}
```

- **`saveState`**: Save state when popping
- **`restoreState`**: Restore state when navigating back

### Back Stack Entry

```kotlin
val backStackEntry = navController.currentBackStackEntryAsState()
val currentRoute = backStackEntry.value?.destination?.route

// Get arguments
val productId = backStackEntry?.arguments?.getLong("productId")
```

---

## 7. Networking Libraries

### Retrofit (Setup - Not Yet Integrated)

#### Interface Definition

```kotlin
interface ApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PageResponse<ProductDto>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: Long): Response<ProductDto>
}
```

#### Annotations

- **`@GET`, `@POST`, `@PUT`, `@DELETE`, `@PATCH`**: HTTP methods
- **`@Query`**: Query parameter (?page=1)
- **`@Path`**: Path parameter (/products/{id})
- **`@Body`**: Request body (JSON)
- **`@Header`**: HTTP header
- **`@Headers`**: Multiple headers

#### Retrofit Instance

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()

val apiService = retrofit.create(ApiService::class.java)
```

### OkHttp

#### Logging Interceptor

```kotlin
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()
```

### Gson (JSON Serialization)

```kotlin
data class UserDto(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("full_name") val fullName: String
)
```

### Coil (Image Loading)

#### AsyncImage

```kotlin
AsyncImage(
    model = imageUrl,
    contentDescription = "Product image",
    modifier = Modifier.size(100.dp),
    contentScale = ContentScale.Crop,
    placeholder = painterResource(R.drawable.placeholder),
    error = painterResource(R.drawable.error)
)
```

---

## 8. Dependency Injection (Coming Soon)

### Hilt (Recommended)

#### Application Class

```kotlin
@HiltAndroidApp
class MyApplication : Application()
```

#### Module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
```

#### ViewModel Injection

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    // ...
}

// In Composable
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    // ...
}
```

---

## 9. Asynchronous Programming

### Coroutines

#### Suspend Functions

```kotlin
suspend fun fetchData(): List<Product> {
    return withContext(Dispatchers.IO) {
        apiService.getProducts()
    }
}
```

- **`suspend`**: Can be paused and resumed
- **Non-blocking**: Doesn't block thread while waiting

#### Coroutine Scope

```kotlin
// ViewModel
viewModelScope.launch {
    val data = repository.getData()
}

// Composable
LaunchedEffect(key) {
    val data = repository.getData()
}
```

#### Dispatchers

```kotlin
Dispatchers.Main       // UI thread
Dispatchers.IO         // Network, disk I/O
Dispatchers.Default    // CPU-intensive work
```

#### `async` & `await`

```kotlin
val deferred1 = async { fetchData1() }
val deferred2 = async { fetchData2() }

val result1 = deferred1.await()
val result2 = deferred2.await()
```

#### Flow

```kotlin
val productsFlow: Flow<List<Product>> = flow {
    emit(repository.getProducts())
}

// In Composable
val products by productsFlow.collectAsState(initial = emptyList())
```

---

## 10. Data Persistence

### DataStore (Preferences)

#### Setup

```kotlin
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object PreferencesKeys {
    val AUTH_TOKEN = stringPreferencesKey("auth_token")
    val USER_ID = longPreferencesKey("user_id")
}
```

#### Write

```kotlin
suspend fun saveToken(token: String) {
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.AUTH_TOKEN] = token
    }
}
```

#### Read

```kotlin
val tokenFlow: Flow<String?> = context.dataStore.data
    .map { preferences ->
        preferences[PreferencesKeys.AUTH_TOKEN]
    }
```

### Room Database (For Offline Support - Future)

#### Entity

```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "product_name") val name: String,
    val price: Double
)
```

#### DAO

```kotlin
@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}
```

---

## 📝 Common Patterns in This App

### 1. State Hoisting

```kotlin
@Composable
fun ParentScreen() {
    var text by remember { mutableStateOf("") }

    ChildComponent(
        text = text,
        onTextChange = { text = it }
    )
}

@Composable
fun ChildComponent(
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(value = text, onValueChange = onTextChange)
}
```

### 2. Unidirectional Data Flow

```
User Action → Event → ViewModel → Update State → Recompose UI
```

### 3. Repository Pattern (Coming)

```kotlin
class ProductRepository(
    private val apiService: ApiService,
    private val productDao: ProductDao
) {
    suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = apiService.getProducts()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 4. UseCase Pattern (Coming)

```kotlin
class GetProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Result<List<Product>> {
        return repository.getProducts()
    }
}
```

---

## 🎯 Quick Reference

### Most Used Compose Functions

- `Text()` - Display text
- `Button()` - Clickable button
- `TextField()` - Text input
- `Image()` / `AsyncImage()` - Display images
- `Column()` - Vertical layout
- `Row()` - Horizontal layout
- `Box()` - Stack layout
- `LazyColumn()` - Scrollable list
- `Spacer()` - Empty space
- `Divider()` - Horizontal line
- `Card()` - Material card
- `Icon()` - Icon display
- `Scaffold()` - App structure

### Most Used Modifiers

- `.fillMaxSize()`, `.fillMaxWidth()`, `.fillMaxHeight()`
- `.size()`, `.width()`, `.height()`
- `.padding()`
- `.background()`
- `.clickable()`
- `.weight()`

### Most Used Icons

```kotlin
Icons.Default.Home
Icons.Default.Search
Icons.Default.ShoppingCart
Icons.Default.Person
Icons.Default.ArrowBack
Icons.Filled.Star
Icons.Outlined.FavoriteBorder
```

---

## 📖 Learning Resources

1. **Jetpack Compose**: https://developer.android.com/jetpack/compose
2. **Kotlin Docs**: https://kotlinlang.org/docs/home.html
3. **Material 3**: https://m3.material.io/
4. **Coroutines**: https://kotlinlang.org/docs/coroutines-overview.html
5. **Navigation**: https://developer.android.com/jetpack/compose/navigation

---

**This documentation covers all major concepts, keywords, and annotations used in the Webby E-Commerce app!** 🚀
