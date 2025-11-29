# 📚 Complete Documentation Index

Welcome to the E-Commerce Application documentation! This guide will help you navigate all the documentation files.

---

## 🚀 Quick Start Documents

### 1. [README.md](README.md)

**Start here!** General project overview and quick setup.

- ✅ Project features overview
- ✅ Tech stack summary
- ✅ Quick start guide (5 minutes to run!)
- ✅ Basic API endpoint list
- ✅ Build & deployment instructions

**Best for:** First-time users, project overview

---

### 2. [PROFILES_GUIDE.md](PROFILES_GUIDE.md)

**Essential for development!** Understanding dev vs prod modes.

- ✅ How to run in development mode
- ✅ How to run in production mode
- ✅ OTP in console for dev mode
- ✅ Profile switching methods
- ✅ Configuration differences
- ✅ Email setup guide
- ✅ Troubleshooting tips

**Best for:** Setting up your development environment

---

## 📖 Comprehensive Guides

### 3. [PROJECT_DOCUMENTATION.md](PROJECT_DOCUMENTATION.md)

**Complete reference!** Everything about the project architecture.

- ✅ Architecture & design patterns
- ✅ All annotations explained in detail
- ✅ Configuration files breakdown
- ✅ Profile management deep dive
- ✅ Security implementation
- ✅ Module-by-module breakdown
- ✅ Running the application
- ✅ Testing guide
- ✅ Best practices

**Best for:** Understanding the entire project structure and architecture

---

### 4. [ANNOTATIONS_GUIDE.md](ANNOTATIONS_GUIDE.md)

**Annotation reference!** Every annotation used in the project.

- ✅ Spring Core (@Configuration, @Bean, @Component, etc.)
- ✅ Dependency Injection (@Autowired, @RequiredArgsConstructor)
- ✅ JPA/Hibernate (@Entity, @OneToMany, @ManyToOne, etc.)
- ✅ Validation (@Valid, @NotBlank, @Email, etc.)
- ✅ Lombok (@Data, @Builder, @Slf4j, etc.)
- ✅ Security (@PreAuthorize, @EnableWebSecurity)
- ✅ REST API (@RestController, @GetMapping, etc.)
- ✅ Async & Transactions (@Async, @Transactional)

**Best for:** Learning Spring Boot annotations and when to use them

---

### 5. [AUTHENTICATION.md](AUTHENTICATION.md)

**Authentication API reference!** JWT and OTP authentication.

- ✅ JWT & OTP overview
- ✅ Configuration details
- ✅ All authentication endpoints
- ✅ Request/response examples
- ✅ Authentication flows
- ✅ Security features
- ✅ Testing instructions
- ✅ Project structure

**Best for:** Implementing authentication in your frontend or API testing

---

### 6. [FLOW_DIAGRAMS.md](FLOW_DIAGRAMS.md)

**Visual guide!** ASCII diagrams showing how things work.

- ✅ Application startup flow
- ✅ Request processing flow
- ✅ JWT authentication flow
- ✅ OTP registration flow
- ✅ Order creation flow
- ✅ Profile selection flow
- ✅ Annotation processing flow

**Best for:** Understanding how components interact at runtime

---

## 📋 Documentation by Use Case

### I want to...

#### ...get started quickly

1. Read [README.md](README.md) - Quick overview
2. Read [PROFILES_GUIDE.md](PROFILES_GUIDE.md) - Setup dev environment
3. Run: `./mvnw spring-boot:run`
4. Test registration with OTP (check console for OTP!)

---

#### ...understand the architecture

1. Read [PROJECT_DOCUMENTATION.md](PROJECT_DOCUMENTATION.md) - Complete guide
2. Read [FLOW_DIAGRAMS.md](FLOW_DIAGRAMS.md) - Visual representation
3. Explore module breakdown section

---

#### ...learn Spring Boot annotations

1. Read [ANNOTATIONS_GUIDE.md](ANNOTATIONS_GUIDE.md) - All annotations explained
2. Reference while reading code
3. Copy examples for your own projects

---

#### ...implement authentication in my frontend

1. Read [AUTHENTICATION.md](AUTHENTICATION.md) - API documentation
2. See request/response examples
3. Test with curl/Postman
4. Implement in your frontend (React/Angular/Vue)

---

#### ...deploy to production

1. Read [PROFILES_GUIDE.md](PROFILES_GUIDE.md) - Production configuration
2. Read [PROJECT_DOCUMENTATION.md](PROJECT_DOCUMENTATION.md) - Security best practices
3. Set environment variables
4. Run with prod profile: `java -jar -Dspring.profiles.active=prod app.jar`

---

#### ...understand how JWT works

1. Read [AUTHENTICATION.md](AUTHENTICATION.md) - JWT overview
2. Read [FLOW_DIAGRAMS.md](FLOW_DIAGRAMS.md) - JWT authentication flow
3. Check JwtTokenProvider.java code
4. Test token generation/validation

