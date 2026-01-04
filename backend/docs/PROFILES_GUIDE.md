# Quick Start Guide - Dev & Prod Profiles

## 🚀 Quick Start

### Development Mode (Recommended for Testing)

```bash
# Simply run - OTP will appear in console!
./mvnw spring-boot:run
```

**What you get:**

- ✅ **OTP in console** - No email configuration needed!
- ✅ Detailed SQL queries
- ✅ Auto-create database tables
- ✅ Debug logging everywhere

### Production Mode

```bash
# Set environment variables first
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://localhost:5432/ecommerce_db
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password

./mvnw spring-boot:run
```

---

## 📁 Configuration Files

### `application.yml` (Common Config)

- JWT settings
- Server port
- File upload limits

### `application-dev.yml` (Development)

- Hardcoded database credentials
- OTP in console: `app.otp.console-output: true`
- Verbose logging (DEBUG)
- Show formatted SQL

### `application-prod.yml` (Production)

- Environment variable configuration
- OTP via email only
- Minimal logging (INFO/WARN)
- No SQL output
- Log files with rotation

---

## 🔧 How to Switch Profiles

### Method 1: Maven Command

```bash
# Development
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Production
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Method 2: Environment Variable

```bash
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
```

### Method 3: application.yml Default

```yaml
spring:
  profiles:
    active: dev # Default profile
```

### Method 4: IDE (IntelliJ IDEA)

1. Edit Run Configuration
2. Add VM options: `-Dspring.profiles.active=dev`
3. Run

---

## 🧪 Testing OTP in Development Mode

### Step 1: Start Application

```bash
./mvnw spring-boot:run
```

### Step 2: Request OTP

```bash
curl -X POST http://localhost:8080/api/auth/register/request-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'
```

### Step 3: Check Console

You'll see:

```
╔════════════════════════════════════════╗
║        DEVELOPMENT MODE - OTP          ║
╠════════════════════════════════════════╣
║  Email: test@example.com               ║
║  OTP:   123456                         ║
║  Valid for: 5 minutes                  ║
╚════════════════════════════════════════╝
```

### Step 4: Use OTP

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

---

## 🔑 Key Differences: Dev vs Prod

| Feature             | Development        | Production                |
| ------------------- | ------------------ | ------------------------- |
| **OTP Location**    | Console + Email    | Email Only                |
| **SQL Logging**     | Verbose, Formatted | Disabled                  |
| **Log Level**       | DEBUG              | INFO/WARN                 |
| **Database Schema** | Auto-create/update | Validate only             |
| **Error Details**   | Full stack traces  | Minimal                   |
| **Configuration**   | Hardcoded (easy)   | Environment vars (secure) |

---

## 📧 Email Configuration (Production Only)

For Gmail:

1. Enable 2-Step Verification
2. Generate App Password: https://myaccount.google.com/apppasswords
3. Set environment variables:

```bash
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-16-digit-app-password
```

For other email providers, update `application-prod.yml`:

```yaml
spring:
  mail:
    host: smtp.yourprovider.com
    port: 587
```

---

## 🐛 Troubleshooting

### OTP not showing in console (Dev mode)?

- Check active profile: Look for "The following profiles are active: dev"
- Check application-dev.yml: `app.otp.console-output: true`

### Email not sending (Prod mode)?

- Verify email credentials
- Check firewall/network allows SMTP (port 587)
- Look for email errors in logs

### Database connection failed?

- Ensure PostgreSQL is running
- Check database `ecommerce_db` exists
- Verify credentials in application-\*.yml

---

## 🎯 Best Practices

### Development

- Use dev profile for local testing
- Don't worry about email configuration
- Check console for OTP codes
- Use hardcoded database credentials

### Production

- Always use prod profile
- Never hardcode secrets
- Use environment variables
- Enable email for OTP delivery
- Monitor log files

---

## 📚 Related Documentation

- [PROJECT_DOCUMENTATION.md](PROJECT_DOCUMENTATION.md) - Complete guide with all annotations explained
- [AUTHENTICATION.md](AUTHENTICATION.md) - API documentation for authentication endpoints
- [README.md](README.md) - General project overview

---

**Pro Tip:** Start with dev mode to test everything locally, then switch to prod mode when deploying! 🚀
