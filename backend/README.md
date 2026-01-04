# E-Commerce Application

A comprehensive Spring Boot e-commerce application with JWT authentication, OTP verification, and modular architecture.

## ✨ Features

- 🔐 **Dual Authentication**: Password-based & OTP-based (email)
- 🎫 **JWT Tokens**: Access & refresh token implementation
- 📧 **Email Integration**: Async email with OTP delivery
- 🏗️ **Modular Architecture**: 9 domain modules (User, Product, Order, Cart, Payment, etc.)
- 🔒 **Role-Based Access**: CUSTOMER, SELLER, ADMIN roles
- 🗄️ **PostgreSQL**: Full JPA/Hibernate integration
- 🚀 **Dev & Prod Profiles**: Separate configurations for development and production
- 📝 **Audit Trails**: Automatic timestamps on entities
- ✅ **Validation**: Comprehensive DTO validation

## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 4.0.1**
- **PostgreSQL 16**
- **JWT (jjwt 0.12.5)**
- **Spring Security**
- **Spring Mail**
- **Lombok**
- **Hibernate/JPA**

## 📋 Prerequisites

1. Java 21 or higher
2. PostgreSQL 16 or higher
3. Maven (included via wrapper)

## 🚀 Quick Start

### 1. Setup Database

```sql
CREATE DATABASE ecommerce_db;
```

### 2. Run in Development Mode (Recommended)

```bash
./mvnw spring-boot:run
```

**In dev mode:**

- ✅ **OTP appears in console** - No email configuration needed!
- ✅ SQL queries displayed
- ✅ Auto-create database tables
- ✅ Detailed debugging logs

### 3. Test Authentication

#### Request OTP:

```bash
curl -X POST http://localhost:8080/api/auth/register/request-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'
```

#### Check Console for OTP:

```
╔════════════════════════════════════════╗
║        DEVELOPMENT MODE - OTP          ║
╠════════════════════════════════════════╣
║  Email: test@example.com               ║
║  OTP:   123456                         ║
║  Valid for: 5 minutes                  ║
╚════════════════════════════════════════╝
```

#### Complete Registration:

```bash
curl -X POST http://localhost:8080/api/auth/register/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email":"test@example.com",
    "firstName":"John",
    "lastName":"Doe",
    "otp":"123456"
  }'
```

## 🌍 Environments

### Development Mode (Default)

```bash
./mvnw spring-boot:run
```

- OTP in console
- Verbose logging
- Auto-create tables

### Production Mode

```bash
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://prod-host:5432/ecommerce_db
export DATABASE_USERNAME=prod_user
export DATABASE_PASSWORD=prod_password
export MAIL_USERNAME=noreply@company.com
export MAIL_PASSWORD=app_password

./mvnw spring-boot:run
```

- OTP via email only
- Minimal logging
- Environment-based configuration

## 📚 Documentation

- **[PROFILES_GUIDE.md](PROFILES_GUIDE.md)** - Quick start guide for dev & prod profiles
- **[PROJECT_DOCUMENTATION.md](PROJECT_DOCUMENTATION.md)** - Complete documentation with all annotations explained
- **[AUTHENTICATION.md](AUTHENTICATION.md)** - API documentation for authentication endpoints

## 🔑 API Endpoints

### Authentication

- `POST /api/auth/register/password` - Register with password
- `POST /api/auth/register/request-otp` - Request OTP for registration
- `POST /api/auth/register/verify-otp` - Verify OTP and complete registration
- `POST /api/auth/login/password` - Login with password
- `POST /api/auth/login/request-otp` - Request OTP for login
- `POST /api/auth/login/verify-otp` - Verify OTP and login
- `POST /api/auth/refresh` - Refresh access token

### Users

- `GET /api/users` - List users (Admin)
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin)

### Products

- `GET /api/products` - List products
- `GET /api/products/{id}` - Get product details
- `POST /api/products` - Create product (Seller/Admin)
- `PUT /api/products/{id}` - Update product (Seller/Admin)
- `DELETE /api/products/{id}` - Delete product (Admin)

### Orders

- `POST /api/orders` - Create order
- `GET /api/orders` - List user orders
- `GET /api/orders/{id}` - Get order details
- `PUT /api/orders/{id}/status` - Update order status (Admin)

### Cart

- `GET /api/cart` - Get user cart
- `POST /api/cart/items` - Add item to cart
- `PUT /api/cart/items/{id}` - Update cart item
- `DELETE /api/cart/items/{id}` - Remove cart item
- `DELETE /api/cart` - Clear cart

_(See AUTHENTICATION.md for complete API documentation)_

## 🏗️ Project Structure

```
src/main/java/works/jayesh/demo/
├── auth/              # Authentication & OTP
├── user/              # User management
├── product/           # Product catalog
├── order/             # Order processing
├── cart/              # Shopping cart
├── payment/           # Payment handling
├── address/           # Address management
├── review/            # Product reviews
├── common/            # Shared utilities
├── security/          # JWT & Security
└── config/            # Configuration classes

src/main/resources/
├── application.yml         # Common config
├── application-dev.yml     # Development config
└── application-prod.yml    # Production config
```

## 🔐 Security

- JWT-based authentication (stateless)
- Access tokens: 15 minutes
- Refresh tokens: 7 days
- BCrypt password hashing
- Role-based access control
- OTP: 6-digit, 5-minute expiry

## 📦 Building for Production

```bash
# Build JAR
./mvnw clean package

# Run
java -jar -Dspring.profiles.active=prod target/ecommerce-app-0.0.1-SNAPSHOT.jar
```

## 🧪 Testing

See console logs for OTP in development mode. No email configuration needed for local testing!

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📝 License

This project is licensed under the MIT License.

## 👨‍💻 Author

Jayesh - [jayeshgupta.eng@gmail.com](mailto:jayeshgupta.eng@gmail.com)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- PostgreSQL for the robust database
- JWT.io for token implementation guidance

---

**Happy Coding! 🚀**

For detailed documentation on annotations and architecture, see [PROJECT_DOCUMENTATION.md](PROJECT_DOCUMENTATION.md)
