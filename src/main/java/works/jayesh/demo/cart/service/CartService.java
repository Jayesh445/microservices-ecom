package works.jayesh.demo.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import works.jayesh.demo.cart.model.dto.AddToCartRequest;
import works.jayesh.demo.cart.model.dto.CartItemResponse;
import works.jayesh.demo.cart.model.dto.CartResponse;
import works.jayesh.demo.cart.model.entity.Cart;
import works.jayesh.demo.cart.model.entity.CartItem;
import works.jayesh.demo.cart.repository.CartItemRepository;
import works.jayesh.demo.cart.repository.CartRepository;
import works.jayesh.demo.common.exception.InsufficientStockException;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.product.model.entity.Product;
import works.jayesh.demo.product.repository.ProductRepository;
import works.jayesh.demo.user.model.entity.User;
import works.jayesh.demo.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        log.info("Adding product {} to cart for user {}", request.getProductId(), userId);

        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();

            if (product.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .price(product.getEffectivePrice())
                    .build();

            cart.getItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }

        log.info("Product added to cart successfully");
        return mapToResponse(cart);
    }

    public CartResponse updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        log.info("Updating cart item quantity for user {} and product {}", userId, productId);

        Cart cart = getCartByUserId(userId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        Product product = cartItem.getProduct();

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            cart.getItems().remove(cartItem);
        } else {
            if (product.getStockQuantity() < quantity) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        log.info("Cart item quantity updated successfully");
        return mapToResponse(cart);
    }

    public CartResponse removeFromCart(Long userId, Long productId) {
        log.info("Removing product {} from cart for user {}", productId, userId);

        Cart cart = getCartByUserId(userId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        log.info("Product removed from cart successfully");
        return mapToResponse(cart);
    }

    public void clearCart(Long userId) {
        log.info("Clearing cart for user {}", userId);

        Cart cart = getCartByUserId(userId);
        cart.getItems().clear();
        cartItemRepository.deleteAll(cartItemRepository.findByCartId(cart.getId()));

        log.info("Cart cleared successfully");
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        return mapToResponse(cart);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();

                    return cartRepository.save(newCart);
                });
    }

    private Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
    }

    private CartResponse mapToResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(cart.getItems().stream().map(this::mapItemToResponse).toList())
                .totalAmount(cart.getTotalAmount())
                .totalItems(cart.getTotalItems())
                .build();
    }

    private CartItemResponse mapItemToResponse(CartItem item) {
        Product product = item.getProduct();
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getImages().isEmpty() ? null : product.getImages().get(0))
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .inStock(product.isInStock())
                .build();
    }
}