---

#### ...debug OTP issues

1. Read [PROFILES_GUIDE.md](PROFILES_GUIDE.md) - OTP configuration
2. Ensure dev profile is active
3. Check console for OTP output
4. Read troubleshooting section

---

#### ...understand database relationships

1. Read [ANNOTATIONS_GUIDE.md](ANNOTATIONS_GUIDE.md) - JPA section
2. Look at @OneToMany, @ManyToOne examples
3. Check entity classes (User, Order, Product)
4. See cascade and fetch type explanations

---

## 🎯 Documentation Roadmap

### Beginner Path

```
1. README.md (15 min)
   ↓
2. PROFILES_GUIDE.md (20 min)
   ↓
3. Test the application (30 min)
   ↓
4. AUTHENTICATION.md (30 min)
```

### Advanced Path

```
1. PROJECT_DOCUMENTATION.md (60 min)
   ↓
2. ANNOTATIONS_GUIDE.md (45 min)
   ↓
3. FLOW_DIAGRAMS.md (30 min)
   ↓
4. Explore source code with understanding
```

---

## 📁 Project Structure Reference

```
E-Commerce Application
│
├── README.md                      ← Start here
├── PROFILES_GUIDE.md              ← Dev vs Prod setup
├── PROJECT_DOCUMENTATION.md       ← Complete reference
├── ANNOTATIONS_GUIDE.md           ← Annotation reference
├── AUTHENTICATION.md              ← API documentation
├── FLOW_DIAGRAMS.md              ← Visual flows
└── DOCUMENTATION_INDEX.md        ← This file!

src/main/java/works/jayesh/demo/
├── auth/                          ← Authentication module
│   ├── controller/               ← REST endpoints
│   ├── service/                  ← Business logic
│   └── model/dto/                ← Data transfer objects
├── user/                          ← User management
├── product/                       ← Product catalog
├── order/                         ← Order processing
├── cart/                          ← Shopping cart
├── payment/                       ← Payment handling
├── address/                       ← Address management
├── review/                        ← Product reviews
├── common/                        ← Shared utilities
├── security/                      ← JWT & Security
│   ├── jwt/                      ← JWT token handling
│   └── service/                  ← User details service
└── config/                        ← Configuration classes

src/main/resources/
├── application.yml                ← Common config
├── application-dev.yml            ← Development config
└── application-prod.yml           ← Production config
```

---

## 🔍 Quick Reference

### Common Commands

```bash
# Development mode
./mvnw spring-boot:run

# Production mode
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Build JAR
./mvnw clean package

# Run JAR
java -jar target/ecommerce-app-0.0.1-SNAPSHOT.jar
```

### Common Endpoints

```
Authentication:
POST /api/auth/register/password
POST /api/auth/register/request-otp
POST /api/auth/login/password
POST /api/auth/refresh

Resources:
GET  /api/users
GET  /api/products
POST /api/orders
GET  /api/cart
```

### Configuration Keys

```yaml
# JWT
jwt.secret
jwt.access-token-expiration
jwt.refresh-token-expiration

# OTP
app.otp.console-output

# Database
spring.datasource.url
spring.datasource.username
spring.datasource.password

# Email
spring.mail.host
spring.mail.username
spring.mail.password
```

---

## 📞 Need Help?

1. **Setup Issues** → [PROFILES_GUIDE.md](PROFILES_GUIDE.md) Troubleshooting section
2. **API Questions** → [AUTHENTICATION.md](AUTHENTICATION.md)
3. **Architecture Questions** → [PROJECT_DOCUMENTATION.md](PROJECT_DOCUMENTATION.md)
4. **Annotation Questions** → [ANNOTATIONS_GUIDE.md](ANNOTATIONS_GUIDE.md)
5. **Flow Questions** → [FLOW_DIAGRAMS.md](FLOW_DIAGRAMS.md)

---

## 🎓 Learning Resources

### Spring Boot Official

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

### JWT

- [JWT.io](https://jwt.io/) - JWT introduction
- [RFC 7519](https://tools.ietf.org/html/rfc7519) - JWT specification

### PostgreSQL

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

## ✅ Checklist for New Developers

- [ ] Read README.md
- [ ] Setup PostgreSQL database
- [ ] Read PROFILES_GUIDE.md
- [ ] Run application in dev mode
- [ ] Test OTP registration (check console)
- [ ] Test password login
- [ ] Read AUTHENTICATION.md
- [ ] Test all auth endpoints with Postman
- [ ] Read PROJECT_DOCUMENTATION.md
- [ ] Explore module structure
- [ ] Read ANNOTATIONS_GUIDE.md
- [ ] Understand security configuration
- [ ] Read FLOW_DIAGRAMS.md
- [ ] Deploy to production (optional)

---

**Happy Learning! 🚀**

For questions or contributions, see [README.md](README.md)
