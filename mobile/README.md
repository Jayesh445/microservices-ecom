# Webby - E-Commerce Android App

A modern e-commerce Android application built with **Jetpack Compose** and **Kotlin**, following **Clean Architecture** principles. The app provides a complete shopping experience similar to Amazon and Flipkart.

## 🏗️ Architecture

The app follows **Clean Architecture** with clear separation of concerns:

### Project Structure

```
app/src/main/java/works/jayesh/webby/
│
├── MainActivity.kt                 # Entry point of the app
│
├── data/                           # Data layer
│   ├── remote/
│   │   └── dto/                    # Data Transfer Objects
│   │       ├── Common.kt           # ApiResponse, PageResponse
│   │       ├── AuthDto.kt          # Authentication DTOs
│   │       ├── ProductDto.kt       # Product DTOs
│   │       ├── CartDto.kt          # Cart DTOs
│   │       ├── OrderDto.kt         # Order DTOs
│   │       ├── AddressDto.kt       # Address DTOs
│   │       └── ReviewAndPaymentDto.kt
│   ├── local/                      # TODO: Local database (Room)
│   ├── repository/                 # TODO: Repository implementations
│   └── mapper/                     # TODO: Data mappers
│
├── domain/                         # Business logic layer
│   ├── model/
│   │   └── Models.kt               # Domain models (User, Product, Cart, Order, etc.)
│   ├── repository/                 # TODO: Repository interfaces
│   └── usecase/                    # TODO: Use cases
│
├── ui/                             # Presentation layer
│   ├── theme/                      # Material3 theming
│   │   ├── Color.kt                # Color definitions
│   │   ├── Type.kt                 # Typography
│   │   ├── Shape.kt                # Shapes (generated)
│   │   └── Theme.kt                # Main theme configuration
│   │
│   ├── components/                 # Reusable composables
│   │   ├── CommonComponents.kt     # Buttons, TextFields, etc.
│   │   └── ProductComponents.kt    # Product cards, rating bars
│   │
│   └── features/                   # Feature-based screens
│       ├── auth/
│       │   ├── LoginScreen.kt
│       │   ├── AuthState.kt
│       │   └── (RegisterScreen, OtpScreen - TODO)
│       │
│       ├── home/
│       │   └── HomeScreen.kt       # Main home with products
│       │
│       ├── cart/
│       │   └── CartScreen.kt       # Shopping cart
│       │
│       ├── orders/
│       │   └── OrdersScreen.kt     # Order history
│       │
│       ├── profile/
│       │   └── ProfileScreen.kt    # User profile
│       │
│       └── (product, checkout, address - TODO)
│
├── navigation/                     # Navigation setup
│   ├── Destinations.kt             # All route definitions
│   └── NavGraph.kt                 # Navigation graph
│
├── utils/                          # Utility classes
│   ├── Constants.kt                # App constants
│   ├── Resource.kt                 # Resource wrapper for API responses
│   └── Extensions.kt               # Extension functions
│
└── di/                             # TODO: Dependency Injection (Hilt/Koin)
    ├── AppModule.kt
    └── NetworkModule.kt
```

## ✨ Features Implemented

### UI Components ✅

- **Material3 Design System** with custom theming
- **Reusable Components**: Buttons, TextFields, SearchBar, Loading indicators
- **Product Cards**: Grid and list layouts with images, prices, ratings
- **Bottom Navigation** with 5 tabs
- **Status Bar** customization

### Screens Implemented ✅

1. **Authentication**

   - Login Screen (with password & OTP options)
   - Register Screen (placeholder)
   - OTP Verification (placeholder)

2. **Home**

   - Search bar
   - Category chips
   - Promotional banner
   - Featured products section
   - Best sellers section
   - Deals section

3. **Cart**

   - Item list with quantity controls
   - Price breakdown (subtotal, shipping, tax)
   - Empty cart state
   - Proceed to checkout

4. **Orders**

   - Order history list
   - Order status chips (color-coded)
   - Order item previews
   - Empty orders state

5. **Profile**
   - User information card
   - Quick stats (orders, wishlist)
   - Menu items for various settings
   - Logout functionality

### Data Models ✅

- User, Product, Category
- Cart, CartItem
- Order, OrderItem, OrderStatus
- Address, AddressType
- Review, Payment, PaymentMethod
- All models are **Parcelable** for navigation

### Resources ✅

- **strings.xml**: 190+ string resources
- **colors.xml**: 30+ color definitions for light/dark theme
- **dimens.xml**: Spacing, text sizes, component dimensions

## 🎨 Design Features

- **Material3 Color Scheme** with primary, secondary, surface colors
- **E-commerce specific colors**: discount red, price green, rating yellow, order status colors
- **Typography**: Complete Material3 text styles
- **Responsive layouts** with proper padding and spacing
- **Smooth animations** (implicit with Compose)

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Minimum SDK 24 (Android 7.0)
- Target SDK 35 (Android 14)
- Kotlin 1.9+

### Build & Run

1. Open project in Android Studio
2. Sync Gradle files
3. Run on emulator or device

## 📦 Dependencies

```kotlin
// Compose
androidx.compose.material3
androidx.navigation:navigation-compose

// Networking (for future integration)
com.squareup.retrofit2:retrofit
com.squareup.retrofit2:converter-gson
com.squareup.okhttp3:logging-interceptor

// Image Loading
io.coil-kt:coil-compose

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-android

// DataStore (for auth tokens)
androidx.datastore:datastore-preferences
```

## 🔄 Backend Integration (TODO)

The app is structured to work with the Spring Boot backend:

### API Endpoints to Integrate:

- **Auth**: `/api/auth/login`, `/api/auth/register`
- **Products**: `/api/products`, `/api/products/{id}`
- **Cart**: `/api/cart/user/{userId}`
- **Orders**: `/api/orders`, `/api/orders/user/{userId}`
- **Address**: `/api/addresses/user/{userId}`
- **Reviews**: `/api/reviews/product/{productId}`

### Next Steps:

1. Create `ApiService.kt` with Retrofit interfaces
2. Implement `Repository` layer
3. Add `UseCase` classes for business logic
4. Create `ViewModels` for each screen
5. Set up Hilt/Koin for dependency injection
6. Implement DataStore for token management
7. Add proper error handling
8. Connect UI screens to ViewModels

## 🎯 Remaining Screens (TODO)

- Product Details Screen
- Product List/Search Results
- Category Grid
- Checkout Flow
- Address Management (Add/Edit)
- Payment Method Selection
- Order Details & Tracking
- Write Review
- Wishlist
- Settings
- Edit Profile

## 🧪 Testing (TODO)

- Unit tests for ViewModels
- Repository tests
- UI tests with Compose Testing
- Integration tests

## 📱 Screenshots

(Add screenshots here once the app is running)

## 🤝 Contributing

This is a portfolio/learning project. Feel free to fork and modify.

## 📄 License

MIT License

## 👤 Author

Jayesh - [GitHub](https://github.com/yourusername)

---

**Note**: This app currently has UI only. Backend integration is pending.
