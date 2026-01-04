package works.jayesh.demo.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import works.jayesh.demo.address.model.entity.Address;
import works.jayesh.demo.address.model.entity.AddressType;
import works.jayesh.demo.address.repository.AddressRepository;
import works.jayesh.demo.cart.model.entity.Cart;
import works.jayesh.demo.cart.model.entity.CartItem;
import works.jayesh.demo.cart.repository.CartItemRepository;
import works.jayesh.demo.cart.repository.CartRepository;
import works.jayesh.demo.cart.service.CartService;
import works.jayesh.demo.order.model.entity.OrderStatus;
import works.jayesh.demo.order.repository.OrderRepository;
import works.jayesh.demo.product.model.entity.Category;
import works.jayesh.demo.product.model.entity.Product;
import works.jayesh.demo.product.model.entity.ProductStatus;
import works.jayesh.demo.product.repository.CategoryRepository;
import works.jayesh.demo.product.repository.ProductRepository;
import works.jayesh.demo.user.model.entity.User;
import works.jayesh.demo.user.model.entity.UserRole;
import works.jayesh.demo.user.model.entity.UserStatus;
import works.jayesh.demo.user.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive Integration Test - E-Commerce Complete Flow
 * Tests real database operations without mocking
 * Flow: User Registration → Product Browsing → Add to Cart → Order Creation
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("E-Commerce Complete Flow Integration Tests")
class ECommerceFlowIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testCustomer;
    private User testSeller;
    private Product testProduct;
    private Category testCategory;
    private Address shippingAddress;

    @BeforeEach
    void setUp() {
        // Create test customer
        testCustomer = User.builder()
                .firstName("John")
                .lastName("Customer")
                .email("customer" + System.currentTimeMillis() + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .phoneNumber("1234567890")
                .role(UserRole.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .phoneVerified(false)
                .twoFactorEnabled(false)
                .build();
        testCustomer = userRepository.save(testCustomer);

        // Create test seller
        testSeller = User.builder()
                .firstName("Jane")
                .lastName("Seller")
                .email("seller" + System.currentTimeMillis() + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .phoneNumber("9876543210")
                .role(UserRole.SELLER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .phoneVerified(false)
                .twoFactorEnabled(false)
                .build();
        testSeller = userRepository.save(testSeller);

        // Create test category
        testCategory = Category.builder()
                .name("Electronics " + System.currentTimeMillis())
                .slug("electronics-" + System.currentTimeMillis())
                .description("Electronic products")
                .active(true)
                .displayOrder(1)
                .build();
        testCategory = categoryRepository.save(testCategory);

        // Create test product
        testProduct = Product.builder()
                .name("Smartphone X")
                .sku("PHONE-" + System.currentTimeMillis())
                .description("Latest smartphone")
                .shortDescription("Best smartphone 2026")
                .price(new BigDecimal("999.99"))
                .stockQuantity(50)
                .slug("smartphone-" + System.currentTimeMillis())
                .category(testCategory)
                .seller(testSeller)
                .brand("TechBrand")
                .active(true)
                .featured(true)
                .status(ProductStatus.ACTIVE)
                .totalReviews(0)
                .totalSold(0)
                .build();
        testProduct = productRepository.save(testProduct);

        // Create shipping address
        shippingAddress = Address.builder()
                .user(testCustomer)
                .fullName("John Customer")
                .phoneNumber("1234567890")
                .addressLine1("123 Main Street")
                .addressLine2("Apt 4B")
                .city("New York")
                .state("NY")
                .country("USA")
                .postalCode("10001")
                .type(AddressType.HOME)
                .isDefault(true)
                .build();
        shippingAddress = addressRepository.save(shippingAddress);

        // Create cart for customer
        Cart cart = Cart.builder()
                .user(testCustomer)
                .build();
        cartRepository.save(cart);
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("Flow 1: User can be created and retrieved from database")
    void testUserCreationFlow() {
        // Verify customer exists
        User foundCustomer = userRepository.findByEmail(testCustomer.getEmail()).orElse(null);
        assertThat(foundCustomer).isNotNull();
        assertThat(foundCustomer.getFirstName()).isEqualTo("John");
        assertThat(foundCustomer.getRole()).isEqualTo(UserRole.CUSTOMER);

        // Verify seller exists
        User foundSeller = userRepository.findByEmail(testSeller.getEmail()).orElse(null);
        assertThat(foundSeller).isNotNull();
        assertThat(foundSeller.getRole()).isEqualTo(UserRole.SELLER);
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Flow 2: Product can be browsed by category")
    void testProductBrowsingFlow() {
        // Find products by category
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productsInCategory = productRepository.findByCategoryId(testCategory.getId(), pageable);

        assertThat(productsInCategory.getContent()).isNotEmpty();
        assertThat(productsInCategory.getContent()).anyMatch(p -> p.getName().equals("Smartphone X"));
        assertThat(productsInCategory.getContent().get(0).getPrice()).isEqualByComparingTo(new BigDecimal("999.99"));
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("Flow 3: Product can be added to cart")
    void testAddToCartFlow() {
        // Get customer's cart
        Cart cart = cartRepository.findByUserId(testCustomer.getId()).orElse(null);
        assertThat(cart).isNotNull();

        // Add product to cart
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(testProduct)
                .quantity(2)
                .price(testProduct.getPrice())
                .build();
        cartItem = cartItemRepository.save(cartItem);

        // Verify cart item was added
        assertThat(cartItem.getId()).isNotNull();
        assertThat(cartItem.getQuantity()).isEqualTo(2);
        assertThat(cartItem.getProduct().getName()).isEqualTo("Smartphone X");

        // Verify cart total
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        BigDecimal total = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(total).isEqualByComparingTo(new BigDecimal("1999.98")); // 999.99 * 2
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("Flow 4: Cart item quantity can be updated")
    void testUpdateCartItemQuantityFlow() {
        // Add product to cart
        Cart cart = cartRepository.findByUserId(testCustomer.getId()).orElse(null);
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(testProduct)
                .quantity(1)
                .price(testProduct.getPrice())
                .build();
        cartItem = cartItemRepository.save(cartItem);

        // Update quantity
        cartItem.setQuantity(5);
        cartItem = cartItemRepository.save(cartItem);

        // Verify update
        CartItem updatedItem = cartItemRepository.findById(cartItem.getId()).orElse(null);
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getQuantity()).isEqualTo(5);
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    @DisplayName("Flow 5: Item can be removed from cart")
    void testRemoveFromCartFlow() {
        // Add product to cart
        Cart cart = cartRepository.findByUserId(testCustomer.getId()).orElse(null);
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(testProduct)
                .quantity(1)
                .price(testProduct.getPrice())
                .build();
        cartItem = cartItemRepository.save(cartItem);
        Long cartItemId = cartItem.getId();

        // Remove from cart
        cartItemRepository.deleteById(cartItemId);

        // Verify removal
        assertThat(cartItemRepository.findById(cartItemId)).isEmpty();
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    @DisplayName("Flow 6: Order can be created from cart")
    void testOrderCreationFlow() {
        // Add items to cart
        Cart cart = cartRepository.findByUserId(testCustomer.getId()).orElse(null);
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(testProduct)
                .quantity(1)
                .price(testProduct.getPrice())
                .build();
        cartItemRepository.save(cartItem);

        // Calculate order totals
        BigDecimal subtotal = testProduct.getPrice();
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.10")); // 10% tax
        BigDecimal shippingCost = new BigDecimal("10.00");
        BigDecimal total = subtotal.add(tax).add(shippingCost);

        // Create order
        works.jayesh.demo.order.model.entity.Order order = works.jayesh.demo.order.model.entity.Order.builder()
                .orderNumber("ORD-" + System.currentTimeMillis())
                .user(testCustomer)
                .status(OrderStatus.PENDING)
                .subtotal(subtotal)
                .tax(tax)
                .shippingCost(shippingCost)
                .totalAmount(total)
                .shippingAddress(shippingAddress)
                .billingAddress(shippingAddress)
                .notes("Please deliver before noon")
                .build();
        order = orderRepository.save(order);

        // Verify order creation
        assertThat(order.getId()).isNotNull();
        assertThat(order.getOrderNumber()).isNotBlank();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotalAmount()).isGreaterThan(BigDecimal.ZERO);
        assertThat(order.getUser().getId()).isEqualTo(testCustomer.getId());
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    @DisplayName("Flow 7: Multiple items can be added to cart")
    void testMultipleItemsInCartFlow() {
        // Create second product
        Product product2 = Product.builder()
                .name("Laptop Pro")
                .sku("LAP-" + System.currentTimeMillis())
                .description("High-performance laptop")
                .price(new BigDecimal("1999.99"))
                .stockQuantity(30)
                .slug("laptop-" + System.currentTimeMillis())
                .category(testCategory)
                .seller(testSeller)
                .brand("TechBrand")
                .active(true)
                .featured(false)
                .status(ProductStatus.ACTIVE)
                .build();
        product2 = productRepository.save(product2);

        // Add both products to cart
        Cart cart = cartRepository.findByUserId(testCustomer.getId()).orElse(null);

        CartItem item1 = CartItem.builder()
                .cart(cart)
                .product(testProduct)
                .quantity(2)
                .price(testProduct.getPrice())
                .build();
        cartItemRepository.save(item1);

        CartItem item2 = CartItem.builder()
                .cart(cart)
                .product(product2)
                .quantity(1)
                .price(product2.getPrice())
                .build();
        cartItemRepository.save(item2);

        // Verify cart has both items
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        assertThat(items).hasSize(2);

        // Verify cart total
        BigDecimal expectedTotal = testProduct.getPrice().multiply(new BigDecimal(2))
                .add(product2.getPrice());
        BigDecimal actualTotal = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(actualTotal).isEqualByComparingTo(expectedTotal);
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    @DisplayName("Flow 8: User's order history can be retrieved")
    void testOrderHistoryFlow() {
        // Create multiple orders
        for (int i = 0; i < 3; i++) {
            works.jayesh.demo.order.model.entity.Order order = works.jayesh.demo.order.model.entity.Order.builder()
                    .orderNumber("ORD-" + System.currentTimeMillis() + "-" + i)
                    .user(testCustomer)
                    .status(OrderStatus.PENDING)
                    .subtotal(new BigDecimal("100.00"))
                    .tax(new BigDecimal("10.00"))
                    .shippingCost(new BigDecimal("5.00"))
                    .totalAmount(new BigDecimal("115.00"))
                    .shippingAddress(shippingAddress)
                    .billingAddress(shippingAddress)
                    .build();
            orderRepository.save(order);
        }

        // Retrieve order history
        Pageable pageable = PageRequest.of(0, 10);
        Page<works.jayesh.demo.order.model.entity.Order> userOrders = orderRepository.findByUserId(testCustomer.getId(),
                pageable);

        assertThat(userOrders.getContent()).hasSizeGreaterThanOrEqualTo(3);
        assertThat(userOrders.getContent()).allMatch(o -> o.getUser().getId().equals(testCustomer.getId()));
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    @DisplayName("Flow 9: Address can be added and used in order")
    void testAddressManagementFlow() {
        // Create new address
        Address workAddress = Address.builder()
                .user(testCustomer)
                .fullName("John Customer")
                .phoneNumber("5551234567")
                .addressLine1("456 Office Park")
                .city("Los Angeles")
                .state("CA")
                .country("USA")
                .postalCode("90001")
                .type(AddressType.WORK)
                .isDefault(false)
                .build();
        workAddress = addressRepository.save(workAddress);

        // Verify address was saved
        assertThat(workAddress.getId()).isNotNull();

        // Use new address in order
        works.jayesh.demo.order.model.entity.Order order = works.jayesh.demo.order.model.entity.Order.builder()
                .orderNumber("ORD-" + System.currentTimeMillis())
                .user(testCustomer)
                .status(OrderStatus.PENDING)
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("10.00"))
                .shippingCost(new BigDecimal("5.00"))
                .totalAmount(new BigDecimal("115.00"))
                .shippingAddress(workAddress)
                .billingAddress(shippingAddress)
                .build();
        order = orderRepository.save(order);

        // Verify order uses correct addresses
        works.jayesh.demo.order.model.entity.Order savedOrder = orderRepository.findById(order.getId()).orElse(null);
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getShippingAddress().getType()).isEqualTo(AddressType.WORK);
        assertThat(savedOrder.getBillingAddress().getType()).isEqualTo(AddressType.HOME);
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    @DisplayName("Flow 10: Product stock validation")
    void testStockValidationFlow() {
        // Check product has stock
        Product product = productRepository.findById(testProduct.getId()).orElse(null);
        assertThat(product).isNotNull();
        assertThat(product.getStockQuantity()).isGreaterThan(0);

        // Simulate reducing stock
        int originalStock = product.getStockQuantity();
        int orderQuantity = 5;
        product.setStockQuantity(originalStock - orderQuantity);
        product = productRepository.save(product);

        // Verify stock was reduced
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElse(null);
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(originalStock - orderQuantity);
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    @DisplayName("Flow 11: Database indexes performance test")
    void testDatabaseIndexesFlow() {
        // This test verifies that indexed queries work correctly

        // Test user_id index on orders
        Pageable pageable = PageRequest.of(0, 10);
        Page<works.jayesh.demo.order.model.entity.Order> ordersByUser = orderRepository
                .findByUserId(testCustomer.getId(), pageable);
        assertThat(ordersByUser).isNotNull();

        // Test category_id index on products
        Page<Product> productsByCategory = productRepository.findByCategoryId(testCategory.getId(), pageable);
        assertThat(productsByCategory).isNotEmpty();

        // Test slug index on products
        Product productBySlug = productRepository.findBySlug(testProduct.getSlug()).orElse(null);
        assertThat(productBySlug).isNotNull();
        assertThat(productBySlug.getId()).isEqualTo(testProduct.getId());
    }

    @Test
    @org.junit.jupiter.api.Order(12)
    @DisplayName("Flow 12: Complete purchase flow end-to-end")
    void testCompletePurchaseFlow() {
        // Step 1: Customer browses products
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = productRepository.findByCategoryId(testCategory.getId(), pageable);
        assertThat(products.getContent()).isNotEmpty();
        Product selectedProduct = products.getContent().get(0);

        // Step 2: Add to cart
        Cart cart = cartRepository.findByUserId(testCustomer.getId()).orElse(null);
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(selectedProduct)
                .quantity(2)
                .price(selectedProduct.getPrice())
                .build();
        cartItem = cartItemRepository.save(cartItem);
        assertThat(cartItem.getId()).isNotNull();

        // Step 3: Review cart
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        assertThat(cartItems).isNotEmpty();

        // Step 4: Calculate totals
        BigDecimal subtotal = cartItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.10"));
        BigDecimal shipping = new BigDecimal("10.00");
        BigDecimal total = subtotal.add(tax).add(shipping);

        // Step 5: Create order
        works.jayesh.demo.order.model.entity.Order order = works.jayesh.demo.order.model.entity.Order.builder()
                .orderNumber("ORD-" + System.currentTimeMillis())
                .user(testCustomer)
                .status(OrderStatus.PENDING)
                .subtotal(subtotal)
                .tax(tax)
                .shippingCost(shipping)
                .totalAmount(total)
                .shippingAddress(shippingAddress)
                .billingAddress(shippingAddress)
                .build();
        order = orderRepository.save(order);

        // Step 6: Verify order was created
        assertThat(order.getId()).isNotNull();
        assertThat(order.getOrderNumber()).startsWith("ORD-");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);

        // Step 7: Clear cart after order
        cartItemRepository.deleteAll(cartItems);
        List<CartItem> emptyCart = cartItemRepository.findByCartId(cart.getId());
        assertThat(emptyCart).isEmpty();
    }
}
