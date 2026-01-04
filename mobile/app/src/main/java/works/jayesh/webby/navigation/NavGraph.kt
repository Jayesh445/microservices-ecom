package works.jayesh.webby.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import works.jayesh.webby.ui.features.address.AddAddressScreen
import works.jayesh.webby.ui.features.address.AddressListScreen
import works.jayesh.webby.ui.features.address.EditAddressScreen
import works.jayesh.webby.ui.features.auth.LoginScreen
import works.jayesh.webby.ui.features.auth.OtpScreen
import works.jayesh.webby.ui.features.auth.RegisterScreen
import works.jayesh.webby.ui.features.cart.CartScreen
import works.jayesh.webby.ui.features.category.CategoryScreen
import works.jayesh.webby.ui.features.checkout.CheckoutScreen
import works.jayesh.webby.ui.features.home.HomeScreen
import works.jayesh.webby.ui.features.order.OrderDetailsScreen
import works.jayesh.webby.ui.features.order.OrderTrackingScreen
import works.jayesh.webby.ui.features.orders.OrdersScreen
import works.jayesh.webby.ui.features.payment.PaymentMethodScreen
import works.jayesh.webby.ui.features.product.ProductDetailsScreen
import works.jayesh.webby.ui.features.product.ProductListScreen
import works.jayesh.webby.ui.features.product.SearchScreen
import works.jayesh.webby.ui.features.profile.EditProfileScreen
import works.jayesh.webby.ui.features.profile.ProfileScreen
import works.jayesh.webby.ui.features.review.ReviewListScreen
import works.jayesh.webby.ui.features.review.WriteReviewScreen
import works.jayesh.webby.ui.features.settings.SettingsScreen
import works.jayesh.webby.ui.features.wishlist.WishlistScreen

@Composable
fun WebbyApp(onToggleTheme: () -> Unit = {}) {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) } // TODO: Connect to auth state

    if (isLoggedIn) {
        MainScreen(navController = navController, onToggleTheme = onToggleTheme)
    } else {
        AuthNavHost(navController = navController, onLoginSuccess = { isLoggedIn = true })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, onToggleTheme: () -> Unit = {}) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar =
            remember(currentRoute) {
                listOf(
                                Destinations.Home.route,
                                Destinations.CategoryList.route,
                                Destinations.Cart.route,
                                Destinations.Orders.route,
                                Destinations.Profile.route
                        )
                        .contains(currentRoute)
            }

    Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(navController = navController)
                }
            },
        contentWindowInsets = WindowInsets(top = 0)
    ) { paddingValues ->
        MainNavHost(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                onToggleTheme = onToggleTheme
        )
    }
}

@Composable
fun AuthNavHost(navController: NavHostController, onLoginSuccess: () -> Unit) {
    NavHost(navController = navController, startDestination = Destinations.Login.route) {
        composable(Destinations.Login.route) {
            LoginScreen(
                    onLoginSuccess = onLoginSuccess,
                    onNavigateToRegister = { navController.navigate(Destinations.Register.route) },
                    onNavigateToOtp = {
                        navController.navigate(Destinations.Otp.createRoute("user@example.com"))
                    }
            )
        }

        composable(Destinations.Register.route) {
            RegisterScreen(
                modifier = Modifier,
                onNavigateBack = {navController.navigateUp()},
                onRegisterSuccess = {onLoginSuccess()}
            )
        }

        composable(
                route = Destinations.Otp.route,
                arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) {
            val email = navController.currentBackStackEntry?.arguments?.getString("email")
            OtpScreen(
                onNavigateBack = {navController.navigateUp()},
                email=email.orEmpty(),
                onVerifySuccess = {/* vnrnunvinern*/}
            )
        }
    }
}

@Composable
fun MainNavHost(
        navController: NavHostController,
        modifier: Modifier = Modifier,
        onToggleTheme: () -> Unit = {}
) {
    NavHost(
            navController = navController,
            startDestination = Destinations.Home.route,
            modifier = modifier
    ) {
        // Home & Search
        composable(Destinations.Home.route) {
            HomeScreen(
                    onNavigateToProduct = { productId ->
                        navController.navigate(Destinations.ProductDetails.createRoute(productId))
                    },
                    onNavigateToCart = { navController.navigate(Destinations.Cart.route) },
                    onNavigateToSearch = { navController.navigate(Destinations.Search.route) }
            )
        }

        composable(Destinations.Search.route) {
            SearchScreen(
                onNavigateBack = {navController.navigateUp()},
                onNavigateToProduct = { productId ->
                    navController.navigate(Destinations.ProductDetails.createRoute(productId))},
            )
        }

        // Categories
        composable(Destinations.CategoryList.route) {
            CategoryScreen(
                onNavigateToCategoryProducts = {
                    navController.navigate(Destinations.ProductList.createRoute(it))
                }
            )
        }

        // Products
        composable(
                route = Destinations.ProductDetails.route,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) {
            ProductDetailsScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToCart = { navController.navigate(Destinations.Cart.route) },
                    onNavigateToReviews = {
                    navController.navigate(Destinations.ReviewList.createRoute(1))
                }
            )
        }

        composable(Destinations.ProductList.route) {
            ProductListScreen(
                onNavigateBack = { navController.navigateUp() },
                onProductClick = { productId ->
                    navController.navigate(Destinations.ProductDetails.createRoute(productId))
                },
                onToggleFavorite = { /* Handle favorite */},
                categoryId = 1,
                categoryName = "Test Category"
            )
        }

        // Cart & Checkout
        composable(Destinations.Cart.route) {
            CartScreen(
                    onNavigateToCheckout = { navController.navigate(Destinations.Checkout.route) },
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToHome = {
                        navController.navigate(Destinations.Home.route) {
                            popUpTo(Destinations.Home.route) { inclusive = true }
                        }
                    }
            )
        }

        composable(Destinations.Checkout.route) {
            CheckoutScreen(
                onNavigateBack = {navController.navigateUp()},
                onNavigateToAddressSelection = {navController.navigate(Destinations.AddressSelection.route)},
                onNavigateToPaymentMethod = {navController.navigate(Destinations.PaymentMethod.route)},
                onPlaceOrder = {/* to be implement*/},
            )
        }

        composable(Destinations.AddressSelection.route) {
            AddressListScreen(
                onNavigateBack = {navController.navigateUp()},
                onNavigateToAddAddress = {navController.navigate(Destinations.AddressAdd.route)},
                onNavigateToEditAddress = { addressId ->
                    navController.navigate(Destinations.AddressEdit.createRoute(addressId))
                },
            )
        }

        composable(Destinations.AddressAdd.route) {
            AddAddressScreen(
                onNavigateBack = {navController.navigateUp()},
                onSaveAddress = {/* to be implement*/},
            )
        }

        composable(
                route = Destinations.AddressEdit.route,
                arguments = listOf(navArgument("addressId") { type = NavType.LongType })
        ) {
            val adressId = navController.currentBackStackEntry?.arguments?.getLong("addressId")
            EditAddressScreen(
                onNavigateBack = {navController.navigateUp()},
                onUpdateAddress = {/* to be implement*/},
                addressId = adressId ?: 1
            )
        }

        composable(Destinations.PaymentMethod.route) {
            PaymentMethodScreen(
                onNavigateBack = {navController.navigateUp()},
                onPaymentSelected = {/* to be implement*/},
            )
        }

        // Orders
        composable(Destinations.Orders.route) {
            OrdersScreen(
                    onNavigateToOrderDetails = { orderId ->
                        navController.navigate(Destinations.OrderDetails.createRoute(orderId))
                    }
            )
        }

        composable(
                route = Destinations.OrderDetails.route,
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) {
            val orderId = navController.currentBackStackEntry?.arguments?.getLong("orderId")
            OrderDetailsScreen(
                onNavigateBack = {navController.navigateUp()},
                onTrackOrder = {
                    navController.navigate(Destinations.OrderTracking.createRoute(orderId = orderId?:1))
                },
                orderId = orderId ?: 1,
                onDownloadInvoice = {/* to be implement*/},
            )
        }

        composable(
                route = Destinations.OrderTracking.route,
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) {
            val orderId = navController.currentBackStackEntry?.arguments?.getLong("orderId")
            OrderTrackingScreen(
                onNavigateBack = {navController.navigateUp()},
                orderId = orderId.toString(),
//                onDownloadInvoice = {/* to be implement*/},
            )
        }

        // Profile
        composable(Destinations.Profile.route) {
            ProfileScreen(
                    onNavigateToEditProfile = {
                        navController.navigate(Destinations.EditProfile.route)
                    },
                    onNavigateToOrders = { navController.navigate(Destinations.Orders.route) },
                    onNavigateToAddresses = {
                        navController.navigate(Destinations.AddressList.route)
                    },
                    onNavigateToWishlist = { navController.navigate(Destinations.Wishlist.route) },
                    onNavigateToSettings = { navController.navigate(Destinations.Settings.route) },
                    onToggleTheme = onToggleTheme,
                    onLogout = {
                        // TODO: Handle logout
                    }
            )
        }

        composable(Destinations.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = {navController.navigateUp()},
                onSaveProfile = {/* to be implement */},
            )
        }

        composable(Destinations.AddressList.route) {
            AddressListScreen(
                onNavigateBack = {navController.navigateUp()},
                onNavigateToAddAddress = {
                    navController.navigate(Destinations.AddressAdd.route)
                },
                onNavigateToEditAddress = {
                    navController.navigate(Destinations.AddressEdit.createRoute(it))
                }
            )
        }

        composable(Destinations.Wishlist.route) {
            WishlistScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToProduct = { productId ->
                        navController.navigate(Destinations.ProductDetails.createRoute(productId))
                    },
            )
        }

        composable(Destinations.Settings.route) {
            SettingsScreen(
                    onNavigateBack = { navController.navigateUp() },
            )
        }

        // Reviews
        composable(
                route = Destinations.WriteReview.route,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) {
            val productId = navController.currentBackStackEntry?.arguments?.getLong("productId")
            WriteReviewScreen(
                onNavigateBack = {navController.navigateUp()},
                onSubmitReview = {/* TODO : to complete next */},
                productId = productId ?: 1,
                productName = "Test Product"
            )
        }

        composable(
                route = Destinations.ReviewList.route,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) {
            val productId = navController.currentBackStackEntry?.arguments?.getLong("productId")
            ReviewListScreen(
                onNavigateBack = {navController.navigateUp()},
                productId = productId ?: 1,
                onWriteReview = {
                    navController.navigate(Destinations.WriteReview.createRoute(productId = productId?:1))
                },
                averageRating = 5.0f,
                productName = "Test Product",
                totalReviews = 100
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items =
            listOf(
                    Triple(Destinations.Home.route, "Home", Icons.Default.Home),
                    Triple(Destinations.CategoryList.route, "Categories", Icons.Default.Category),
                    Triple(Destinations.Cart.route, "Cart", Icons.Default.ShoppingCart),
                    Triple(Destinations.Orders.route, "Orders", Icons.Default.Receipt),
                    Triple(Destinations.Profile.route, "Profile", Icons.Default.Person)
            )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label) },
                    selected = currentRoute == route,
                    onClick = {
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(Destinations.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
        title: String,
        navController: NavHostController,
        onSuccess: (() -> Unit)? = null
) {
    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text(title) },
                        navigationIcon = {
                            if (navController.previousBackStackEntry != null) {
                                IconButton(onClick = { navController.navigateUp() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                                }
                            }
                        }
                )
            }
    ) { padding ->
        Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$title Screen", style = MaterialTheme.typography.headlineMedium)
                if (onSuccess != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onSuccess) { Text("Continue") }
                }
            }
        }
    }
}
